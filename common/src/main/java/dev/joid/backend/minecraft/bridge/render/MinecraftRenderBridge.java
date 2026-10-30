package dev.joid.backend.minecraft.bridge.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.framebuffer.MinecraftFrameBuffer;
import dev.joid.backend.minecraft.bridge.render.pass.PassEncoder;
import dev.joid.backend.minecraft.bridge.render.pipeline.MinecraftPipelineCache;
import dev.joid.backend.minecraft.bridge.render.shader.MinecraftShader;
import dev.joid.backend.minecraft.bridge.render.shader.ShaderSourceProvider;
import dev.joid.backend.minecraft.bridge.render.texture.IMinecraftTexture;
import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.lib.bridge.render.RenderBridge;
import dev.joid.lib.bridge.render.framebuffer.IFrameBuffer;
import dev.joid.lib.bridge.render.matrix.DepthRange;
import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.shader.SamplerBinding;
import dev.joid.lib.bridge.render.shader.source.GlslShaderTranslator;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;

@Getter
public final class MinecraftRenderBridge extends RenderBridge {

	private final boolean                openGl;
	private final GpuDevice              device;
	private final boolean                zeroToOne;
	private final PassEncoder            passEncoder;
	private final Matrix4f               rasterProjection;
	private final ShaderSourceProvider   sourceProvider;
	private final MinecraftPipelineCache pipelineCache;

	@Getter(AccessLevel.NONE) private final List<MinecraftRenderTarget>  pendingList;
	@Getter(AccessLevel.NONE) private final List<ProjectionMatrixBuffer> rasterProjectionBufferList;

	private int                   rasterDepth;
	private ByteBuffer            scratch;
	private MinecraftRenderTarget target;
	private MinecraftRenderTarget rasterTarget;
	private MinecraftRenderTarget screenTarget;

	public MinecraftRenderBridge() {
		this.device                     = RenderSystem.getDevice();
		this.openGl                     = "OpenGL".equals(this.device.getDeviceInfo().backendName());
		this.zeroToOne                  = this.device.getDeviceInfo().isZZeroToOne();
		this.passEncoder                = new PassEncoder(this.device);
		this.sourceProvider             = new ShaderSourceProvider();
		this.pipelineCache              = new MinecraftPipelineCache(this.device, this.sourceProvider);
		this.rasterProjection           = new Matrix4f();
		this.pendingList                = new ArrayList<>();
		this.rasterProjectionBufferList = new ArrayList<>();
		this.scratch                    = ByteBuffer.allocateDirect(1 << 16).order(ByteOrder.nativeOrder());
	}

	@Override
	protected void beginFrameCommands() {
		if (this.screenTarget == null) {
			throw new IllegalStateException("A JOID frame needs a screen target, set it with screenTarget before beginFrame");
		}

		this.target = this.screenTarget;
		this.clearFrame(this.target);
		if (this.target.getViewport() != null) {
			this.clearFrame(this.target.getViewport());
		}
	}

	@Override
	protected void submitFrameCommands() {
		this.resolve(null);
		this.passEncoder.end();
		this.target = null;
	}

	@Override
	protected void clearDepthBuffer() {
		super.requireFrame();
		this.forEachTarget(super.getState(), target -> this.passEncoder.encoder().clearDepthTexture(target.getDepth(), 1D));
	}

	@Override
	protected void clearStencilBuffer() {
		super.requireFrame();
		this.forEachTarget(super.getState(), target -> {
			if (target.getStencil() != null) {
				this.clearColorTexture(target.getStencil(), new Vector4f(0F));
			}
		});
	}

	@Override
	protected void clearColorBuffer(final float red, final float green, final float blue, final float alpha) {
		super.requireFrame();
		this.forEachTarget(super.getState(), target -> {
			this.clearColorTexture(target.getTexture().getTexture(), new Vector4f(red, green, blue, alpha));
			target.clearPending();
		});
	}

