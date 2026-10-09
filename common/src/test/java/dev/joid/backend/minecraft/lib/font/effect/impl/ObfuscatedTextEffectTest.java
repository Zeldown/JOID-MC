package dev.joid.backend.minecraft.lib.font.effect.impl;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.color.Color;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.ITextRenderer;
import dev.joid.lib.font.TextStyle;
import dev.joid.lib.font.impl.glyph.FontFamily;
import dev.joid.lib.font.impl.glyph.GlyphFont;
import dev.joid.lib.font.impl.glyph.IFontFace;
import dev.joid.lib.font.impl.glyph.TextGlyph;
import lombok.NonNull;

public class ObfuscatedTextEffectTest {

	@Test
	public void replacesAGlyphByAGlyphOfTheSameAdvance() {
		for (int attempt = 0; attempt < 100; attempt++) {
			final TextGlyph<WidthFontFace> glyph = ObfuscatedTextEffectTest.createGlyph('i');
			ObfuscatedTextEffect.inst().apply(glyph);
			Assert.assertEquals(2F, glyph.getFace().getAdvance(glyph.getCodepoint()), 0F);
		}
	}

	@Test
	public void keepsTheSpaces() {
		final TextGlyph<WidthFontFace> glyph = ObfuscatedTextEffectTest.createGlyph(' ');
		ObfuscatedTextEffect.inst().apply(glyph);
		Assert.assertEquals(' ', glyph.getCodepoint());
	}

	@Test
	public void keepsAGlyphWithoutCandidate() {
		final TextGlyph<WidthFontFace> glyph = ObfuscatedTextEffectTest.createGlyph(0x2603);
		ObfuscatedTextEffect.inst().apply(glyph);
		Assert.assertEquals(0x2603, glyph.getCodepoint());
	}

	private static TextGlyph<WidthFontFace> createGlyph(final int codepoint) {
		final WidthFontFace face = new WidthFontFace();
		return TextGlyph.create(new WidthFont(face), face, 0, codepoint, TextStyle.create(FontWeight.REGULAR, false, Color.BLACK), 0D, 0D, 1D, 1D, Color.BLACK);
	}

	private static final class WidthFont extends GlyphFont<WidthFontFace> {

		private WidthFont(final WidthFontFace face) {
			super(FontFamily.of(face));
		}

		@Override
		public @NonNull ITextRenderer getTextRenderer() {
			throw new UnsupportedOperationException("The font of the test has no renderer");
		}

	}

	private static final class WidthFontFace implements IFontFace {

		@Override
		public boolean isItalic() {
			return false;
		}

		@Override
		public float getAscender() {
			return 1F;
		}

		@Override
		public float getDescender() {
			return 0F;
		}

		@Override
		public float getLineHeight() {
			return 1F;
		}

		@Override
		public float getUnderlineY() {
			return 0F;
		}

		@Override
		public float getUnderlineThickness() {
			return 0F;
		}

		@Override
		public @NonNull String getName() {
			return "width";
		}

		@Override
		public @NonNull FontWeight getWeight() {
			return FontWeight.REGULAR;
		}

		@Override
		public float getAdvance(final int codepoint) {
			if (codepoint == 0x2603) {
				return 9F;
			}

			return codepoint == 'i' || codepoint == 'l' || codepoint == '!' ? 2F : 6F;
		}

		@Override
		public float getKerning(final int previous, final int current) {
			return 0F;
		}

		@Override
		public boolean hasGlyph(final int codepoint) {
			return true;
		}

	}

}