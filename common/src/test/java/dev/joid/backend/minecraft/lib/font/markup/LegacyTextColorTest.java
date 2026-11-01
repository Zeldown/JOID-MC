package dev.joid.backend.minecraft.lib.font.markup;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.util.text.TextFormatting;

public class LegacyTextColorTest {

	@Test
	public void matchesTheMinecraftPalette() {
		for (final LegacyTextColor color : LegacyTextColor.values()) {
			final TextFormatting formatting = TextFormatting.valueOf(color.name());
			Assert.assertNotNull(formatting);
			Assert.assertEquals(color.getCode(), formatting.toString().charAt(1));
			Assert.assertTrue(formatting.isColor());
		}
	}

	@Test
	public void coversEveryMinecraftColor() {
		for (final TextFormatting formatting : TextFormatting.values()) {
			Assert.assertEquals(formatting.isColor(), LegacyTextColor.byCode(formatting.toString().charAt(1)) != null);
		}
	}

}