package dev.joid.impl.minecraft.lib.font.dto.markup;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.impl.minecraft.lib.font.dto.effect.ObfuscatedTextEffect;
import dev.joid.impl.minecraft.lib.font.dto.effect.StrikethroughTextEffect;
import dev.joid.impl.minecraft.lib.font.dto.effect.UnderlineTextEffect;
import dev.joid.lib.color.Color;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.dto.TextStyle;

public class LegacyTextMarkupTest {

	@Test
	public void ignoresTextWithoutThePrefix() {
		Assert.assertEquals(0, LegacyTextMarkup.inst().parse("abc", 0, LegacyTextMarkupTest.createStyle()));
	}

	@Test
	public void setsTheColorOfAColorCode() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		Assert.assertEquals(2, LegacyTextMarkup.inst().parse("\u00A7cHello", 0, style));
		Assert.assertEquals(new Color(0xFFFF5555), style.getColor());
	}

	@Test
	public void acceptsUppercaseCodes() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		LegacyTextMarkup.inst().parse("\u00A7A\u00A7L", 0, style);
		LegacyTextMarkup.inst().parse("\u00A7A\u00A7L", 2, style);
		Assert.assertEquals(new Color(0xFF55FF55), style.getColor());
		Assert.assertEquals(FontWeight.BOLD, style.getWeight());
	}

	@Test
	public void appliesTheFormattingCodes() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		final String text = "\u00A7k\u00A7l\u00A7m\u00A7n\u00A7o";
		for (int index = 0; index < text.length(); index += 2) {
			Assert.assertEquals(2, LegacyTextMarkup.inst().parse(text, index, style));
		}

		Assert.assertEquals(FontWeight.BOLD, style.getWeight());
		Assert.assertTrue(style.isItalic());
		Assert.assertTrue(style.getEffects().contains(ObfuscatedTextEffect.inst()));
		Assert.assertTrue(style.getEffects().contains(StrikethroughTextEffect.inst()));
		Assert.assertTrue(style.getEffects().contains(UnderlineTextEffect.inst()));
	}

	@Test
	public void clearsTheFormattingWhenTheColorChanges() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		LegacyTextMarkup.inst().parse("\u00A7l\u00A7n\u00A7e", 0, style);
		LegacyTextMarkup.inst().parse("\u00A7l\u00A7n\u00A7e", 2, style);
		LegacyTextMarkup.inst().parse("\u00A7l\u00A7n\u00A7e", 4, style);
		Assert.assertEquals(FontWeight.REGULAR, style.getWeight());
		Assert.assertTrue(style.getEffects().isEmpty());
		Assert.assertEquals(new Color(0xFFFFFF55), style.getColor());
	}

	@Test
	public void resetsToTheBaseStyle() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		LegacyTextMarkup.inst().parse("\u00A7c\u00A7o\u00A7r", 0, style);
		LegacyTextMarkup.inst().parse("\u00A7c\u00A7o\u00A7r", 2, style);
		LegacyTextMarkup.inst().parse("\u00A7c\u00A7o\u00A7r", 4, style);
		Assert.assertEquals(Color.BLACK, style.getColor());
		Assert.assertFalse(style.isItalic());
	}

	@Test
	public void consumesAnUnknownCodeLikeMinecraft() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		Assert.assertEquals(2, LegacyTextMarkup.inst().parse("\u00A7zHello", 0, style));
		Assert.assertEquals(Color.BLACK, style.getColor());
	}

	@Test
	public void consumesATrailingPrefix() {
		Assert.assertEquals(1, LegacyTextMarkup.inst().parse("Hello\u00A7", 5, LegacyTextMarkupTest.createStyle()));
	}

	@Test
	public void setsTheColorOfAHexCode() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		Assert.assertEquals(14, LegacyTextMarkup.inst().parse("\u00A7x\u00A71\u00A72\u00A7a\u00A7B\u00A7c\u00A7dHello", 0, style));
		Assert.assertEquals(new Color(0xFF12ABCD), style.getColor());
	}

	@Test
	public void consumesAnIncompleteHexCodeAsAnUnknownCode() {
		final TextStyle style = LegacyTextMarkupTest.createStyle();
		Assert.assertEquals(2, LegacyTextMarkup.inst().parse("\u00A7x\u00A71\u00A72Hello", 0, style));
		Assert.assertEquals(2, LegacyTextMarkup.inst().parse("\u00A7x\u00A71\u00A72\u00A7g\u00A7B\u00A7c\u00A7d", 0, style));
		Assert.assertEquals(Color.BLACK, style.getColor());
	}

	private static TextStyle createStyle() {
		return TextStyle.create(FontWeight.REGULAR, false, Color.BLACK).derive();
	}

}