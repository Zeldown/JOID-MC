package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.font.impl.bitmap.BitmapCell;
import dev.joid.lib.font.impl.bitmap.BitmapTextRenderer;
import dev.joid.lib.font.impl.glyph.TextGlyph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftTextRenderer extends BitmapTextRenderer<MinecraftFontFace> {

	private static final MinecraftTextRenderer INSTANCE = new MinecraftTextRenderer();

	private static final Matrix4f IDENTITY = Matrix4f.createScaleMatrix(1F, 1F, 1F);

	private final GlyphVertexCapture     capture  = new GlyphVertexCapture();
	private final Map<RenderType, Sheet> sheetMap = new HashMap<>();

	public static @NonNull MinecraftTextRenderer inst() {
		return MinecraftTextRenderer.INSTANCE;
	}

	@Override
	protected BitmapCell getCell(final @NonNull TextGlyph<MinecraftFontFace> glyph) {
		final MinecraftFontFace face = glyph.getFace();
		final BakedGlyph[] baked = new BakedGlyph[1];
		BridgeHandler.RENDER.get().suspend(() -> baked[0] = face.getGlyph(glyph.getCodepoint()));
		final GlyphVertexCapture capture = this.capture.reset();
		baked[0].render(false, 0F, 0F, MinecraftTextRenderer.IDENTITY, capture, 1F, 1F, 1F, 1F, 0);
		if (capture.isEmpty()) {
			return null;
		}

		final Sheet sheet = this.sheetMap.computeIfAbsent(baked[0].renderType(Font.DisplayMode.NORMAL), MinecraftTextRenderer::readSheet);
		final int width = sheet.texture.getWidth();
		final int height = sheet.texture.getHeight();
		return BitmapCell
				.create(sheet.texture, Math.round(capture.getMinU() * width), Math.round(capture.getMinV() * height), Math.round(capture.getMaxU() * width), Math.round(capture.getMaxV() * height))
				.bounds(capture.getMinX(), capture.getMinY(), capture.getMaxX(), capture.getMaxY())
				.grayscale(sheet.grayscale)
				.bold(face.isBold());
	}

	private static Sheet readSheet(final RenderType type) {
		final int[] sheet = new int[2];
		BridgeHandler.RENDER.get().suspend(() -> {
			type.setupRenderState();
			try {
				sheet[0] = RenderSystem.getShaderTexture(0);
				GlStateManager._bindTexture(sheet[0]);
				sheet[1] = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
			} finally {
				type.clearRenderState();
			}
		});
		return new Sheet(GlBorrowedTexture.of(sheet[0]), sheet[1] == GL30.GL_R8 || sheet[1] == GL11.GL_RED);
	}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class Sheet {

		private final ITexture texture;
		private final boolean  grayscale;

	}

}