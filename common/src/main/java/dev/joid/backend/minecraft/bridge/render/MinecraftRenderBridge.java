package dev.joid.backend.minecraft.bridge.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joml.Vector4f;

import dev.joid.backend.minecraft.bridge.render.framebuffer.MinecraftFrameBuffer;
import dev.joid.backend.minecraft.bridge.render.pass.PassEncoder;
import dev.joid.backend.minecraft.bridge.render.pipeline.PipelineCache;
import dev.joid.backend.minecraft.bridge.render.raster.Rasterizer;
import dev.joid.backend.minecraft.bridge.render.shader.MinecraftShader;
import dev.joid.backend.minecraft.bridge.render.shader.MinecraftShaderTranslator;
import dev.joid.backend.minecraft.bridge.render.shader.ShaderSourceProvider;
import dev.joid.backend.minecraft.bridge.render.texture.IGpuTexture;
import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.lib.bridge.render.RenderBridge;
import dev.joid.lib.bridge.render.framebuffer.IFrameBuffer;
import dev.joid.lib.bridge.render.matrix.DepthRange;
import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.shader.SamplerBinding;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.uniform.UniformSampler;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.PipelineKey;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.state.StencilEmulation;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureSampling;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import dev.joid.lib.bridge.render.vertex.Primitive;
import dev.joid.lib.bridge.render.vertex.VertexBuffer;
import dev.joid.lib.bridge.render.vertex.VertexFill;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;

@Getter
public final class MinecraftRenderBridge extends RenderBridge {

	private final GpuDevice            device;
	private final boolean              zeroToOne;
	private final PassEncoder          passEncoder;
	private final PipelineCache        pipelineCache;
	private final ShaderSourceProvider sourceProvider;
	private final Rasterizer           rasterizer;

	private ByteBuffer            scratch;
	private MinecraftRenderTarget target;
	private MinecraftRenderTarget screenTarget;

	public MinecraftRenderBridge() {
		this.device         = RenderSystem.getDevice();
		this.zeroToOne      = this.device.getDeviceInfo().isZZeroToOne();
		this.passEncoder    = PassEncoder.create(this.device);
		this.sourceProvider = ShaderSourceProvider.create();
		this.pipelineCache  = PipelineCache.create(this.device, this.sourceProvider);
		this.scratch        = ByteBuffer.allocateDirect(1 << 16).order(ByteOrder.nativeOrder());
		this.rasterizer     = Rasterizer.create(this);
	}

	public @NonNull MinecraftRenderBridge screenTarget(final MinecraftRenderTarget screenTarget) {
		this.screenTarget = screenTarget;
		return this;
	}

	@Override
	public boolean canWrap(final @NonNull TextureWrap wrap) {
		return wrap != TextureWrap.CLAMP_TO_BORDER;
	}

	@Override
	public @NonNull ITexture createTexture() {
		return MinecraftTexture.create(this);
	}

	@Override
	public @NonNull IFrameBuffer createFrameBuffer(final int width, final int height) {
		return MinecraftFrameBuffer.create(this, width, height);
	}

	@Override
	public @NonNull IShader createShader(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		return MinecraftShader.create(this, vertex, fragment, blend);
	}

	public @NonNull ByteBuffer getScratch(final int size) {
		if (this.scratch.capacity() < size) {
			this.scratch = ByteBuffer.allocateDirect(Math.max(size, this.scratch.capacity() * 2)).order(ByteOrder.nativeOrder());
		}

		this.scratch.clear().limit(size);
		return this.scratch;
	}

	@Override
	protected void beginFrameCommands() {
		if (this.screenTarget == null) {
			throw new IllegalStateException("A JOID frame needs a screen target, set it with screenTarget before beginFrame");
		}

		this.target = this.screenTarget;
		this.passEncoder.encoder().clearDepthTexture(this.target.getDepth(), 1D);
		if (this.target.getStencil() != null) {
			this.passEncoder.encoder().clearColorTexture(this.target.getStencil(), new Vector4f(0F));
		}
	}

	@Override
	protected void submitFrameCommands() {
		this.passEncoder.end();
		this.target = null;
	}

	@Override
	protected void clearDepthBuffer() {
		super.requireFrame();
		this.passEncoder.encoder().clearDepthTexture(this.getTarget(super.getState()).getDepth(), 1D);
	}

	@Override
	protected void clearStencilBuffer() {
		super.requireFrame();
		final MinecraftRenderTarget target = this.getTarget(super.getState());
		if (target.getStencil() != null) {
			this.passEncoder.encoder().clearColorTexture(target.getStencil(), new Vector4f(0F));
		}
	}

	@Override
	protected void clearColorBuffer(final float red, final float green, final float blue, final float alpha) {
		super.requireFrame();
		this.passEncoder.encoder().clearColorTexture(this.getTarget(super.getState()).getTexture().getTexture(), new Vector4f(red, green, blue, alpha));
	}

