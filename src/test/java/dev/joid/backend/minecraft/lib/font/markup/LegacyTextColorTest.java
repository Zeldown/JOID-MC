package dev.joid.backend.minecraft.lib.font.markup;

import org.junit.Assert;
import org.junit.Test;

public class LegacyTextColorTest {

	@Test
	public void findsAColorByItsCodeWhateverTheCase() {
		Assert.assertEquals(LegacyTextColor.GREEN, LegacyTextColor.byCode('a'));
		Assert.assertEquals(LegacyTextColor.GREEN, LegacyTextColor.byCode('A'));
		Assert.assertNull(LegacyTextColor.byCode('k'));
	}

	@Test
	public void findsAColorByItsRgbWhateverTheAlpha() {
		Assert.assertEquals(LegacyTextColor.GOLD, LegacyTextColor.byRgb(0xFFFFAA00));
		Assert.assertNull(LegacyTextColor.byRgb(0x123456));
	}

}