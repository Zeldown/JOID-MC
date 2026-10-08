package dev.joid.backend.minecraft.lib.font.impl.minecraft.dto;

import org.joml.Matrix4fc;

import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.vertex.VertexConsumer;

public final class GlyphVertexCapture implements VertexConsumer {

	private final float[] positions = new float[16];
	private final float[] textures  = new float[16];

	@Getter private int count;

	public @NonNull GlyphVertexCapture reset() {
		this.count = 0;
		return this;
	}

	public float getX(final int vertex) {
		return this.positions[vertex * 2];
	}

	public float getY(final int vertex) {
		return this.positions[vertex * 2 + 1];
	}

	public float getU(final int vertex) {
		return this.textures[vertex * 2];
	}

	public float getV(final int vertex) {
		return this.textures[vertex * 2 + 1];
	}

	@Override
	public @NonNull GlyphVertexCapture addVertex(final Matrix4fc pose, final float x, final float y, final float z) {
		return this.addVertex(x, y, z);
	}

	@Override
	public @NonNull GlyphVertexCapture addVertex(final float x, final float y, final float z) {
		if (this.count * 2 >= this.positions.length) {
			throw new IllegalStateException("A Minecraft glyph has more than " + this.positions.length / 2 + " vertices");
		}

		this.positions[this.count * 2] = x;
		this.positions[this.count * 2 + 1] = y;
		this.count++;
		return this;
	}

	@Override
	public @NonNull GlyphVertexCapture setUv(final float u, final float v) {
		this.textures[this.count * 2 - 2] = u;
		this.textures[this.count * 2 - 1] = v;
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