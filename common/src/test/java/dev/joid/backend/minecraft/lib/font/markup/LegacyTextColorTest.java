package dev.joid.backend.minecraft.lib.font.markup;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.util.EnumChatFormatting;

public class LegacyTextColorTest {

	@Test
	public void matchesTheMinecraftPalette() {
		for (final LegacyTextColor color : LegacyTextColor.values()) {
			final EnumChatFormatting formatting = EnumChatFormatting.valueOf(color.name());
			Assert.assertNotNull(formatting);
			Assert.assertEquals(color.getCode(), formatting.getFormattingCode());
			Assert.assertTrue(formatting.isColor());
		}
	}

	@Test
	public void coversEveryMinecraftColor() {
		for (final EnumChatFormatting formatting : EnumChatFormatting.values()) {
			Assert.assertEquals(formatting.isColor(), LegacyTextColor.byCode(formatting.getFormattingCode()) != null);
		}
	}

}