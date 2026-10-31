package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import com.mojang.blaze3d.vertex.VertexConsumer;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class GlyphVertexCapture implements VertexConsumer {

	private float minX;
	private float minY;
	private float minU;
	private float minV;
	private float maxX;
	private float maxY;
	private float maxU;
	private float maxV;

	public @NonNull GlyphVertexCapture reset() {
		this.minX = Float.POSITIVE_INFINITY;
		this.minY = Float.POSITIVE_INFINITY;
		this.minU = Float.POSITIVE_INFINITY;
		this.minV = Float.POSITIVE_INFINITY;
		this.maxX = Float.NEGATIVE_INFINITY;
		this.maxY = Float.NEGATIVE_INFINITY;
		this.maxU = Float.NEGATIVE_INFINITY;
		this.maxV = Float.NEGATIVE_INFINITY;
		return this;
	}

	public boolean isEmpty() {
		return this.maxX < this.minX;
	}

	@Override
	public @NonNull GlyphVertexCapture vertex(final double x, final double y, final double z) {
		this.minX = Math.min(this.minX, (float) x);
		this.minY = Math.min(this.minY, (float) y);
		this.maxX = Math.max(this.maxX, (float) x);
		this.maxY = Math.max(this.maxY, (float) y);
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture color(final int red, final int green, final int blue, final int alpha) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture uv(final float u, final float v) {
		this.minU = Math.min(this.minU, u);
		this.minV = Math.min(this.minV, v);
		this.maxU = Math.max(this.maxU, u);
		this.maxV = Math.max(this.maxV, v);
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture overlayCoords(final int u, final int v) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture uv2(final int u, final int v) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture normal(final float x, final float y, final float z) {
		return this;
	}

	@Override
	public void endVertex() {}

	@Override
	public void defaultColor(final int red, final int green, final int blue, final int alpha) {}

	@Override
	public void unsetDefaultColor() {}

}