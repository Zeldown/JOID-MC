package dev.joid.impl.joidmc.lib.render.raster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.vertex.DrawMode;
import dev.joid.lib.bridge.render.vertex.VertexBuffer;
import dev.joid.lib.color.Color;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class RasterLayer {

	private static final BlendState BLEND = BlendState.create(BlendState.Equation.ADD, BlendState.Factor.ONE, BlendState.Factor.ONE_MINUS_SRC_ALPHA);

	private final RenderBridge          bridge;
	private final Rasterizer            rasterizer;
	private final List<ScreenRectangle> regionList;
	private final Texture               texture;
	private final ByteBuffer            data;

	public RasterLayer(final RenderBridge bridge) {
		this.bridge     = bridge;
		this.rasterizer = new Rasterizer();
		this.regionList = new ArrayList<>();
		this.texture    = new Texture(bridge);
		this.data       = ByteBuffer.allocateDirect(6 * VertexBuffer.STRIDE).order(ByteOrder.LITTLE_ENDIAN);
	}

	public void reset() {
		this.regionList.clear();
	}

	public ScreenRectangle getViewport() {
		final RenderState state = this.bridge.getState();
		return new ScreenRectangle(0, 0, state.getViewportWidth(), state.getViewportHeight());
	}

	public Matrix4f getMatrix(final double x, final double y) {
		final RenderState state = this.bridge.getState();
		final int width = state.getViewportWidth();
		final int height = state.getViewportHeight();
		final float[] projection = this.bridge.getProjection().getMatrix();
		final Matrix4f matrix = new Matrix4f(
				projection[0] * width / 2F, 0F, 0F, 0F,
				0F, -projection[5] * height / 2F, 0F, 0F,
				0F, 0F, 1F, 0F,
				(projection[12] + 1F) * width / 2F, (1F - projection[13]) * height / 2F, 0F, 1F);
		matrix.mul(new Matrix4f().set(this.bridge.getModelView().getMatrix())).translate((float) x, (float) y, 0F);
		final Vector3f axis = matrix.getScale(new Vector3f());
		return matrix.scale(1F, 1F, axis.x / axis.z).m32(0F);
	}

	public ScreenRectangle getRegion(final Matrix4f matrix, final double width, final double height) {
		final RenderState state = this.bridge.getState();
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
		final int right = Math.min(state.getViewportWidth(), Mth.ceil(maximumX));
		final int bottom = Math.min(state.getViewportHeight(), Mth.ceil(maximumY));
		return right <= left || bottom <= top ? null : new ScreenRectangle(left, top, right - left, bottom - top);
	}

	public GpuTextureView render(final ScreenRectangle region, final Lighting.Entry lighting, final RasterDrawer drawer) {
		final RenderState state = this.bridge.getState();
		final int width = state.getViewportWidth();
		final int height = state.getViewportHeight();
		if (!this.regionList.isEmpty() && (!this.rasterizer.isAllocated(width, height) || this.regionList.stream().anyMatch(region::intersects))) {
			this.bridge.flush();
		}

		this.rasterizer.allocate(width, height);
		this.regionList.add(region);
		return this.rasterizer.render(region, lighting, drawer);
	}

	public void composite(final GpuTextureView view, final ScreenRectangle region, final Color color) {
		final RenderState state = this.bridge.getState();
		final int width = state.getViewportWidth();
		final int height = state.getViewportHeight();
		final ITexture texture = state.getTexture();
		final TextureFilter filter = state.getTextureFilter();
		final BlendState blend = state.getBlend();
		final float red = state.getRed();
		final float green = state.getGreen();
		final float blue = state.getBlue();
		final float alpha = state.getAlpha();
		final float opacity = alpha * color.a;
		this.bridge.color(red * color.r * opacity, green * color.g * opacity, blue * color.b * opacity, opacity);
		state.setTexture(this.texture.borrow(view));
		state.setTextureFilter(TextureFilter.NEAREST);
		state.setBlend(RasterLayer.BLEND);
		this.bridge.pushProjection();
		this.bridge.ortho(0D, width, height, 0D, -1D, 1D);
		this.bridge.pushMatrix();
		this.bridge.loadIdentity();
		this.bridge.draw(DrawMode.TRIANGLES, this.buffer(region.left(), region.top(), region.width(), region.height(), region.left() / (float) width, 1F - region.top() / (float) height, region.right() / (float) width, 1F - region.bottom() / (float) height));
		this.bridge.popMatrix();
		this.bridge.popProjection();
		state.setBlend(blend);
		state.setTextureFilter(filter);
		state.setTexture(texture);
		this.bridge.color(red, green, blue, alpha);
	}

	public void rect(final double x, final double y, final double width, final double height, final int argb) {
		final int color = RasterLayer.toVertexColor(argb);
		RasterLayer.vertex(this.data, 0, x, y, 0F, 0F, color);
		RasterLayer.vertex(this.data, 1, x, y + height, 0F, 0F, color);
		RasterLayer.vertex(this.data, 2, x + width, y + height, 0F, 0F, color);
		RasterLayer.vertex(this.data, 3, x, y, 0F, 0F, color);
		RasterLayer.vertex(this.data, 4, x + width, y + height, 0F, 0F, color);
		RasterLayer.vertex(this.data, 5, x + width, y, 0F, 0F, color);

		final RenderState state = this.bridge.getState();
		final ITexture texture = state.getTexture();
		final BlendState blend = state.getBlend();
		state.setTexture(null);
		state.setBlend(BlendState.NORMAL);
		this.bridge.draw(DrawMode.TRIANGLES, VertexBuffer.create(this.data, 6, true, true, false));
		state.setBlend(blend);
		state.setTexture(texture);
	}

	private VertexBuffer buffer(final double x, final double y, final double width, final double height, final float u0, final float v0, final float u1, final float v1) {
		RasterLayer.vertex(this.data, 0, x, y, u0, v0);
		RasterLayer.vertex(this.data, 1, x, y + height, u0, v1);
		RasterLayer.vertex(this.data, 2, x + width, y + height, u1, v1);
		RasterLayer.vertex(this.data, 3, x, y, u0, v0);
		RasterLayer.vertex(this.data, 4, x + width, y + height, u1, v1);
		RasterLayer.vertex(this.data, 5, x + width, y, u1, v0);
		return VertexBuffer.create(this.data, 6, true, false, false);
	}

	private static void vertex(final ByteBuffer buffer, final int index, final double x, final double y, final float u, final float v) {
		final int offset = index * VertexBuffer.STRIDE;
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET, (float) x);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 4, (float) y);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 8, 0F);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET, u);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET + 4, v);
	}

	private static void vertex(final ByteBuffer buffer, final int index, final double x, final double y, final float u, final float v, final int color) {
		RasterLayer.vertex(buffer, index, x, y, u, v);
		buffer.putInt(index * VertexBuffer.STRIDE + VertexBuffer.COLOR_OFFSET, color);
	}

	private static int toVertexColor(final int argb) {
		return ARGB.red(argb) | ARGB.green(argb) << 8 | ARGB.blue(argb) << 16 | ARGB.alpha(argb) << 24;
	}

}