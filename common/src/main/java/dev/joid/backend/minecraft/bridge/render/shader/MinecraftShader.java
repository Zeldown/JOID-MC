package dev.joid.backend.minecraft.bridge.render.shader;

import java.util.Collection;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.vertex.VertexLayout;
import dev.joid.lib.bridge.render.matrix.MatrixStack;
import dev.joid.lib.bridge.render.shader.Shader;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderTranslation;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.state.StencilEmulation;
import dev.joid.lib.bridge.render.vertex.VertexBuffer;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.Identifier;

public final class MinecraftShader extends Shader {

	private static int count;

	private final GpuDevice device;

	@Getter private Identifier      identifier;
	@Getter private BindGroupLayout layout;
	@Getter private VertexFormat    vertexFormat;
	@Getter private Identifier      stencilIdentifier;

	private Boolean active;

	private MinecraftShader(final MinecraftRenderBridge bridge, final ShaderSource vertex, final ShaderSource fragment, final BlendState blend) {
		super(bridge, MinecraftShaderTranslator.create().stencil(StencilEmulation.Pass.TEST), vertex, fragment, blend);
		this.device = bridge.getDevice();
	}

	public static @NonNull MinecraftShader create(final @NonNull MinecraftRenderBridge bridge, final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		return new MinecraftShader(bridge, vertex, fragment, blend);
	}

	@Override
	public boolean isActive() {
		if (this.active == null) {
			this.active = ((MinecraftRenderBridge) super.getBridge()).getPipelineCache().isValid(this.identifier, this.layout, this.vertexFormat, super.getBlend());
		}

		return this.active;
	}

	public @NonNull GpuBufferSlice upload(final @NonNull RenderState state, final @NonNull VertexBuffer buffer, final @NonNull float[] projection, final @NonNull MatrixStack modelView, final @NonNull StencilEmulation stencil) {
		stencil.write(super.builtins(state, buffer, projection, modelView));
		super.getBlock().pack();
		return this.device.createCommandEncoder().transientMemory().uploadGpu(super.getBlock().getData().slice(0, super.getBlock().getSize()), this.device.getDeviceInfo().limits().minUniformOffsetAlignment(), GpuBuffer.USAGE_UNIFORM);
	}

	@Override
	protected void compileProgram(final @NonNull ShaderTranslation translation) {
		final MinecraftRenderBridge bridge = (MinecraftRenderBridge) super.getBridge();
		this.identifier        = Identifier.fromNamespaceAndPath(Backend.MOD_ID, "shader/" + MinecraftShader.count++);
		this.stencilIdentifier = Identifier.fromNamespaceAndPath(Backend.MOD_ID, this.identifier.getPath() + "_stencil");
		this.layout            = MinecraftShader.createLayout(super.getSamplerMap().keySet());
		this.vertexFormat      = VertexLayout.create(super.getVertex().getBuiltins());
		bridge.getSourceProvider().register(this.identifier, ShaderType.VERTEX, translation.getVertex());
		bridge.getSourceProvider().register(this.identifier, ShaderType.FRAGMENT, translation.getFragment());
		bridge.getSourceProvider().register(this.stencilIdentifier, ShaderType.FRAGMENT, translation.getStencilFragment());
	}

	private static BindGroupLayout createLayout(final Collection<String> samplers) {
		final BindGroupLayout.Builder layout = BindGroupLayout.builder().withUniform(MinecraftShaderTranslator.BLOCK, UniformType.UNIFORM_BUFFER);
		for (final String sampler : samplers) {
			layout.withSampler(sampler);
		}
		layout.withSampler(MinecraftShaderTranslator.STENCIL);
		return layout.build();
	}

}