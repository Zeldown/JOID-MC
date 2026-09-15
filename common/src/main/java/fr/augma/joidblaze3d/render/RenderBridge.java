package fr.augma.joidblaze3d.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.joml.Vector4f;

import be.zeldown.joid.lib.bridge.render.framebuffer.IFrameBuffer;
import be.zeldown.joid.lib.bridge.render.shader.IShader;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderSource;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderStage;
import be.zeldown.joid.lib.bridge.render.state.BlendState;
import be.zeldown.joid.lib.bridge.render.state.RenderState;
import be.zeldown.joid.lib.bridge.render.state.StencilOperation;
import be.zeldown.joid.lib.bridge.render.texture.ITexture;
import be.zeldown.joid.lib.bridge.render.texture.TextureFilter;
import be.zeldown.joid.lib.bridge.render.texture.TextureWrap;
import be.zeldown.joid.lib.bridge.render.vertex.DrawMode;
import be.zeldown.joid.lib.bridge.render.vertex.VertexBuffer;
import fr.augma.joidblaze3d.Constants;
import fr.augma.joidblaze3d.render.framebuffer.FrameBuffer;
import fr.augma.joidblaze3d.render.pipeline.PipelineKey;
import fr.augma.joidblaze3d.render.shader.Shader;
import fr.augma.joidblaze3d.render.shader.ShaderTranslator;
import fr.augma.joidblaze3d.render.shader.uniform.SamplerUniform;
import fr.augma.joidblaze3d.render.shader.uniform.UniformMember;
import fr.augma.joidblaze3d.render.texture.Texture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendEquation;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.BlendOp;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.Identifier;

public final class RenderBridge extends be.zeldown.joid.lib.bridge.render.RenderBridge {

	private static final int          STAGING_CAPACITY = 1 << 20;
	private static final VertexFormat VERTEX_FORMAT    = VertexFormat.builder(0)
			.addAttribute("aPosition", VertexBuffer.POSITION_OFFSET, 12, GpuFormat.RGB32_FLOAT, 1)
			.addAttribute("aTexCoord", VertexBuffer.TEXTURE_OFFSET, 8, GpuFormat.RG32_FLOAT, 1)
			.addAttribute("aColor", VertexBuffer.COLOR_OFFSET, 4, GpuFormat.RGBA8_UNORM, 1)
			.addAttribute("aNormal", VertexBuffer.NORMAL_OFFSET, 8, GpuFormat.RGBA8_SNORM, 1)
			.build();

	private final GpuDevice                        device;
	private final boolean                          zeroToOne;
	private final int                              uniformAlignment;
	private final Map<Identifier, String>          vertexSourceMap;
	private final Map<Identifier, String>          fragmentSourceMap;
	private final Map<PipelineKey, RenderPipeline> pipelineMap;
	private final List<Operation>                  operationList;
	private final Texture                          target;
	private final Texture                          emptyTexture;
	private final Shader                           fixedShader;

	private ByteBuffer     vertexData;
	private ByteBuffer     uniformData;
	private ByteBuffer     pixelData;
	private int            vertexPosition;
	private int            uniformPosition;
	private int            vertexUploaded;
	private int            uniformUploaded;
	private GpuBuffer      vertexBuffer;
	private GpuBuffer      uniformBuffer;
	private GpuTexture     stencilTexture;
	private GpuTextureView stencilView;
	private GpuTexture     stencilCopy;
	private GpuTextureView stencilCopyView;
	private boolean        frameActive;

	public RenderBridge() {
		this.device            = RenderSystem.getDevice();
		this.zeroToOne         = this.device.getDeviceInfo().isZZeroToOne();
		this.uniformAlignment  = this.device.getDeviceInfo().limits().minUniformOffsetAlignment();
		this.vertexSourceMap   = new HashMap<>();
		this.fragmentSourceMap = new HashMap<>();
		this.pipelineMap       = new HashMap<>();
		this.operationList     = new ArrayList<>();
		this.vertexData        = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.uniformData       = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.pixelData         = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.target            = new Texture(this);
		this.emptyTexture      = new Texture(this).allocate(1, 1).upload(new int[] {0xFFFFFFFF}, 1, 1);
		this.fixedShader       = (Shader) this.createShader(ShaderSource.read(ShaderStage.VERTEX, ShaderSource.class.getResourceAsStream("/assets/shaders/fixed/fixed.vsh")), ShaderSource.read(ShaderStage.FRAGMENT, ShaderSource.class.getResourceAsStream("/assets/shaders/fixed/fixed.fsh")), BlendState.DISABLED);
	}

