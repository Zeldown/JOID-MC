package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import org.joml.Matrix4fc;

import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.vertex.VertexConsumer;

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

	@Override
	public @NonNull GlyphVertexCapture addVertex(final Matrix4fc pose, final float x, final float y, final float z) {
		return this.addVertex(x, y, z);
	}

	@Override
	public @NonNull GlyphVertexCapture addVertex(final float x, final float y, final float z) {
		this.minX = Math.min(this.minX, x);
		this.minY = Math.min(this.minY, y);
		this.maxX = Math.max(this.maxX, x);
		this.maxY = Math.max(this.maxY, y);
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setUv(final float u, final float v) {
		this.minU = Math.min(this.minU, u);
		this.minV = Math.min(this.minV, v);
		this.maxU = Math.max(this.maxU, u);
		this.maxV = Math.max(this.maxV, v);
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setColor(final int r, final int g, final int b, final int a) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setColor(final int color) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setUv1(final int u, final int v) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setUv2(final int u, final int v) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setNormal(final float x, final float y, final float z) {
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setLineWidth(final float width) {
		return this;
	}

}