	@Override
	protected void drawPrimitive(final @NonNull Primitive primitive, final @NonNull VertexBuffer buffer, final @NonNull IShader shader) {
		super.requireFrame();
		final RenderState state = super.getState();
		final MinecraftRenderTarget base = this.getBaseTarget(state);
		final MinecraftRenderTarget target = this.getTarget(state, base);
		if (!MinecraftRenderBridge.isVisible(state, target)) {
			return;
		}

		final StencilEmulation stencil = StencilEmulation.from(state, target.getStencil() != null);
		final boolean color = state.isColorWrite() || state.isDepthTest() && state.isDepthWrite();
		if (!color && !stencil.isWrite()) {
			return;
		}

		if (target == base) {
			this.resolve(base.getTexture());
		}

		final MinecraftShader blazeShader = (MinecraftShader) shader;
		for (final UniformSampler sampler : blazeShader.getSamplerMap().values()) {
			if (!sampler.getName().equals(GlslShaderTranslator.STENCIL)) {
				this.resolve(super.resolveSampler(sampler).getTexture());
			}
		}

		final GpuBufferSlice vertices = this.uploadVertices(buffer);
		final GpuBufferSlice uniforms = blazeShader.upload(state, buffer, this.getProjectionMatrix(state, target), super.getModelView(), stencil);
		if (color) {
			this.record(PipelineKey.of(shader, state, primitive), target.getView(), target.getDepthView(), stencil.isTest() ? target.getStencilView() : ((MinecraftTexture) super.getEmptyTexture()).getView(), blazeShader, vertices, uniforms, buffer.getCount());
			if (target != base) {
				target.markPending();
				if (!this.pendingList.contains(base)) {
					this.pendingList.add(base);
				}
			}
		}

		if (stencil.isWrite()) {
			this.passEncoder.encoder().copyTextureToTexture(target.getStencil(), target.getStencilCopy(), 0, 0, 0, 0, 0, target.getWidth(), target.getHeight());
			this.record(PipelineKey.stencil(shader, state, primitive), target.getStencilView(), null, target.getStencilCopyView(), blazeShader, vertices, uniforms, buffer.getCount());
		}

		this.passEncoder.end();
	}

	@Override
	public @NonNull ITexture createTexture() {
		return MinecraftTexture.create(this);
	}

	@Override
	public @NonNull IFrameBuffer createFrameBuffer(final int width, final int height) {
		return new MinecraftFrameBuffer(this, width, height);
	}

	@Override
	public @NonNull IShader createShader(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		return MinecraftShader.create(this, vertex, fragment, blend);
	}

	@Override
	public void suspend(final @NonNull Runnable draw) {
		draw.run();
	}

	@Override
	public void raster(final @NonNull IFrameBuffer target, final int width, final int height, final @NonNull Runnable draw) {
		super.requireFrame();
		final MinecraftRenderTarget renderTarget = ((MinecraftFrameBuffer) target).getTarget();
		this.resolve(renderTarget.getTexture());
		final MinecraftRenderTarget previous = this.rasterTarget;
		final ScissorState scissor = new ScissorState(RenderSystem.getScissorStateForRenderTypeDraws());
		final GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
		final ProjectionType projectionType = RenderSystem.getProjectionType();
		final float range = Math.max(1000F, Math.max(width, height));
		this.passEncoder.encoder().clearDepthTexture(renderTarget.getDepth(), 0D);
		this.rasterTarget = renderTarget;
		this.rasterProjection.setOrtho(0F, renderTarget.getWidth(), height, height - renderTarget.getHeight(), range, -range, this.zeroToOne);
		RenderSystem.setProjectionMatrix(this.getRasterProjectionBuffer(this.rasterDepth).getBuffer(this.rasterProjection), ProjectionType.ORTHOGRAPHIC);
		RenderSystem.enableScissorForRenderTypeDraws(0, 0, width, height);
		this.rasterDepth++;
		try {
			draw.run();
		} finally {
			this.rasterDepth--;
			RenderSystem.getScissorStateForRenderTypeDraws().setFrom(scissor);
			RenderSystem.setProjectionMatrix(projection, projectionType);
			this.rasterTarget = previous;
		}
	}

	@Override
	public boolean canWrap(final @NonNull TextureWrap wrap) {
		return wrap != TextureWrap.CLAMP_TO_BORDER;
	}

	public @NonNull MinecraftRenderBridge screenTarget(final MinecraftRenderTarget screenTarget) {
		this.screenTarget = screenTarget;
		return this;
	}

	public @NonNull ByteBuffer getScratch(final int size) {
		if (this.scratch.capacity() < size) {
			this.scratch = ByteBuffer.allocateDirect(Math.max(size, this.scratch.capacity() * 2)).order(ByteOrder.nativeOrder());
		}

		this.scratch.clear().limit(size);
		return this.scratch;
	}