	public void beginFrame(final int width, final int height) {
		if (this.frameActive) {
			throw new IllegalStateException("The JOID frame has already begun");
		}

		this.target.allocate(width, height);
		if (this.stencilTexture == null || this.stencilTexture.getWidth(0) != width || this.stencilTexture.getHeight(0) != height) {
			this.releaseStencil();
			this.stencilTexture  = this.createStencil("JOID Stencil", width, height);
			this.stencilView     = this.device.createTextureView(this.stencilTexture);
			this.stencilCopy     = this.createStencil("JOID Stencil Copy", width, height);
			this.stencilCopyView = this.device.createTextureView(this.stencilCopy);
		}

		this.vertexPosition  = 0;
		this.uniformPosition = 0;
		this.vertexUploaded  = 0;
		this.uniformUploaded = 0;
		this.frameActive     = true;

		super.frameBuffer(null);
		super.viewport(0, 0, width, height);
		super.ortho(0D, width, height, 0D, 0D, 10000D);
		super.loadIdentity();

		final GpuTexture target = this.target.getTexture();
		final GpuTexture stencil = this.stencilTexture;
		this.operationList.add(Operation.encoder(encoder -> {
			encoder.clearColorTexture(target, new Vector4f(0F));
			encoder.clearColorTexture(stencil, new Vector4f(0F));
		}));
	}

	public @NonNull GpuTextureView endFrame() {
		this.requireFrame();
		this.flush();
		this.frameActive = false;
		return this.target.getView();
	}

	@Override
	public void clear(final float red, final float green, final float blue, final float alpha) {
		this.requireFrame();
		if (!super.getState().isColorMask()) {
			return;
		}

		final GpuTexture texture = this.getTarget(super.getState()).getTexture();
		this.operationList.add(Operation.encoder(encoder -> encoder.clearColorTexture(texture, new Vector4f(red, green, blue, alpha))));
	}

	@Override
	public void clearStencil() {
		this.requireFrame();
		if (super.getState().getFrameBuffer() != null) {
			return;
		}

		final GpuTexture stencil = this.stencilTexture;
		this.operationList.add(Operation.encoder(encoder -> encoder.clearColorTexture(stencil, new Vector4f(0F))));
	}

	@Override
	public void draw(final @NonNull DrawMode mode, final @NonNull VertexBuffer buffer) {
		this.requireFrame();
		final RenderState state = super.getState();
		final Shader shader = state.getShader() == null ? this.fixedShader : (Shader) state.getShader();
		if (!shader.isActive() || buffer.getCount() == 0 || state.getViewportWidth() <= 0 || state.getViewportHeight() <= 0) {
			return;
		}

		final boolean screen = state.getFrameBuffer() == null;
		final boolean stencilTest = screen && state.isStencilTest();
		final boolean stencilWrite = stencilTest && (state.getStencilPass() != StencilOperation.KEEP || state.getStencilFail() != StencilOperation.KEEP);
		if (!state.isColorMask() && !stencilWrite) {
			return;
		}

		final PrimitiveTopology topology = RenderBridge.getTopology(mode);
		final int firstVertex = this.writeVertices(buffer, state);
		final int uniformOffset = this.writeUniforms(state, shader, stencilTest);
		if (state.isColorMask()) {
			this.record(PipelineKey.create(shader, state.getBlend(), state.isCull(), topology, false), this.getTarget(state).getView(), screen ? this.stencilView : this.emptyTexture.getView(), state, shader, firstVertex, buffer.getCount(), uniformOffset);
		}

		if (stencilWrite) {
			final GpuTexture stencil = this.stencilTexture;
			final GpuTexture copy = this.stencilCopy;
			this.operationList.add(Operation.encoder(encoder -> encoder.copyTextureToTexture(stencil, copy, 0, 0, 0, 0, 0, stencil.getWidth(0), stencil.getHeight(0))));
			this.record(PipelineKey.create(shader, BlendState.DISABLED, state.isCull(), topology, true), this.stencilView, this.stencilCopyView, state, shader, firstVertex, buffer.getCount(), uniformOffset);
		}
	}

	@Override
	public @NonNull ITexture createTexture() {
		return new Texture(this);
	}

	@Override
	public @NonNull IFrameBuffer createFrameBuffer(final int width, final int height, final @NonNull TextureFilter filter) {
		return FrameBuffer.create(this, width, height);
	}

