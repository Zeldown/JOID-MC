package dev.joid.impl.minecraft.render.shader;

import java.util.List;

import dev.joid.impl.minecraft.JoidMinecraft;
import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.impl.minecraft.render.state.PipelineKey;
import dev.joid.impl.minecraft.render.stencil.StencilEmulation;
import dev.joid.impl.minecraft.render.texture.IGpuTexture;
import dev.joid.impl.minecraft.render.vertex.VertexLayout;
import dev.joid.lib.bridge.render.matrix.MatrixStack;
import dev.joid.lib.bridge.render.shader.source.BlockShaderTranslator;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import dev.joid.lib.bridge.render.shader.uniform.UniformBlock;
import dev.joid.lib.bridge.render.shader.uniform.UniformSampler;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.Identifier;

@Getter
public final class Shader extends dev.joid.lib.bridge.render.shader.Shader {

	private static int count;

	private final RenderBridge    bridge;
	private final BlendState      blend;
	private final Identifier      identifier;
	private final Identifier      stencilIdentifier;
	private final BindGroupLayout layout;
	private final VertexFormat    vertexFormat;

	private boolean    bound;
	private boolean    active;
	private BlendState previousBlend;

	private Shader(final RenderBridge bridge, final BlendState blend, final UniformBlock block, final List<ShaderVariable> samplers, final VertexFormat vertexFormat, final String path) {
		super(block, samplers);
		final BindGroupLayout.Builder layout = BindGroupLayout.builder().withUniform(BlockShaderTranslator.BLOCK, UniformType.UNIFORM_BUFFER);
		for (final ShaderVariable sampler : samplers) {
			layout.withSampler(sampler.getName());
		}
		layout.withSampler(ShaderTranslator.STENCIL);

		this.bridge            = bridge;
		this.blend             = blend;
		this.identifier        = Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, path);
		this.stencilIdentifier = Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, path + "_stencil");
		this.layout            = layout.build();
		this.vertexFormat      = vertexFormat;
	}

	public static @NonNull Shader create(final @NonNull RenderBridge bridge, final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		final ShaderTranslator translator = ShaderTranslator.create();
		final Shader shader = new Shader(bridge, blend, translator.createBlock(vertex, fragment), translator.getSamplers(vertex, fragment), VertexLayout.create(vertex.getBuiltins()), "shader/" + Shader.count++);
		bridge.getSourceProvider().register(shader.identifier, ShaderType.VERTEX, translator.translateVertex(vertex, fragment));
		bridge.getSourceProvider().register(shader.identifier, ShaderType.FRAGMENT, translator.translateFragment(vertex, fragment));
		bridge.getSourceProvider().register(shader.stencilIdentifier, ShaderType.FRAGMENT, ShaderTranslator.createStencil().translateFragment(vertex, fragment));

		final RenderState state = new RenderState();
		state.setBlend(blend);
		shader.active = bridge.getPipelineCache().get(PipelineKey.create(shader, state, false)) != null;
		return shader;
	}

	@Override
	public void bind() {
		this.previousBlend = this.bridge.getState().getBlend();
		this.bridge.shader(this);
		this.bridge.blend(this.blend);
		this.bound = true;
	}

	@Override
	public void unbind() {
		this.bridge.shader(null);
		if (this.previousBlend != null) {
			this.bridge.blend(this.previousBlend);
			this.previousBlend = null;
		}

		this.bound = false;
	}

	public @NonNull GpuBufferSlice upload(final @NonNull RenderState state, final @NonNull float[] projection, final @NonNull MatrixStack modelView, final @NonNull StencilEmulation stencil) {
		stencil.write(super.builtins(state, projection, modelView));
		super.getBlock().pack();
		return this.bridge.getDevice().createCommandEncoder().transientMemory().uploadGpu(super.getBlock().getData().slice(0, super.getBlock().getSize()), this.bridge.getDevice().getDeviceInfo().limits().minUniformOffsetAlignment(), GpuBuffer.USAGE_UNIFORM);
	}

	public void apply(final @NonNull RenderPass pass, final @NonNull RenderState state, final @NonNull GpuBufferSlice uniforms, final @NonNull GpuTextureView stencil) {
		pass.setUniform(BlockShaderTranslator.BLOCK, uniforms);
		for (final UniformSampler sampler : super.getSamplerMap().values()) {
			final IGpuTexture samplerTexture = (IGpuTexture) sampler.getTexture();
			final IGpuTexture stateTexture = (IGpuTexture) state.getTexture();
			if (samplerTexture != null && samplerTexture.getView() != null) {
				pass.bindTexture(sampler.getName(), samplerTexture.getView(), RenderBridge.getSampler(sampler.getFilter(), sampler.getWrap(), samplerTexture.isMipmapped()));
			} else if (stateTexture != null && stateTexture.getView() != null) {
				pass.bindTexture(sampler.getName(), stateTexture.getView(), RenderBridge.getSampler(state.getTextureFilter(), state.getTextureWrap(), stateTexture.isMipmapped()));
			} else {
				pass.bindTexture(sampler.getName(), this.bridge.getEmptyTexture().getView(), RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.REPEAT, false));
			}
		}

		pass.bindTexture(ShaderTranslator.STENCIL, stencil, RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE, false));
	}

}