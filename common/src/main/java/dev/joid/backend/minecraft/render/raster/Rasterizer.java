package dev.joid.backend.minecraft.render.raster;

import org.joml.Vector4f;

import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.texture.Texture;
import dev.joid.lib.bridge.render.matrix.PixelGrid;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import dev.joid.lib.bridge.render.vertex.DrawMode;
import dev.joid.lib.render.tessellator.Tessellator;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;

public final class Rasterizer {

	private final RenderBridge           bridge;
	private final Texture                texture;
	private final PoseStack              pose;
	private final Projection             projection;
	private final ProjectionMatrixBuffer projectionBuffer;
	private final SubmitNodeStorage      storage;

	private GpuTexture     depth;
	private GpuTextureView depthView;

	private Rasterizer(final RenderBridge bridge) {
		this.bridge           = bridge;
		this.texture          = Texture.create(bridge);
		this.pose             = new PoseStack();
		this.projection       = new Projection();
		this.projectionBuffer = new ProjectionMatrixBuffer("JOID Raster");
		this.storage          = new SubmitNodeStorage();
	}

	public static @NonNull Rasterizer create(final @NonNull RenderBridge bridge) {
		return new Rasterizer(bridge);
	}

	public void draw(final double x, final double y, final double width, final double height, final Lighting.Entry lighting, final @NonNull IRasterDrawable drawable) {
		if (width <= 0D || height <= 0D) {
			return;
		}

		final PixelGrid grid = this.bridge.getPixelGrid();
		final double left = grid.snapX(x);
		final double top = grid.snapY(y);
		final double right = grid.snapRight(x, x + width);
		final double bottom = grid.snapBottom(y, y + height);
		final int pixelWidth = Rasterizer.toPixels((right - left) * grid.getScaleX());
		final int pixelHeight = Rasterizer.toPixels((bottom - top) * grid.getScaleY());
		this.allocate(pixelWidth, pixelHeight);
		final int textureWidth = this.texture.getWidth();
		final int textureHeight = this.texture.getHeight();
		this.bridge.getPassEncoder().encoder().clearColorAndDepthTextures(this.texture.getTexture(), new Vector4f(0F), this.depth, 0D, 0, textureHeight - pixelHeight, pixelWidth, pixelHeight);
		this.rasterize(pixelWidth, pixelHeight, lighting, drawable);

		final float u = (float) pixelWidth / textureWidth;
		final float v = 1F - (float) pixelHeight / textureHeight;
		this.bridge.blend(BlendState.PREMULTIPLIED);
		this.bridge.texture(this.texture, grid.isAligned() ? TextureFilter.NEAREST : TextureFilter.LINEAR, TextureWrap.CLAMP_TO_EDGE);
		try {
			final Tessellator tess = Tessellator.inst();
			tess.start(DrawMode.QUADS);
			tess.addVertexWithUV(left, bottom, 0D, 0D, v);
			tess.addVertexWithUV(right, bottom, 0D, u, v);
			tess.addVertexWithUV(right, top, 0D, u, 1D);
			tess.addVertexWithUV(left, top, 0D, 0D, 1D);
			tess.draw();
		} finally {
			this.bridge.resetTexture();
			this.bridge.blend(BlendState.DISABLED);
		}
	}

	private void rasterize(final int width, final int height, final Lighting.Entry lighting, final IRasterDrawable drawable) {
		final Minecraft minecraft = Minecraft.getInstance();
		final GpuTextureView color = RenderSystem.outputColorTextureOverride;
		final GpuTextureView depth = RenderSystem.outputDepthTextureOverride;
		final float size = Math.min(width, height);
		RenderSystem.outputColorTextureOverride = this.texture.getView();
		RenderSystem.outputDepthTextureOverride = this.depthView;
		RenderSystem.backupProjectionMatrix();
		this.projection.setupOrtho(-Math.max(1000F, size), Math.max(1000F, size), this.texture.getWidth(), this.texture.getHeight(), true);
		RenderSystem.setProjectionMatrix(this.projectionBuffer.getBuffer(this.projection), ProjectionType.ORTHOGRAPHIC);
		RenderSystem.enableScissorForRenderTypeDraws(0, this.texture.getHeight() - height, width, height);
		minecraft.gameRenderer.lighting().setupFor(lighting);
		this.pose.pushPose();
		try {
			this.pose.translate(width / 2F, height / 2F, 0F);
			this.pose.scale(size, -size, size);
			drawable.submit(this.pose, this.storage);
			minecraft.gameRenderer.featureRenderDispatcher().renderAllFeatures(this.storage);
		} finally {
			this.pose.popPose();
			RenderSystem.disableScissorForRenderTypeDraws();
			RenderSystem.restoreProjectionMatrix();
			RenderSystem.outputColorTextureOverride = color;
			RenderSystem.outputDepthTextureOverride = depth;
		}
	}

	private void allocate(final int width, final int height) {
		if (this.depth != null && this.texture.getWidth() >= width && this.texture.getHeight() >= height) {
			return;
		}

		final int textureWidth = Rasterizer.capacity(this.depth == null ? 0 : this.texture.getWidth(), width);
		final int textureHeight = Rasterizer.capacity(this.depth == null ? 0 : this.texture.getHeight(), height);
		if (this.depth != null) {
			this.bridge.getPassEncoder().end();
			this.depthView.close();
			this.depth.close();
		}

		this.texture.allocate(textureWidth, textureHeight);
		this.depth     = this.bridge.getDevice().createTexture("JOID Raster Depth", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.D32_FLOAT, textureWidth, textureHeight, 1, 1);
		this.depthView = this.bridge.getDevice().createTextureView(this.depth);
	}

	private static int capacity(final int current, final int required) {
		return current >= required ? current : Math.max(required, Math.max(64, current * 2));
	}

	private static int toPixels(final double size) {
		return Math.max(1, Math.min(4096, (int) Math.ceil(Math.abs(size) - 1E-3D)));
	}

}