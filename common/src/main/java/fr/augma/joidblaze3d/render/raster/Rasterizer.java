package fr.augma.joidblaze3d.render.raster;

import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;

public final class Rasterizer {

	private static final float DEPTH = 10000F;

	private final SubmitNodeStorage      storage;
	private final PoseStack              pose;
	private final Projection             projection;
	private final ProjectionMatrixBuffer projectionBuffer;

	private GpuTexture     texture;
	private GpuTextureView view;
	private GpuTexture     depth;
	private GpuTextureView depthView;

	public Rasterizer() {
		this.storage          = new SubmitNodeStorage();
		this.pose             = new PoseStack();
		this.projection       = new Projection();
		this.projectionBuffer = new ProjectionMatrixBuffer("JOID Raster");
	}

	public void allocate(final int width, final int height) {
		if (this.isAllocated(width, height)) {
			return;
		}

		if (this.texture != null) {
			this.view.close();
			this.texture.close();
			this.depthView.close();
			this.depth.close();
		}

		final GpuDevice device = RenderSystem.getDevice();
		this.texture   = device.createTexture("JOID Raster", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
		this.view      = device.createTextureView(this.texture);
		this.depth     = device.createTexture("JOID Raster Depth", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.D32_FLOAT, width, height, 1, 1);
		this.depthView = device.createTextureView(this.depth);
	}

	public boolean isAllocated(final int width, final int height) {
		return this.texture != null && this.texture.getWidth(0) == width && this.texture.getHeight(0) == height;
	}

	public @NonNull GpuTextureView render(final @NonNull ScreenRectangle region, final @NonNull Lighting.Entry lighting, final @NonNull RasterDrawer drawer) {
		final int width = this.texture.getWidth(0);
		final int height = this.texture.getHeight(0);
		final int bottom = height - region.bottom();
		RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.texture, GuiRenderer.CLEAR_COLOR, this.depth, 0D, region.left(), bottom, region.width(), region.height());

		final GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
		final ProjectionType type = RenderSystem.getProjectionType();
		RenderSystem.outputColorTextureOverride = this.view;
		RenderSystem.outputDepthTextureOverride = this.depthView;
		this.projection.setupOrtho(-Rasterizer.DEPTH, Rasterizer.DEPTH, width, height, true);
		RenderSystem.setProjectionMatrix(this.projectionBuffer.getBuffer(this.projection), ProjectionType.ORTHOGRAPHIC);
		RenderSystem.enableScissorForRenderTypeDraws(region.left(), bottom, region.width(), region.height());

		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.gameRenderer.lighting().setupFor(lighting);
		this.pose.pushPose();
		drawer.draw(this.pose, this.storage);
		minecraft.gameRenderer.featureRenderDispatcher().renderAllFeatures(this.storage);
		this.pose.popPose();

		RenderSystem.disableScissorForRenderTypeDraws();
		RenderSystem.outputColorTextureOverride = null;
		RenderSystem.outputDepthTextureOverride = null;
		if (projection != null) {
			RenderSystem.setProjectionMatrix(projection, type);
		}
		return this.view;
	}

}