	@Override
	public @NonNull IShader createShader(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull BlendState blend) {
		return Shader.create(this, vertex, fragment, blend);
	}

	public void upload(final @NonNull GpuTexture texture, final @NonNull int[] pixels, final int width, final int height) {
		this.flush();
		final int size = width * height * 4;
		this.pixelData = RenderBridge.ensure(this.pixelData, size);
		for (int i = 0; i < width * height; i++) {
			final int pixel = pixels[i];
			this.pixelData.putInt(i * 4, (pixel & 0xFF00FF00) | ((pixel >> 16) & 0xFF) | ((pixel & 0xFF) << 16));
		}
		this.device.createCommandEncoder().writeToTexture(texture, this.pixelData.slice(0, size), 0, 0, 0, 0, width, height);
	}

	public void flush() {
		if (this.operationList.isEmpty()) {
			return;
		}

		final CommandEncoder encoder = this.device.createCommandEncoder();
		this.vertexBuffer    = this.uploadBuffer(encoder, this.vertexBuffer, GpuBuffer.USAGE_VERTEX, this.vertexData, this.vertexUploaded, this.vertexPosition);
		this.uniformBuffer   = this.uploadBuffer(encoder, this.uniformBuffer, GpuBuffer.USAGE_UNIFORM, this.uniformData, this.uniformUploaded, this.uniformPosition);
		this.vertexUploaded  = this.vertexPosition;
		this.uniformUploaded = this.uniformPosition;

		int index = 0;
		while (index < this.operationList.size()) {
			final Operation operation = this.operationList.get(index);
			if (operation.getEncoder() != null) {
				operation.getEncoder().accept(encoder);
				index++;
				continue;
			}

			try (RenderPass pass = encoder.createRenderPass(() -> "JOID", operation.getTarget(), Optional.empty())) {
				while (index < this.operationList.size() && this.operationList.get(index).getTarget() == operation.getTarget()) {
					this.operationList.get(index).getPass().accept(pass);
					index++;
				}
			}
		}
		this.operationList.clear();
	}

	public void registerSource(final @NonNull Identifier identifier, final @NonNull ShaderType type, final @NonNull String source) {
		(type == ShaderType.VERTEX ? this.vertexSourceMap : this.fragmentSourceMap).put(identifier, source);
	}

	public boolean isValid(final @NonNull PipelineKey key) {
		return this.device.precompilePipeline(this.getPipeline(key), this::getSource).isValid();
	}

	private void record(final PipelineKey key, final GpuTextureView target, final GpuTextureView stencil, final RenderState state, final Shader shader, final int firstVertex, final int count, final int uniformOffset) {
		final RenderPipeline pipeline = this.getPipeline(key);
		if (!this.device.precompilePipeline(pipeline, this::getSource).isValid()) {
			return;
		}

		final List<String> samplerList = shader.getSamplerList();
		final GpuTextureView[] views = new GpuTextureView[samplerList.size()];
		final GpuSampler[] samplers = new GpuSampler[samplerList.size()];
		for (int i = 0; i < samplerList.size(); i++) {
			final SamplerUniform uniform = shader.getSamplerMap().get(samplerList.get(i));
			final Texture texture = (Texture) state.getTexture();
			if (uniform != null && uniform.getTexture() != null && uniform.getTexture().getView() != null) {
				views[i]    = uniform.getTexture().getView();
				samplers[i] = RenderBridge.getSampler(uniform.getFilter(), uniform.getWrap());
			} else if (texture != null && texture.getView() != null) {
				views[i]    = texture.getView();
				samplers[i] = RenderBridge.getSampler(state.getTextureFilter(), state.getTextureWrap());
			} else {
				views[i]    = this.emptyTexture.getView();
				samplers[i] = RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.REPEAT);
			}
		}

