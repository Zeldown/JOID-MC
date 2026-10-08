package dev.joid.backend.minecraft.lib.font.dto.markup;

import dev.joid.backend.minecraft.lib.font.dto.effect.ObfuscatedTextEffect;
import dev.joid.backend.minecraft.lib.font.dto.effect.StrikethroughTextEffect;
import dev.joid.backend.minecraft.lib.font.dto.effect.UnderlineTextEffect;
import dev.joid.lib.color.Color;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.dto.TextStyle;
import dev.joid.lib.font.dto.markup.ITextMarkup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LegacyTextMarkup implements ITextMarkup {

	public static final char PREFIX = '\u00A7';

	private static final LegacyTextMarkup INSTANCE = new LegacyTextMarkup();

	public static @NonNull LegacyTextMarkup inst() {
		return LegacyTextMarkup.INSTANCE;
	}

	@Override
	public int parse(final @NonNull String text, final int index, final @NonNull TextStyle style) {
		if (text.charAt(index) != LegacyTextMarkup.PREFIX) {
			return 0;
		}

		if (index + 1 >= text.length()) {
			return 1;
		}

		final char code = Character.toLowerCase(text.charAt(index + 1));
		final LegacyTextColor color = LegacyTextColor.byCode(code);
		if (color != null) {
			style.reset().color(color.getColor());
			return 2;
		}

		switch (code) {
		case 'x':
			return LegacyTextMarkup.parseHex(text, index, style);
		case 'k':
			style.effect(ObfuscatedTextEffect.inst());
			break;
		case 'l':
			style.weight(FontWeight.BOLD);
			break;
		case 'm':
			style.effect(StrikethroughTextEffect.inst());
			break;
		case 'n':
			style.effect(UnderlineTextEffect.inst());
			break;
		case 'o':
			style.italic(true);
			break;
		case 'r':
			style.reset();
			break;
		default:
			break;
		}

		return 2;
	}

	private static int parseHex(final @NonNull String text, final int index, final @NonNull TextStyle style) {
		if (index + 14 > text.length()) {
			return 2;
		}

		int rgb = 0;
		for (int digit = 0; digit < 6; digit++) {
			final int position = index + 2 + digit * 2;
			final int value = Character.digit(text.charAt(position + 1), 16);
			if (text.charAt(position) != LegacyTextMarkup.PREFIX || value < 0) {
				return 2;
			}
			rgb = rgb << 4 | value;
		}

		style.reset().color(new Color(0xFF000000 | rgb));
		return 14;
	}

}