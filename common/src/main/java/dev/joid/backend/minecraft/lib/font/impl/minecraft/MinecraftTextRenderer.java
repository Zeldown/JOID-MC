package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.util.HashMap;
import java.util.Map;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.font.impl.bitmap.BitmapCell;
import dev.joid.lib.font.impl.bitmap.BitmapTextRenderer;
import dev.joid.lib.font.impl.glyph.TextGlyph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftTextRenderer extends BitmapTextRenderer<MinecraftFontFace> {

	private static final MinecraftTextRenderer INSTANCE = new MinecraftTextRenderer();

	private final Map<ResourceLocation, ITexture> textures = new HashMap<>();

	public static @NonNull MinecraftTextRenderer inst() {
		return MinecraftTextRenderer.INSTANCE;
	}

	@Override
	protected BitmapCell getCell(final @NonNull TextGlyph<MinecraftFontFace> glyph) {
		final MinecraftFontFace face = glyph.getFace();
		final int codepoint = glyph.getCodepoint();
		if (codepoint == ' ') {
			return null;
		}

		final int index = face.getAsciiIndex(codepoint);
		if (index >= 0) {
			final int width = face.getRenderer().getCharWidth((char) codepoint) - 1;
			if (width <= 0) {
				return null;
			}

			final ITexture texture = this.getTexture(face.getTexture());
			final double scale = texture.getWidth() / 128D;
			final int left = (int) Math.round(index % 16 * 8 * scale);
			final int top = (int) Math.round(index / 16 * 8 * scale);
			return BitmapCell.create(texture, left, top, (int) Math.round((index % 16 * 8 + width) * scale), (int) Math.round((index / 16 * 8 + 8) * scale)).bounds(0D, 0D, width, 8D).bold(face.isBold());
		}

		final int size = MinecraftFontFace.getGlyphSize(codepoint);
		final int start = size >>> 4;
		final int end = (size & 15) + 1;
		if (size == 0 || end <= start) {
			return null;
		}

		final ITexture texture = this.getTexture(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", codepoint >> 8)));
		final double scale = texture.getWidth() / 256D;
		final int column = codepoint % 16 * 16;
		final int row = (codepoint & 255) / 16 * 16;
		return BitmapCell.create(texture, (int) Math.round((column + start) * scale), (int) Math.round(row * scale), (int) Math.round((column + end) * scale), (int) Math.round((row + 16) * scale)).bounds(0D, 0D, (end - start) / 2D, 8D).bold(face.isBold());
	}

	private ITexture getTexture(final ResourceLocation location) {
		return this.textures.computeIfAbsent(location, key -> GlBorrowedTexture.of(() -> MinecraftTextRenderer.getTextureId(key)));
	}

	private static int getTextureId(final ResourceLocation location) {
		final TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		final ITextureObject texture = manager.getTexture(location);
		if (texture != null) {
			return texture.getGlTextureId();
		}

		final SimpleTexture loaded = new SimpleTexture(location);
		BridgeHandler.RENDER.get().suspend(() -> manager.loadTexture(location, loaded));
		return loaded.getGlTextureId();
	}

}