	@Override
	protected void drawPrimitive(final @NonNull Primitive primitive, final @NonNull VertexBuffer buffer, final @NonNull IShader shader) {
		super.requireFrame();
		final RenderState state = super.getState();
		final MinecraftRenderTarget target = this.getTarget(state);
		if (!MinecraftRenderBridge.isVisible(state, target)) {
			return;
		}

		final StencilEmulation stencil = StencilEmulation.create(state, target.getStencil() != null);
		final boolean color = state.isColorWrite() || state.isDepthTest() && state.isDepthWrite();
		if (!color && !stencil.isWrite()) {
			return;
		}

		final MinecraftShader blazeShader = (MinecraftShader) shader;
		final GpuBufferSlice vertices = this.uploadVertices(buffer, state);
		final GpuBufferSlice uniforms = blazeShader.upload(state, this.getProjectionMatrix(state, target), super.getModelView(), stencil);
		if (color) {
			this.record(PipelineKey.create(shader, state, primitive), target.getView(), target.getDepthView(), stencil.isTest() ? target.getStencilView() : ((MinecraftTexture) super.getEmptyTexture()).getView(), blazeShader, vertices, uniforms, buffer.getCount());
		}

		if (stencil.isWrite()) {
			this.passEncoder.encoder().copyTextureToTexture(target.getStencil(), target.getStencilCopy(), 0, 0, 0, 0, 0, target.getWidth(), target.getHeight());
			this.record(PipelineKey.stencil(shader, state, primitive), target.getStencilView(), null, target.getStencilCopyView(), blazeShader, vertices, uniforms, buffer.getCount());
		}
	}

	public static @NonNull GpuSampler getSampler(final @NonNull TextureSampling sampling) {
		final AddressMode address = sampling.getWrap() == TextureWrap.REPEAT ? AddressMode.REPEAT : AddressMode.CLAMP_TO_EDGE;
		final FilterMode mode = sampling.getFilter() == TextureFilter.LINEAR ? FilterMode.LINEAR : FilterMode.NEAREST;
		return RenderSystem.getSamplerCache().getSampler(address, address, mode, mode, sampling.isMipmapFiltered());
	}

	private MinecraftRenderTarget getTarget(final RenderState state) {
		return state.getFrameBuffer() == null ? this.target : ((MinecraftFrameBuffer) state.getFrameBuffer()).getTarget();
	}

	private void record(final PipelineKey key, final GpuTextureView color, final GpuTextureView depth, final GpuTextureView stencil, final MinecraftShader shader, final GpuBufferSlice vertices, final GpuBufferSlice uniforms, final int count) {
		final RenderPipeline pipeline = this.pipelineCache.get(key);
		if (pipeline == null) {
			return;
		}

		final RenderState state = super.getState();
		final RenderPass pass = this.passEncoder.begin(color, depth);
		pass.setPipeline(pipeline);
		pass.setUniform(MinecraftShaderTranslator.BLOCK, uniforms);
		for (final UniformSampler sampler : shader.getSamplerMap().values()) {
			final SamplerBinding binding = super.resolveSampler(sampler);
			pass.bindTexture(sampler.getName(), ((IGpuTexture) binding.getTexture()).getView(), MinecraftRenderBridge.getSampler(binding.getSampling()));
		}
		pass.bindTexture(MinecraftShaderTranslator.STENCIL, stencil, MinecraftRenderBridge.getSampler(TextureSampling.of(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE, false)));
		pass.setVertexBuffer(0, vertices);

		final int width = color.getWidth(0);
		final int height = color.getHeight(0);
		final int left = Math.max(0, state.getViewportX());
		final int bottom = Math.max(0, state.getViewportY());
		final int right = Math.min(width, state.getViewportX() + state.getViewportWidth());
		final int top = Math.min(height, state.getViewportY() + state.getViewportHeight());
		if (left == 0 && bottom == 0 && right == width && top == height) {
			pass.disableScissor();
		} else {
			pass.enableScissor(left, bottom, right - left, top - bottom);
		}

		pass.draw(count, 1, 0, 0);
	}

	private GpuBufferSlice uploadVertices(final VertexBuffer buffer, final RenderState state) {
		final ByteBuffer data = VertexFill.complete(buffer, this.getScratch(buffer.getCount() * VertexBuffer.STRIDE), state);
		return this.device.createCommandEncoder().transientMemory().uploadGpu(data, VertexBuffer.STRIDE, GpuBuffer.USAGE_VERTEX);
	}

	private float[] getProjectionMatrix(final RenderState state, final MinecraftRenderTarget target) {
		final float[] matrix = super.getProjection().getMatrix().clone();
		final float scaleX = (float) state.getViewportWidth() / target.getWidth();
		final float scaleY = (float) state.getViewportHeight() / target.getHeight();
		final float offsetX = (float) (2 * state.getViewportX() + state.getViewportWidth()) / target.getWidth() - 1F;
		final float offsetY = (float) (2 * state.getViewportY() + state.getViewportHeight()) / target.getHeight() - 1F;
		for (int column = 0; column < 4; column++) {
			matrix[column * 4] = scaleX * matrix[column * 4] + offsetX * matrix[column * 4 + 3];
			matrix[column * 4 + 1] = scaleY * matrix[column * 4 + 1] + offsetY * matrix[column * 4 + 3];
		}
		return this.zeroToOne ? DepthRange.toZeroToOne(matrix) : matrix;
	}

	private static boolean isVisible(final RenderState state, final MinecraftRenderTarget target) {
		return state.getViewportX() < target.getWidth() && state.getViewportY() < target.getHeight() && state.getViewportX() + state.getViewportWidth() > 0 && state.getViewportY() + state.getViewportHeight() > 0;
	}

}