		final int size = shader.getBlock().getData().capacity();
		final GpuSampler stencilSampler = RenderBridge.getSampler(TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE);
		this.operationList.add(Operation.pass(target, pass -> {
			pass.setPipeline(pipeline);
			pass.setVertexBuffer(0, this.vertexBuffer.slice());
			pass.setUniform(ShaderTranslator.BLOCK, this.uniformBuffer.slice(uniformOffset, size));
			for (int i = 0; i < views.length; i++) {
				pass.bindTexture(samplerList.get(i), views[i], samplers[i]);
			}
			pass.bindTexture(ShaderTranslator.STENCIL, stencil, stencilSampler);
			pass.draw(count, 1, firstVertex, 0);
		}));
	}

	private int writeVertices(final VertexBuffer buffer, final RenderState state) {
		final int size = buffer.getCount() * VertexBuffer.STRIDE;
		final int offset = this.vertexPosition;
		this.vertexData = RenderBridge.ensure(this.vertexData, offset + size);
		this.vertexData.put(offset, buffer.getBuffer(), buffer.getBuffer().position(), size);
		if (!buffer.isTexture() || !buffer.isColor() || !buffer.isNormal()) {
			final int color = RenderBridge.toByte(state.getRed()) | RenderBridge.toByte(state.getGreen()) << 8 | RenderBridge.toByte(state.getBlue()) << 16 | RenderBridge.toByte(state.getAlpha()) << 24;
			for (int vertex = offset; vertex < offset + size; vertex += VertexBuffer.STRIDE) {
				if (!buffer.isTexture()) {
					this.vertexData.putFloat(vertex + VertexBuffer.TEXTURE_OFFSET, 0F);
					this.vertexData.putFloat(vertex + VertexBuffer.TEXTURE_OFFSET + 4, 0F);
				}

				if (!buffer.isColor()) {
					this.vertexData.putInt(vertex + VertexBuffer.COLOR_OFFSET, color);
				}

				if (!buffer.isNormal()) {
					this.vertexData.putInt(vertex + VertexBuffer.NORMAL_OFFSET, 127 << 16);
				}
			}
		}

		this.vertexPosition = offset + size;
		return offset / VertexBuffer.STRIDE;
	}

	private int writeUniforms(final RenderState state, final Shader shader, final boolean stencilTest) {
		final Map<String, UniformMember> memberMap = shader.getBlock().getMemberMap();
		final UniformMember projection = memberMap.get("uProjectionMatrix");
		if (projection != null) {
			final float[] matrix = super.getProjection().getMatrix().clone();
			if (this.zeroToOne) {
				for (int column = 0; column < 4; column++) {
					matrix[column * 4 + 2] = 0.5F * matrix[column * 4 + 2] + 0.5F * matrix[column * 4 + 3];
				}
			}
			projection.putMatrix(matrix);
		}

		final UniformMember modelView = memberMap.get("uModelViewMatrix");
		if (modelView != null) {
			modelView.putMatrix(super.getModelView().getMatrix());
		}

		final UniformMember normal = memberMap.get("uNormalMatrix");
		if (normal != null) {
			normal.putMatrix(super.getModelView().getNormalMatrix());
		}

		final UniformMember lighting = memberMap.get("uLighting");
		if (lighting != null) {
			lighting.putInt(state.isLighting() ? 1 : 0);
		}

		memberMap.get("joid_AlphaTest").putInt(state.isAlphaTest() ? 1 : 0);
		memberMap.get("joid_AlphaThreshold").putFloats(state.getAlphaThreshold());
		memberMap.get("joid_StencilTest").putInt(stencilTest ? 1 : 0);
		memberMap.get("joid_StencilFunction").putInt(state.getStencilFunction().ordinal());
		memberMap.get("joid_StencilReference").putInt(state.getStencilReference());
		memberMap.get("joid_StencilMask").putInt(state.getStencilMask());
		memberMap.get("joid_StencilFail").putInt(state.getStencilFail().ordinal());
		memberMap.get("joid_StencilPass").putInt(state.getStencilPass().ordinal());

		final ByteBuffer data = shader.getBlock().getData();
		final int offset = RenderBridge.align(this.uniformPosition, this.uniformAlignment);
		this.uniformData = RenderBridge.ensure(this.uniformData, offset + data.capacity());
		this.uniformData.put(offset, data, 0, data.capacity());
		this.uniformPosition = offset + data.capacity();
		return offset;
	}

	private GpuBuffer uploadBuffer(final CommandEncoder encoder, final GpuBuffer buffer, final int usage, final ByteBuffer data, final int from, final int to) {
		GpuBuffer target = buffer;
		int start = from;
		if (target == null || target.size() < to) {
			final long size = Math.max(to, target == null ? RenderBridge.STAGING_CAPACITY : target.size() * 2L);
			if (target != null) {
				target.close();
			}

			target = this.device.createBuffer(() -> "JOID Buffer", usage | GpuBuffer.USAGE_COPY_DST, size);
			start = 0;
		}

		if (to > start) {
			encoder.writeToBuffer(target.slice(start, to - start), data.slice(start, to - start));
		}
		return target;
	}

	private RenderPipeline getPipeline(final PipelineKey key) {
		return this.pipelineMap.computeIfAbsent(key, pipelineKey -> RenderPipeline.builder()
				.withLocation(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "joid/pipeline_" + this.pipelineMap.size()))
				.withVertexShader(pipelineKey.getShader().getIdentifier())
				.withFragmentShader(pipelineKey.isStencil() ? pipelineKey.getShader().getStencilIdentifier() : pipelineKey.getShader().getIdentifier())
				.withBindGroupLayout(pipelineKey.getShader().getLayout())
				.withVertexBinding(0, RenderBridge.VERTEX_FORMAT)
				.withPrimitiveTopology(pipelineKey.getTopology())
				.withColorTargetState(new ColorTargetState(RenderBridge.getBlendFunction(pipelineKey), pipelineKey.isStencil() ? GpuFormat.R8_UNORM : GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_ALL))
				.withCull(pipelineKey.isCull())
				.build());
	}

	private String getSource(final Identifier identifier, final ShaderType type) {
		return (type == ShaderType.VERTEX ? this.vertexSourceMap : this.fragmentSourceMap).get(identifier);
	}

	private Texture getTarget(final RenderState state) {
		return state.getFrameBuffer() == null ? this.target : ((FrameBuffer) state.getFrameBuffer()).getTexture();
	}

	private GpuTexture createStencil(final String label, final int width, final int height) {
		return this.device.createTexture(label, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.R8_UNORM, width, height, 1, 1);
	}

	private void releaseStencil() {
		if (this.stencilTexture == null) {
			return;
		}

		this.stencilView.close();
		this.stencilTexture.close();
		this.stencilCopyView.close();
		this.stencilCopy.close();
	}

	private void requireFrame() {
		if (!this.frameActive) {
			throw new IllegalStateException("JOID rendering must happen between beginFrame and endFrame");
		}
	}

	private static Optional<BlendFunction> getBlendFunction(final PipelineKey key) {
		if (!key.isBlend()) {
			return Optional.empty();
		}

		final BlendOp operation = BlendOp.valueOf(key.getEquation().name());
		return Optional.of(new BlendFunction(new BlendEquation(BlendFactor.valueOf(key.getSourceColor().name()), BlendFactor.valueOf(key.getDestinationColor().name()), operation), new BlendEquation(BlendFactor.valueOf(key.getSourceAlpha().name()), BlendFactor.valueOf(key.getDestinationAlpha().name()), operation)));
	}

	private static GpuSampler getSampler(final TextureFilter filter, final TextureWrap wrap) {
		final AddressMode address = wrap == TextureWrap.REPEAT ? AddressMode.REPEAT : AddressMode.CLAMP_TO_EDGE;
		final FilterMode mode = filter == TextureFilter.LINEAR ? FilterMode.LINEAR : FilterMode.NEAREST;
		return RenderSystem.getSamplerCache().getSampler(address, address, mode, mode, false);
	}

	private static PrimitiveTopology getTopology(final DrawMode mode) {
		switch (mode) {
		case TRIANGLES:
			return PrimitiveTopology.TRIANGLES;
		case LINES:
			return PrimitiveTopology.DEBUG_LINES;
		case LINE_STRIP:
			return PrimitiveTopology.DEBUG_LINE_STRIP;
		case POLYGON:
			return PrimitiveTopology.TRIANGLE_FAN;
		default:
			throw new IllegalArgumentException(mode + " is not supported by Blaze3D, convert it before drawing");
		}
	}

	private static ByteBuffer ensure(final ByteBuffer buffer, final int size) {
		if (buffer.capacity() >= size) {
			return buffer;
		}

		final ByteBuffer grown = RenderBridge.allocate(Math.max(size, buffer.capacity() * 2));
		grown.put(0, buffer, 0, buffer.capacity());
		return grown;
	}

	private static ByteBuffer allocate(final int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN);
	}

	private static int toByte(final float value) {
		return Math.round(Math.max(0F, Math.min(1F, value)) * 255F);
	}

	private static int align(final int value, final int alignment) {
		return (value + alignment - 1) / alignment * alignment;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class Operation {

		private final GpuTextureView           target;
		private final Consumer<RenderPass>     pass;
		private final Consumer<CommandEncoder> encoder;

		private static Operation pass(final GpuTextureView target, final Consumer<RenderPass> pass) {
			return new Operation(target, pass, null);
		}

		private static Operation encoder(final Consumer<CommandEncoder> encoder) {
			return new Operation(null, null, encoder);
		}

	}

}