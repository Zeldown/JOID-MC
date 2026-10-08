package dev.joid.impl.joidmc.lib.render.text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joml.Matrix4f;

import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.vertex.DrawMode;
import dev.joid.lib.bridge.render.vertex.VertexBuffer;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;

public final class GlyphRenderer {

	private static final int      FULL_BRIGHT = 15728880;
	private static final int[]    QUAD_ORDER  = {0, 1, 2, 0, 2, 3};
	private static final Matrix4f IDENTITY    = new Matrix4f();

	private final RenderBridge bridge;
	private final Texture      texture;
	private final GlyphCapture capture;
	private final ByteBuffer   data;

	public GlyphRenderer(final RenderBridge bridge) {
		this.bridge  = bridge;
		this.texture = new Texture(bridge);
		this.capture = new GlyphCapture();
		this.data    = ByteBuffer.allocateDirect(6 * VertexBuffer.STRIDE).order(ByteOrder.LITTLE_ENDIAN);
	}

	public void draw(final Font.PreparedText prepared, final double x, final double y, final double scaleX, final double scaleY) {
		final RenderState state = this.bridge.getState();
		final ITexture texture = state.getTexture();
		final BlendState blend = state.getBlend();
		final TextureFilter filter = state.getTextureFilter();
		state.setBlend(BlendState.NORMAL);
		state.setTextureFilter(TextureFilter.NEAREST);
		prepared.visit(new Font.GlyphVisitor() {

			@Override
			public void acceptRenderable(final TextRenderable renderable) {
				GlyphRenderer.this.capture.reset();
				renderable.render(GlyphRenderer.IDENTITY, GlyphRenderer.this.capture, GlyphRenderer.FULL_BRIGHT, true);
				state.setTexture(GlyphRenderer.this.texture.borrow(renderable.textureView()));
				for (int quad = 0; quad < GlyphRenderer.this.capture.getQuadCount(); quad++) {
					for (int index = 0; index < GlyphRenderer.QUAD_ORDER.length; index++) {
						final int vertex = quad * 4 + GlyphRenderer.QUAD_ORDER[index];
						GlyphRenderer.vertex(GlyphRenderer.this.data, index, x + GlyphRenderer.this.capture.getX(vertex) * scaleX, y + GlyphRenderer.this.capture.getY(vertex) * scaleY, GlyphRenderer.this.capture.getU(vertex), GlyphRenderer.this.capture.getV(vertex), GlyphRenderer.this.capture.getColor(vertex));
					}

					GlyphRenderer.this.bridge.draw(DrawMode.TRIANGLES, VertexBuffer.create(GlyphRenderer.this.data, 6, true, true, false));
				}
			}

		});
		state.setTextureFilter(filter);
		state.setBlend(blend);
		state.setTexture(texture);
	}

	private static void vertex(final ByteBuffer buffer, final int index, final double x, final double y, final float u, final float v, final int color) {
		final int offset = index * VertexBuffer.STRIDE;
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET, (float) x);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 4, (float) y);
		buffer.putFloat(offset + VertexBuffer.POSITION_OFFSET + 8, 0F);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET, u);
		buffer.putFloat(offset + VertexBuffer.TEXTURE_OFFSET + 4, v);
		buffer.putInt(offset + VertexBuffer.COLOR_OFFSET, color);
	}

}