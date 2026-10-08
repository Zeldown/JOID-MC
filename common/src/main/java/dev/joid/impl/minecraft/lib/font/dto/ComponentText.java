package dev.joid.impl.minecraft.lib.font.dto;

import java.util.Optional;

import dev.joid.impl.minecraft.lib.font.dto.markup.LegacyTextColor;
import dev.joid.impl.minecraft.lib.font.dto.markup.LegacyTextMarkup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComponentText {

	public static @NonNull String of(final @NonNull Component component) {
		final StringBuilder text = new StringBuilder();
		final StringBuilder codes = new StringBuilder();
		component.visit((style, content) -> {
			final String next = ComponentText.codes(style);
			if (!next.contentEquals(codes)) {
				text.append(LegacyTextMarkup.PREFIX).append('r').append(next);
				codes.setLength(0);
				codes.append(next);
			}

			text.append(content);
			return Optional.empty();
		}, Style.EMPTY);
		return text.toString();
	}

	private static @NonNull String codes(final @NonNull Style style) {
		final StringBuilder codes = new StringBuilder();
		final TextColor color = style.getColor();
		if (color != null) {
			final LegacyTextColor legacy = LegacyTextColor.byRgb(color.getValue());
			if (legacy != null) {
				codes.append(LegacyTextMarkup.PREFIX).append(legacy.getCode());
			} else {
				codes.append(LegacyTextMarkup.PREFIX).append('x');
				for (final char digit : String.format("%06x", color.getValue() & 0xFFFFFF).toCharArray()) {
					codes.append(LegacyTextMarkup.PREFIX).append(digit);
				}
			}
		}

		ComponentText.append(codes, style.isObfuscated(), 'k');
		ComponentText.append(codes, style.isBold(), 'l');
		ComponentText.append(codes, style.isStrikethrough(), 'm');
		ComponentText.append(codes, style.isUnderlined(), 'n');
		ComponentText.append(codes, style.isItalic(), 'o');
		return codes.toString();
	}

	private static void append(final @NonNull StringBuilder codes, final boolean enabled, final char code) {
		if (enabled) {
			codes.append(LegacyTextMarkup.PREFIX).append(code);
		}
	}

}