	public static @NonNull GpuSampler getSampler(final TextureSampling sampling) {
		final AddressMode address = sampling.getWrap() == TextureWrap.REPEAT ? AddressMode.REPEAT : AddressMode.CLAMP_TO_EDGE;
		final FilterMode mode = sampling.getFilter() == TextureFilter.LINEAR ? FilterMode.LINEAR : FilterMode.NEAREST;
		return RenderSystem.getSamplerCache().getSampler(address, address, mode, mode, sampling.isMipmapFiltered());
	}

	private MinecraftRenderTarget getBaseTarget(final RenderState state) {
		return state.getFrameBuffer() == null ? this.target : ((MinecraftFrameBuffer) state.getFrameBuffer()).getTarget();
	}

	private MinecraftRenderTarget getTarget(final RenderState state, final MinecraftRenderTarget base) {
		final int width = state.getViewportWidth();
		final int height = state.getViewportHeight();
		if (this.rasterDepth > 0 || state.getViewportX() != 0 || state.getViewportY() != 0 || width <= 0 || height <= 0 || width > base.getWidth() || height > base.getHeight() || width == base.getWidth() && height == base.getHeight()) {
			return base;
		}
		return base.viewport(this, width, height);
	}

	private void forEachTarget(final RenderState state, final Consumer<MinecraftRenderTarget> action) {
		final MinecraftRenderTarget base = this.getBaseTarget(state);
		final MinecraftRenderTarget target = this.getTarget(state, base);
		action.accept(base);
		if (target != base) {
			action.accept(target);
		}
	}

	private void resolve(final ITexture texture) {
		for (final Iterator<MinecraftRenderTarget> iterator = this.pendingList.iterator(); iterator.hasNext();) {
			final MinecraftRenderTarget pending = iterator.next();
			if (texture == null || pending.getTexture() == texture) {
				pending.resolve(this.passEncoder.encoder());
				iterator.remove();
			}
		}
	}

	private void clearFrame(final MinecraftRenderTarget target) {
		this.passEncoder.encoder().clearDepthTexture(target.getDepth(), 1D);
		if (target.getStencil() != null) {
			this.clearColorTexture(target.getStencil(), new Vector4f(0F));
		}
	}

	private void clearColorTexture(final GpuTexture texture, final Vector4f color) {
		this.passEncoder.encoder().clearColorTexture(texture, color);
		if (this.openGl) {
			GL11.glFlush();
		}
	}

	private void record(final PipelineKey key, final GpuTextureView color, final GpuTextureView depth, final GpuTextureView stencil, final MinecraftShader shader, final GpuBufferSlice vertices, final GpuBufferSlice uniforms, final int count) {
		final CompiledRenderPipeline pipeline = this.pipelineCache.get(key);
		if (pipeline == null) {
			return;
		}

		final RenderState state = super.getState();
		final RenderPass pass = this.passEncoder.begin(color, depth);
		pass.setPipeline(pipeline);
		pass.setUniform(GlslShaderTranslator.BLOCK, uniforms);
		for (final UniformSampler sampler : shader.getSamplerMap().values()) {
			if (!sampler.getName().equals(GlslShaderTranslator.STENCIL)) {
				final SamplerBinding binding = super.resolveSampler(sampler);
				pass.setUniform(sampler.getName(), ((IMinecraftTexture) binding.getTexture()).getView(), MinecraftRenderBridge.getSampler(binding.getSampling()));
			}
		}
		pass.setUniform(GlslShaderTranslator.STENCIL, stencil, MinecraftRenderBridge.getSampler(TextureSampling.of(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE, false)));
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

	private GpuBufferSlice uploadVertices(final VertexBuffer buffer) {
		final ByteBuffer data = VertexFill.complete(buffer, this.getScratch(buffer.getCount() * VertexBuffer.STRIDE));
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

	private ProjectionMatrixBuffer getRasterProjectionBuffer(final int depth) {
		if (depth == this.rasterProjectionBufferList.size()) {
			this.rasterProjectionBufferList.add(new ProjectionMatrixBuffer("JOID Raster " + depth));
		}

		return this.rasterProjectionBufferList.get(depth);
	}

	private static boolean isVisible(final RenderState state, final MinecraftRenderTarget target) {
		return state.getViewportX() < target.getWidth() && state.getViewportY() < target.getHeight() && state.getViewportX() + state.getViewportWidth() > 0 && state.getViewportY() + state.getViewportHeight() > 0;
	}

}