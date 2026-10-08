package dev.joid.impl.minecraft.render.shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.joid.impl.minecraft.JoidMinecraft;
import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.impl.minecraft.render.shader.uniform.BooleanUniform;
import dev.joid.impl.minecraft.render.shader.uniform.Float2Uniform;
import dev.joid.impl.minecraft.render.shader.uniform.Float3Uniform;
import dev.joid.impl.minecraft.render.shader.uniform.Float4ArrayUniform;
import dev.joid.impl.minecraft.render.shader.uniform.Float4Uniform;
import dev.joid.impl.minecraft.render.shader.uniform.FloatArrayUniform;
import dev.joid.impl.minecraft.render.shader.uniform.FloatMatrixUniform;
import dev.joid.impl.minecraft.render.shader.uniform.FloatUniform;
import dev.joid.impl.minecraft.render.shader.uniform.IntUniform;
import dev.joid.impl.minecraft.render.shader.uniform.SamplerUniform;
import dev.joid.impl.minecraft.render.shader.uniform.UniformBlock;
import dev.joid.impl.minecraft.render.shader.uniform.UniformMember;
import dev.joid.impl.minecraft.render.state.PipelineKey;
import dev.joid.impl.minecraft.render.stencil.StencilEmulation;
import dev.joid.impl.minecraft.render.texture.Texture;
import dev.joid.lib.bridge.render.matrix.MatrixStack;
import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.resources.Identifier;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Shader implements IShader {

	private static int count;

	private final RenderBridge                bridge;
	private final BlendState                  blend;
	private final Identifier                  identifier;
	private final Identifier                  stencilIdentifier;
	private final UniformBlock                block;
	private final List<String>                samplerList;
	private final BindGroupLayout             layout;
	private final Map<String, SamplerUniform> samplerMap;

	private boolean    bound;
	private boolean    active;
	private BlendState previousBlend;

	public static @NonNull Shader create(final @NonNull RenderBridge bridge, final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		final String path = "shader/" + Shader.count++;
		final List<String> samplerList = ShaderTranslator.getSamplers(vertex, fragment);
		final BindGroupLayout.Builder layout = BindGroupLayout.builder().withUniform(ShaderTranslator.BLOCK, UniformType.UNIFORM_BUFFER);
		for (final String sampler : samplerList) {
			layout.withSampler(sampler);
		}
		layout.withSampler(ShaderTranslator.STENCIL);

		final Shader shader = new Shader(bridge, blend, Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, path), Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, path + "_stencil"), UniformBlock.create(ShaderTranslator.getUniforms(vertex, fragment), vertex.getBody() + fragment.getBody()), samplerList, layout.build(), new HashMap<>());
		bridge.getSourceProvider().register(shader.identifier, ShaderType.VERTEX, ShaderTranslator.translateVertex(vertex, fragment));
		bridge.getSourceProvider().register(shader.identifier, ShaderType.FRAGMENT, ShaderTranslator.translateFragment(vertex, fragment));
		bridge.getSourceProvider().register(shader.stencilIdentifier, ShaderType.FRAGMENT, ShaderTranslator.translateStencil(vertex, fragment));

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
		final UniformMember projectionMatrix = this.block.getMember("uProjectionMatrix");
		if (projectionMatrix != null) {
			projectionMatrix.putMatrix(projection);
		}

		final UniformMember modelViewMatrix = this.block.getMember("uModelViewMatrix");
		if (modelViewMatrix != null) {
			modelViewMatrix.putMatrix(modelView.getMatrix());
		}

		final UniformMember normalMatrix = this.block.getMember("uNormalMatrix");
		if (normalMatrix != null) {
			normalMatrix.putMatrix(modelView.getNormalMatrix());
		}

		final UniformMember lighting = this.block.getMember("uLighting");
		if (lighting != null) {
			lighting.putInt(state.isLighting() ? 1 : 0);
		}

		this.block.getMember("joid_AlphaTest").putInt(state.isAlphaTest() ? 1 : 0);
		this.block.getMember("joid_AlphaThreshold").putFloats(state.getAlphaThreshold());
		stencil.write(this.block);
		return this.bridge.getDevice().createCommandEncoder().transientMemory().uploadGpu(this.block.getData().slice(0, this.block.getSize()), this.bridge.getDevice().getDeviceInfo().limits().minUniformOffsetAlignment(), GpuBuffer.USAGE_UNIFORM);
	}

	public void apply(final @NonNull RenderPass pass, final @NonNull RenderState state, final @NonNull GpuBufferSlice uniforms, final @NonNull GpuTextureView stencil) {
		pass.setUniform(ShaderTranslator.BLOCK, uniforms);
		for (final String name : this.samplerList) {
			final SamplerUniform sampler = this.samplerMap.get(name);
			final Texture stateTexture = (Texture) state.getTexture();
			if (sampler != null && sampler.getTexture() != null && ((Texture) sampler.getTexture()).getView() != null) {
				final Texture texture = (Texture) sampler.getTexture();
				pass.bindTexture(name, texture.getView(), RenderBridge.getSampler(sampler.getFilter(), sampler.getWrap(), texture.isMipmapped()));
			} else if (stateTexture != null && stateTexture.getView() != null) {
				pass.bindTexture(name, stateTexture.getView(), RenderBridge.getSampler(state.getTextureFilter(), state.getTextureWrap(), stateTexture.isMipmapped()));
			} else {
				pass.bindTexture(name, this.bridge.getEmptyTexture().getView(), RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.REPEAT, false));
			}
		}

		pass.bindTexture(ShaderTranslator.STENCIL, stencil, RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE, false));
	}

	@Override
	public @NonNull IntUniform getIntUniform(final @NonNull String name) {
		return new IntUniform(this.block.getMember(name));
	}

	@Override
	public @NonNull FloatUniform getFloatUniform(final @NonNull String name) {
		return new FloatUniform(this.block.getMember(name));
	}

	@Override
	public @NonNull Float2Uniform getFloat2Uniform(final @NonNull String name) {
		return new Float2Uniform(this.block.getMember(name));
	}

	@Override
	public @NonNull Float3Uniform getFloat3Uniform(final @NonNull String name) {
		return new Float3Uniform(this.block.getMember(name));
	}

	@Override
	public @NonNull Float4Uniform getFloat4Uniform(final @NonNull String name) {
		return new Float4Uniform(this.block.getMember(name));
	}

	@Override
	public @NonNull BooleanUniform getBooleanUniform(final @NonNull String name) {
		return new BooleanUniform(this.block.getMember(name));
	}

	@Override
	public @NonNull SamplerUniform getSamplerUniform(final @NonNull String name) {
		return this.samplerMap.computeIfAbsent(name, _ -> new SamplerUniform());
	}

	@Override
	public @NonNull FloatArrayUniform getFloatArrayUniform(final @NonNull String name) {
		return new FloatArrayUniform(this.block.getMember(name));
	}

	@Override
	public @NonNull Float4ArrayUniform getFloat4ArrayUniform(final @NonNull String name) {
		return new Float4ArrayUniform(this.block.getMember(name));
	}

	@Override
	public @NonNull FloatMatrixUniform getFloatMatrixUniform(final @NonNull String name) {
		return new FloatMatrixUniform(this.block.getMember(name));
	}

}