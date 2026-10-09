package dev.joid.backend.minecraft.lib.font.markup;

import dev.joid.lib.color.Color;
import lombok.Getter;

@Getter
public enum LegacyTextColor {

	BLACK('0', 0x000000),
	DARK_BLUE('1', 0x0000AA),
	DARK_GREEN('2', 0x00AA00),
	DARK_AQUA('3', 0x00AAAA),
	DARK_RED('4', 0xAA0000),
	DARK_PURPLE('5', 0xAA00AA),
	GOLD('6', 0xFFAA00),
	GRAY('7', 0xAAAAAA),
	DARK_GRAY('8', 0x555555),
	BLUE('9', 0x5555FF),
	GREEN('a', 0x55FF55),
	AQUA('b', 0x55FFFF),
	RED('c', 0xFF5555),
	LIGHT_PURPLE('d', 0xFF55FF),
	YELLOW('e', 0xFFFF55),
	WHITE('f', 0xFFFFFF);

	private final char  code;
	private final int   rgb;
	private final Color color;

	private LegacyTextColor(final char code, final int rgb) {
		this.code = code;
		this.rgb = rgb;
		this.color = new Color(0xFF000000 | rgb);
	}

	public static LegacyTextColor byRgb(final int rgb) {
		for (final LegacyTextColor color : LegacyTextColor.values()) {
			if (color.rgb == (rgb & 0xFFFFFF)) {
				return color;
			}
		}

		return null;
	}

	public static LegacyTextColor byCode(final char code) {
		final char lower = Character.toLowerCase(code);
		for (final LegacyTextColor color : LegacyTextColor.values()) {
			if (color.code == lower) {
				return color;
			}
		}

		return null;
	}

}