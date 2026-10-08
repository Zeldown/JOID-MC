package dev.joid.impl.minecraft.lib.font.dto.markup;

import org.junit.Assert;
import org.junit.Test;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public class LegacyTextColorTest {

	@Test
	public void matchesTheMinecraftPalette() {
		for (final LegacyTextColor color : LegacyTextColor.values()) {
			final ChatFormatting formatting = ChatFormatting.getByCode(color.getCode());
			Assert.assertNotNull(formatting);
			Assert.assertEquals(color.name(), formatting.name());
			Assert.assertEquals(TextColor.fromLegacyFormat(formatting).getValue(), color.getRgb());
		}
	}

	@Test
	public void coversEveryMinecraftColor() {
		for (final ChatFormatting formatting : ChatFormatting.values()) {
			Assert.assertEquals(TextColor.fromLegacyFormat(formatting) != null, LegacyTextColor.byCode(formatting.toString().charAt(1)) != null);
		}
	}

	@Test
	public void findsAColorByItsRgbWhateverTheAlpha() {
		Assert.assertEquals(LegacyTextColor.GOLD, LegacyTextColor.byRgb(0xFFFFAA00));
		Assert.assertNull(LegacyTextColor.byRgb(0x123456));
	}

}