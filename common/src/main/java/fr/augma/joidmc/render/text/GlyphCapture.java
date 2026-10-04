package fr.augma.joidmc.render.text;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.util.ARGB;

public final class GlyphCapture implements VertexConsumer {

	private static final int VERTICES = 4;
	private static final int CAPACITY = GlyphCapture.VERTICES * 8;

	private final float[] positionX;
	private final float[] positionY;
	private final float[] textureU;
	private final float[] textureV;
	private final int[]   colorList;

	private int count;

	public GlyphCapture() {
		this.positionX = new float[GlyphCapture.CAPACITY];
		this.positionY = new float[GlyphCapture.CAPACITY];
		this.textureU  = new float[GlyphCapture.CAPACITY];
		this.textureV  = new float[GlyphCapture.CAPACITY];
		this.colorList = new int[GlyphCapture.CAPACITY];
	}

	public void reset() {
		this.count = 0;
	}

	public int getQuadCount() {
		return this.count / GlyphCapture.VERTICES;
	}

	public float getX(final int index) {
		return this.positionX[index];
	}

	public float getY(final int index) {
		return this.positionY[index];
	}

	public float getU(final int index) {
		return this.textureU[index];
	}

	public float getV(final int index) {
		return this.textureV[index];
	}

	public int getColor(final int index) {
		return this.colorList[index];
	}

	@Override
	public VertexConsumer addVertex(final float x, final float y, final float z) {
		if (this.count < GlyphCapture.CAPACITY) {
			this.positionX[this.count] = x;
			this.positionY[this.count] = y;
			this.colorList[this.count] = 0xFFFFFFFF;
			this.count++;
		}
		return this;
	}

	@Override
	public VertexConsumer setUv(final float u, final float v) {
		if (this.count > 0) {
			this.textureU[this.count - 1] = u;
			this.textureV[this.count - 1] = v;
		}
		return this;
	}

	@Override
	public VertexConsumer setColor(final int red, final int green, final int blue, final int alpha) {
		if (this.count > 0) {
			this.colorList[this.count - 1] = red | green << 8 | blue << 16 | alpha << 24;
		}
		return this;
	}

	@Override
	public VertexConsumer setColor(final int color) {
		return this.setColor(ARGB.red(color), ARGB.green(color), ARGB.blue(color), ARGB.alpha(color));
	}

	@Override
	public VertexConsumer setUv1(final int u, final int v) {
		return this;
	}

	@Override
	public VertexConsumer setUv2(final int u, final int v) {
		return this;
	}

	@Override
	public VertexConsumer setNormal(final float x, final float y, final float z) {
		return this;
	}

	@Override
	public VertexConsumer setLineWidth(final float width) {
		return this;
	}

}