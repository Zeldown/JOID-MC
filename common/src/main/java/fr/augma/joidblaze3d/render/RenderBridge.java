package fr.augma.joidblaze3d.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
import be.zeldown.joid.lib.color.Color;
import fr.augma.joidblaze3d.Constants;
import fr.augma.joidblaze3d.render.framebuffer.FrameBuffer;
import fr.augma.joidblaze3d.render.raster.Rasterizer;
import fr.augma.joidblaze3d.render.text.GlyphCapture;
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

import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class RenderBridge extends be.zeldown.joid.lib.bridge.render.RenderBridge {

	private static final int          STAGING_CAPACITY = 1 << 20;
	private static final BlendState   ITEM_BLEND       = BlendState.create(BlendState.Equation.ADD, BlendState.Factor.ONE, BlendState.Factor.ONE_MINUS_SRC_ALPHA);
	private static final int          FULL_BRIGHT      = 15728880;
	private static final float        DEGREE           = (float) Math.PI / 180F;
	private static final int[]        QUAD_ORDER       = {0, 1, 2, 0, 2, 3};
	private static final Matrix4f     IDENTITY         = new Matrix4f();
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
	private final List<ScreenRectangle>            regionList;
	private final Texture                          target;
	private final Texture                          emptyTexture;
	private final Texture                          itemTexture;
	private final Shader                           fixedShader;
	private final TrackingItemStackRenderState     itemState;
	private final Rasterizer                       rasterizer;
	private final GlyphCapture                     glyph;
	private final ByteBuffer                       itemData;

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
		this.regionList        = new ArrayList<>();
		this.vertexData        = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.uniformData       = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.pixelData         = RenderBridge.allocate(RenderBridge.STAGING_CAPACITY);
		this.target            = new Texture(this);
		this.emptyTexture      = new Texture(this).allocate(1, 1).upload(new int[] {0xFFFFFFFF}, 1, 1);
		this.itemTexture       = new Texture(this);
		this.itemState         = new TrackingItemStackRenderState();
		this.rasterizer        = new Rasterizer();
		this.glyph             = new GlyphCapture();
		this.itemData          = RenderBridge.allocate(6 * VertexBuffer.STRIDE);
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

	public void item(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final @NonNull Color color, final boolean durability, final boolean stackCount, final boolean cooldown, final String text) {
		this.requireFrame();
		final Minecraft minecraft = Minecraft.getInstance();
		this.itemState.clear();
		minecraft.getItemModelResolver().updateForTopItem(this.itemState, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
		if (this.itemState.isEmpty()) {
			return;
		}

		final RenderState render = super.getState();
		final int viewportWidth = render.getViewportWidth();
		final int viewportHeight = render.getViewportHeight();
		final Matrix4f matrix = this.getLayerMatrix(viewportWidth, viewportHeight, x, y);
		final ScreenRectangle region = RenderBridge.getRegion(matrix, width, height, viewportWidth, viewportHeight);
		if (region != null) {
			this.claim(region, viewportWidth, viewportHeight);
			final GpuTextureView view = this.rasterizer.render(region, this.itemState.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT, (pose, collector) -> {
				pose.mulPose(matrix);
				pose.scale((float) (width / 16D), (float) (height / 16D), (float) (width / 16D));
				pose.translate(8F, 8F, 0F);
				pose.scale(16F, -16F, 16F);
				this.itemState.submit(pose, collector, RenderBridge.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			});
			this.composite(view, region, viewportWidth, viewportHeight, color);
		}

		this.decorations(stack, x, y, width, height, durability, stackCount, cooldown, text);
	}

	public void entity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale, final float yaw, final float pitch, final float rotationYaw, final float rotationPitch) {
		this.requireFrame();
		final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		final EntityRenderState state = dispatcher.getRenderer(entity).createRenderState(entity, 1F);
		state.shadowPieces.clear();
		state.outlineColor = 0;
		state.lightCoords  = RenderBridge.FULL_BRIGHT;
		if (state instanceof final LivingEntityRenderState living) {
			living.bodyRot           = 180F + yaw;
			living.yRot              = yaw;
			living.xRot              = pitch;
			living.boundingBoxWidth  = living.boundingBoxWidth / living.scale;
			living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
			living.scale             = 1F;
		}

		final RenderState render = super.getState();
		final int viewportWidth = render.getViewportWidth();
		final int viewportHeight = render.getViewportHeight();
		final Matrix4f matrix = this.getLayerMatrix(viewportWidth, viewportHeight, x + width / 2D, y + height / 2D);

		final Quaternionf camera = new Quaternionf().rotateX(-pitch * RenderBridge.DEGREE);
		final Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).mul(camera);
		final Quaternionf orientation = new Quaternionf().rotateY(rotationYaw * RenderBridge.DEGREE).rotateX(rotationPitch * RenderBridge.DEGREE);
		final float offset = state.boundingBoxHeight / 2F + 0.0625F;
		final ScreenRectangle region = new ScreenRectangle(0, 0, viewportWidth, viewportHeight);
		this.claim(region, viewportWidth, viewportHeight);
		final GpuTextureView view = this.rasterizer.render(region, Lighting.Entry.ENTITY_IN_UI, (pose, collector) -> {
			pose.mulPose(matrix);
			pose.scale((float) scale, (float) scale, (float) -scale);
			pose.translate(0F, offset, 0F);
			pose.mulPose(orientation);
			pose.mulPose(rotation);
			final CameraRenderState cameraState = new CameraRenderState();
			cameraState.orientation = camera.conjugate(new Quaternionf()).rotateY((float) Math.PI);
			dispatcher.submit(state, cameraState, 0D, 0D, 0D, pose, collector);
		});

		this.composite(view, region, viewportWidth, viewportHeight, Color.WHITE);
	}

	private void decorations(final ItemStack stack, final double x, final double y, final double width, final double height, final boolean durability, final boolean stackCount, final boolean cooldown, final String text) {
		final double scaleX = width / 16D;
		final double scaleY = height / 16D;
		if (durability && stack.isBarVisible()) {
			this.rect(x + 2D * scaleX, y + 13D * scaleY, 13D * scaleX, 2D * scaleY, 0xFF000000);
			this.rect(x + 2D * scaleX, y + 13D * scaleY, stack.getBarWidth() * scaleX, scaleY, ARGB.opaque(stack.getBarColor()));
		}

		if (cooldown) {
			final Minecraft minecraft = Minecraft.getInstance();
			final LocalPlayer player = minecraft.player;
			final float percent = player == null ? 0F : player.getCooldowns().getCooldownPercent(stack, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
			if (percent > 0F) {
				final int top = Mth.floor(16F * (1F - percent));
				final int bottom = top + Mth.ceil(16F * percent);
				this.rect(x, y + top * scaleY, 16D * scaleX, (bottom - top) * scaleY, Integer.MAX_VALUE);
			}
		}

		if (stackCount && (stack.getCount() != 1 || text != null)) {
			final Font font = Minecraft.getInstance().font;
			final String count = text == null ? String.valueOf(stack.getCount()) : text;
			this.text(font, count, x + (17D - font.width(count)) * scaleX, y + 9D * scaleY, scaleX, scaleY);
		}
	}

	private void composite(final GpuTextureView view, final ScreenRectangle region, final int width, final int height, final Color color) {
		final RenderState state = super.getState();
		final ITexture texture = state.getTexture();
		final TextureFilter filter = state.getTextureFilter();
		final BlendState blend = state.getBlend();
		final float red = state.getRed();
		final float green = state.getGreen();
		final float blue = state.getBlue();
		final float alpha = state.getAlpha();
		final float opacity = alpha * color.a;
		super.color(red * color.r * opacity, green * color.g * opacity, blue * color.b * opacity, opacity);
		state.setTexture(this.itemTexture.borrow(view));
		state.setTextureFilter(TextureFilter.NEAREST);
		state.setBlend(RenderBridge.ITEM_BLEND);
		super.pushProjection();
		super.ortho(0D, width, height, 0D, -1D, 1D);
		super.pushMatrix();
		super.loadIdentity();
		this.draw(DrawMode.TRIANGLES, this.itemBuffer(region.left(), region.top(), region.width(), region.height(), region.left() / (float) width, 1F - region.top() / (float) height, region.right() / (float) width, 1F - region.bottom() / (float) height));
		super.popMatrix();
		super.popProjection();
		state.setBlend(blend);
		state.setTextureFilter(filter);
		state.setTexture(texture);
		super.color(red, green, blue, alpha);
	}

	private void rect(final double x, final double y, final double width, final double height, final int argb) {
		final int color = RenderBridge.toVertexColor(argb);
		RenderBridge.itemVertex(this.itemData, 0, x, y, 0F, 0F, color);
		RenderBridge.itemVertex(this.itemData, 1, x, y + height, 0F, 0F, color);
		RenderBridge.itemVertex(this.itemData, 2, x + width, y + height, 0F, 0F, color);
		RenderBridge.itemVertex(this.itemData, 3, x, y, 0F, 0F, color);
		RenderBridge.itemVertex(this.itemData, 4, x + width, y + height, 0F, 0F, color);
		RenderBridge.itemVertex(this.itemData, 5, x + width, y, 0F, 0F, color);

		final RenderState state = super.getState();
		final ITexture texture = state.getTexture();
		final BlendState blend = state.getBlend();
		state.setTexture(null);
		state.setBlend(BlendState.NORMAL);
		this.draw(DrawMode.TRIANGLES, VertexBuffer.create(this.itemData, 6, true, true, false));
		state.setBlend(blend);
		state.setTexture(texture);
	}

	private void text(final Font font, final String text, final double x, final double y, final double scaleX, final double scaleY) {
		final RenderState state = super.getState();
		final ITexture texture = state.getTexture();
		final BlendState blend = state.getBlend();
		final TextureFilter filter = state.getTextureFilter();
		state.setBlend(BlendState.NORMAL);
		state.setTextureFilter(TextureFilter.NEAREST);
		font.prepareText(text, 0F, 0F, -1, true, 0).visit(new Font.GlyphVisitor() {

			@Override
			public void acceptRenderable(final TextRenderable renderable) {
				RenderBridge.this.glyph.reset();
				renderable.render(RenderBridge.IDENTITY, RenderBridge.this.glyph, RenderBridge.FULL_BRIGHT, true);
				state.setTexture(RenderBridge.this.itemTexture.borrow(renderable.textureView()));
				for (int quad = 0; quad < RenderBridge.this.glyph.getQuadCount(); quad++) {
					for (int index = 0; index < RenderBridge.QUAD_ORDER.length; index++) {
						final int vertex = quad * 4 + RenderBridge.QUAD_ORDER[index];
						RenderBridge.itemVertex(RenderBridge.this.itemData, index, x + RenderBridge.this.glyph.getX(vertex) * scaleX, y + RenderBridge.this.glyph.getY(vertex) * scaleY, RenderBridge.this.glyph.getU(vertex), RenderBridge.this.glyph.getV(vertex), RenderBridge.this.glyph.getColor(vertex));
					}

					RenderBridge.this.draw(DrawMode.TRIANGLES, VertexBuffer.create(RenderBridge.this.itemData, 6, true, true, false));
				}
			}

		});
		state.setTextureFilter(filter);
		state.setBlend(blend);
		state.setTexture(texture);
	}

	private static int toVertexColor(final int argb) {
		return ARGB.red(argb) | ARGB.green(argb) << 8 | ARGB.blue(argb) << 16 | ARGB.alpha(argb) << 24;
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
		this.regionList.clear();
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

	private Matrix4f getLayerMatrix(final int width, final int height, final double x, final double y) {
		final float[] projection = super.getProjection().getMatrix();
		final Matrix4f matrix = new Matrix4f(
				projection[0] * width / 2F, 0F, 0F, 0F,
				0F, -projection[5] * height / 2F, 0F, 0F,
				0F, 0F, 1F, 0F,
				(projection[12] + 1F) * width / 2F, (1F - projection[13]) * height / 2F, 0F, 1F);
		matrix.mul(new Matrix4f().set(super.getModelView().getMatrix())).translate((float) x, (float) y, 0F);
		final Vector3f axis = matrix.getScale(new Vector3f());
		return matrix.scale(1F, 1F, axis.x / axis.z).m32(0F);
	}

	private static ScreenRectangle getRegion(final Matrix4f matrix, final double width, final double height, final int viewportWidth, final int viewportHeight) {
		final Vector3f corner = new Vector3f();
		float minimumX = Float.MAX_VALUE;
		float minimumY = Float.MAX_VALUE;
		float maximumX = -Float.MAX_VALUE;
		float maximumY = -Float.MAX_VALUE;
		for (int index = 0; index < 4; index++) {
			matrix.transformPosition((float) (index % 2 * width), (float) (index / 2 * height), 0F, corner);
			minimumX = Math.min(minimumX, corner.x);
			minimumY = Math.min(minimumY, corner.y);
			maximumX = Math.max(maximumX, corner.x);
			maximumY = Math.max(maximumY, corner.y);
		}

		final int left = Math.max(0, Mth.floor(minimumX));
		final int top = Math.max(0, Mth.floor(minimumY));
		final int right = Math.min(viewportWidth, Mth.ceil(maximumX));
		final int bottom = Math.min(viewportHeight, Mth.ceil(maximumY));
		return right <= left || bottom <= top ? null : new ScreenRectangle(left, top, right - left, bottom - top);
	}

	private void claim(final ScreenRectangle region, final int width, final int height) {
		if (!this.regionList.isEmpty() && (!this.rasterizer.isAllocated(width, height) || this.regionList.stream().anyMatch(region::intersects))) {
			this.flush();
		}

		this.rasterizer.allocate(width, height);
		this.regionList.add(region);
	}

	private VertexBuffer itemBuffer(final double x, final double y, final double width, final double height, final float u0, final float v0, final float u1, final float v1) {
		RenderBridge.itemVertex(this.itemData, 0, x, y, u0, v0);
		RenderBridge.itemVertex(this.itemData, 1, x, y + height, u0, v1);
		RenderBridge.itemVertex(this.itemData, 2, x + width, y + height, u1, v1);
		RenderBridge.itemVertex(this.itemData, 3, x, y, u0, v0);
		RenderBridge.itemVertex(this.itemData, 4, x + width, y + height, u1, v1);
		RenderBridge.itemVertex(this.itemData, 5, x + width, y, u1, v0);
		return VertexBuffer.create(this.itemData, 6, true, false, false);
	}

	private static void itemVertex(final ByteBuffer buffer, final int index, final double x, final double y, final float u, final float v) {
		final int offset = index * VertexBuffer.STRIDE;
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET, (float) x);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 4, (float) y);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 8, 0F);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET, u);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET + 4, v);
	}

	private static void itemVertex(final ByteBuffer buffer, final int index, final double x, final double y, final float u, final float v, final int color) {
		RenderBridge.itemVertex(buffer, index, x, y, u, v);
		buffer.putInt(index * VertexBuffer.STRIDE + VertexBuffer.COLOR_OFFSET, color);
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