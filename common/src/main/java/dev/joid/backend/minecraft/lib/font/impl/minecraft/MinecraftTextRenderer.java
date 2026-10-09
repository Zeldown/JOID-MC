package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import dev.joid.backend.minecraft.bridge.render.texture.GpuBorrowedTexture;
import dev.joid.lib.font.impl.bitmap.BitmapCell;
import dev.joid.lib.font.impl.bitmap.BitmapTextRenderer;
import dev.joid.lib.font.impl.glyph.TextGlyph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftTextRenderer extends BitmapTextRenderer<MinecraftFontFace> {

	private static final Matrix4fc             IDENTITY = new Matrix4f();
	private static final MinecraftTextRenderer INSTANCE = new MinecraftTextRenderer();

	private final GlyphVertexCapture capture = new GlyphVertexCapture();

	private GpuTextureView     view;
	private GpuBorrowedTexture texture;

	public static @NonNull MinecraftTextRenderer inst() {
		return MinecraftTextRenderer.INSTANCE;
	}

	@Override
	protected BitmapCell getCell(final @NonNull TextGlyph<MinecraftFontFace> glyph) {
		final MinecraftFontFace face = glyph.getFace();
		final TextRenderable.Styled renderable = face.getGlyph(glyph.getCodepoint()).createGlyph(0F, 0F, -1, 0, Style.EMPTY, 0F, 0F);
		if (renderable == null) {
			return null;
		}

		final GpuTextureView view = renderable.textureView();
		if (view != this.view) {
			this.view    = view;
			this.texture = GpuBorrowedTexture.create(view);
		}

		final GlyphVertexCapture capture = this.capture.reset();
		renderable.render(MinecraftTextRenderer.IDENTITY, capture, 0, true);
		final int width = view.getWidth(0);
		final int height = view.getHeight(0);
		return BitmapCell
				.create(this.texture, (int) Math.round(capture.getMinU() * width), (int) Math.round(capture.getMinV() * height), (int) Math.round(capture.getMaxU() * width), (int) Math.round(capture.getMaxV() * height))
				.bounds(capture.getMinX(), capture.getMinY(), capture.getMaxX(), capture.getMaxY())
				.grayscale(view.texture().getFormat() == GpuFormat.R8_UNORM)
				.bold(face.isBold());
	}

}