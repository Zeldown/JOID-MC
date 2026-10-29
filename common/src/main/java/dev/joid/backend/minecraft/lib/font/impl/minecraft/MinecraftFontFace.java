package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.impl.glyph.IFontFace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftFontFace implements IFontFace {

	private static final String ASCII = "\u00C0\u00C1\u00C2\u00C8\u00CA\u00CB\u00CD\u00D3\u00D4\u00D5\u00DA\u00DF\u00E3\u00F5\u011F\u0130\u0131\u0152\u0153\u015E\u015F\u0174\u0175\u017E\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00C7\u00FC\u00E9\u00E2\u00E4\u00E0\u00E5\u00E7\u00EA\u00EB\u00E8\u00EF\u00EE\u00EC\u00C4\u00C5\u00C9\u00E6\u00C6\u00F4\u00F6\u00F2\u00FB\u00F9\u00FF\u00D6\u00DC\u00F8\u00A3\u00D8\u00D7\u0192\u00E1\u00ED\u00F3\u00FA\u00F1\u00D1\u00AA\u00BA\u00BF\u00AE\u00AC\u00BD\u00BC\u00A1\u00AB\u00BB\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255D\u255C\u255B\u2510\u2514\u2534\u252C\u251C\u2500\u253C\u255E\u255F\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256B\u256A\u2518\u250C\u2588\u2584\u258C\u2590\u2580\u03B1\u03B2\u0393\u03C0\u03A3\u03C3\u03BC\u03C4\u03A6\u0398\u03A9\u03B4\u221E\u2205\u2208\u2229\u2261\u00B1\u2265\u2264\u2320\u2321\u00F7\u2248\u00B0\u2219\u00B7\u221A\u207F\u00B2\u25A0\u0000";

	private static byte[] glyphSizes;

	@Getter
	private final FontWeight weight;

	@Getter
	private final ResourceLocation texture;

	private final Supplier<FontRenderer> renderer;

	public static @NonNull MinecraftFontFace of(final @NonNull ResourceLocation texture, final @NonNull Supplier<FontRenderer> renderer, final @NonNull FontWeight weight) {
		return new MinecraftFontFace(weight, texture, renderer);
	}

	public boolean isBold() {
		return this.weight == FontWeight.BOLD;
	}

	@Override
	public boolean isItalic() {
		return false;
	}

	@Override
	public float getAscender() {
		return 7F / MinecraftFont.SIZE;
	}

	@Override
	public float getDescender() {
		return -2F / MinecraftFont.SIZE;
	}

	@Override
	public float getLineHeight() {
		return 9F / MinecraftFont.SIZE;
	}

	@Override
	public float getUnderlineY() {
		return -1F / MinecraftFont.SIZE;
	}

	@Override
	public float getUnderlineThickness() {
		return 1F / MinecraftFont.SIZE;
	}

	@Override
	public @NonNull String getName() {
		return this.texture.toString();
	}

	@Override
	public float getAdvance(final int codepoint) {
		final int width = this.getWidth(codepoint);
		return (width > 0 && this.isBold() ? width + 1 : width) / MinecraftFont.SIZE;
	}

	@Override
	public float getKerning(final int previous, final int current) {
		return 0F;
	}

	@Override
	public boolean hasGlyph(final int codepoint) {
		return true;
	}

	public @NonNull FontRenderer getRenderer() {
		return this.renderer.get();
	}

	public int getAsciiIndex(final int codepoint) {
		return codepoint <= 0 || codepoint > Character.MAX_VALUE || this.getRenderer().getUnicodeFlag() ? -1 : MinecraftFontFace.ASCII.indexOf(codepoint);
	}

	public int getWidth(final int codepoint) {
		if (codepoint == ' ') {
			return 4;
		}

		if (this.getAsciiIndex(codepoint) >= 0) {
			return this.getRenderer().getCharWidth((char) codepoint);
		}

		final int size = MinecraftFontFace.getGlyphSize(codepoint);
		return size == 0 ? 0 : ((size & 15) + 1 - (size >>> 4)) / 2 + 1;
	}

	public static int getGlyphSize(final int codepoint) {
		if (codepoint < 0 || codepoint > Character.MAX_VALUE) {
			return 0;
		}

		if (MinecraftFontFace.glyphSizes == null) {
			MinecraftFontFace.glyphSizes = MinecraftFontFace.readGlyphSizes();
		}
		return MinecraftFontFace.glyphSizes[codepoint] & 0xFF;
	}

	private static byte[] readGlyphSizes() {
		final byte[] sizes = new byte[Character.MAX_VALUE + 1];
		try (final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream()) {
			int offset = 0;
			for (int read = stream.read(sizes); read > 0 && offset < sizes.length; read = stream.read(sizes, offset, sizes.length - offset)) {
				offset += read;
			}
		} catch (final IOException exception) {
			throw new UncheckedIOException("The glyph sizes of the Minecraft font cannot be read", exception);
		}
		return sizes;
	}

}