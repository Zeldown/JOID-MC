package fr.augma.joidblaze3d.render.raster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;

public final class Rasterizer {

	private static final int ATLAS   = 1024;
	private static final int MINIMUM = 64;
	private static final int MAXIMUM = 512;

	private final SubmitNodeStorage      storage;
	private final PoseStack              pose;
	private final Projection             projection;
	private final ProjectionMatrixBuffer projectionBuffer;
	private final Map<Integer, List<Atlas>> atlasMap;

	public Rasterizer() {
		this.storage          = new SubmitNodeStorage();
		this.pose             = new PoseStack();
		this.projection       = new Projection();
		this.projectionBuffer = new ProjectionMatrixBuffer("JOID Raster");
		this.atlasMap         = new HashMap<>();
	}

	public void begin() {
		for (final List<Atlas> atlasList : this.atlasMap.values()) {
			for (final Atlas atlas : atlasList) {
				atlas.begin();
			}
		}
	}

	public RasterCell render(final @NonNull Object identity, final double size, final @NonNull Lighting.Entry lighting, final @NonNull RasterDrawer drawer) {
		final int resolution = Rasterizer.resolution(size);
		final List<Atlas> atlasList = this.atlasMap.computeIfAbsent(resolution, key -> new ArrayList<>());
		for (final Atlas atlas : atlasList) {
			final RasterCell cell = atlas.render(identity, lighting, drawer);
			if (cell != null) {
				return cell;
			}
		}

		final Atlas atlas = new Atlas(resolution);
		atlasList.add(atlas);
		return atlas.render(identity, lighting, drawer);
	}

	private static int resolution(final double size) {
		int resolution = Rasterizer.MINIMUM;
		while (resolution < size && resolution < Rasterizer.MAXIMUM) {
			resolution <<= 1;
		}
		return resolution;
	}

	private final class Atlas {

		private final int                  cell;
		private final int                  columns;
		private final int                  size;
		private final Map<Object, Integer> cellMap;

		private GpuTexture     texture;
		private GpuTextureView view;
		private GpuTexture     depth;
		private GpuTextureView depthView;
		private int            cursor;

		private Atlas(final int cell) {
			this.cell    = cell;
			this.columns = Math.max(2, Rasterizer.ATLAS / cell);
			this.size    = cell * this.columns;
			this.cellMap = new HashMap<>();
		}

		private void begin() {
			this.cellMap.clear();
			this.cursor = 0;
		}

		private RasterCell render(final Object identity, final Lighting.Entry lighting, final RasterDrawer drawer) {
			final Integer cached = this.cellMap.get(identity);
			if (cached != null) {
				return this.toCell(cached);
			}

			if (this.cursor >= this.columns * this.columns) {
				return null;
			}

			final int index = this.cursor++;
			this.cellMap.put(identity, index);
			this.allocate();
			this.draw(index, lighting, drawer);
			return this.toCell(index);
		}

		private RasterCell toCell(final int index) {
			final float ratio = this.cell / (float) this.size;
			final float inset = 1F / this.size;
			final float u = index % this.columns * ratio;
			final float v = 1F - index / this.columns * ratio;
			return RasterCell.create(this.view, u + inset, v - inset, u + ratio - inset, v - ratio + inset);
		}

		private void allocate() {
			if (this.texture != null) {
				return;
			}

			final GpuDevice device = RenderSystem.getDevice();
			this.texture   = device.createTexture("JOID Raster Atlas " + this.cell, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, this.size, this.size, 1, 1);
			this.view      = device.createTextureView(this.texture);
			this.depth     = device.createTexture("JOID Raster Atlas Depth " + this.cell, GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.D32_FLOAT, this.size, this.size, 1, 1);
			this.depthView = device.createTextureView(this.depth);
		}

		private void draw(final int index, final Lighting.Entry lighting, final RasterDrawer drawer) {
			final int left   = index % this.columns * this.cell;
			final int top    = index / this.columns * this.cell;
			final int bottom = this.size - top - this.cell;
			RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.texture, GuiRenderer.CLEAR_COLOR, this.depth, 0D, left, bottom, this.cell, this.cell);

			Rasterizer.this.pose.pushPose();
			Rasterizer.this.pose.translate(left + this.cell / 2F, top + this.cell / 2F, 0F);

			final GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
			final ProjectionType type = RenderSystem.getProjectionType();
			RenderSystem.outputColorTextureOverride = this.view;
			RenderSystem.outputDepthTextureOverride = this.depthView;
			Rasterizer.this.projection.setupOrtho(-1000F, 1000F, this.size, this.size, true);
			RenderSystem.setProjectionMatrix(Rasterizer.this.projectionBuffer.getBuffer(Rasterizer.this.projection), ProjectionType.ORTHOGRAPHIC);
			RenderSystem.enableScissorForRenderTypeDraws(left, bottom, this.cell, this.cell);

			final Minecraft minecraft = Minecraft.getInstance();
			minecraft.gameRenderer.lighting().setupFor(lighting);
			drawer.draw(Rasterizer.this.pose, Rasterizer.this.storage, this.cell);
			minecraft.gameRenderer.featureRenderDispatcher().renderAllFeatures(Rasterizer.this.storage);

			RenderSystem.disableScissorForRenderTypeDraws();
			RenderSystem.outputColorTextureOverride = null;
			RenderSystem.outputDepthTextureOverride = null;
			if (projection != null) {
				RenderSystem.setProjectionMatrix(projection, type);
			}
			Rasterizer.this.pose.popPose();
		}

	}

}