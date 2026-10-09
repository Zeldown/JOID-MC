package dev.joid.backend.minecraft.lib.font.dto;

import java.util.Optional;

import dev.joid.backend.minecraft.lib.font.dto.markup.LegacyTextColor;
import dev.joid.backend.minecraft.lib.font.dto.markup.LegacyTextMarkup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatTextConverter {

	public static @NonNull String of(final @NonNull Component component) {
		final StringBuilder text = new StringBuilder();
		final StringBuilder codes = new StringBuilder();
		component.visit((style, content) -> {
			final String next = ChatTextConverter.codes(style);
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

		ChatTextConverter.append(codes, style.isObfuscated(), 'k');
		ChatTextConverter.append(codes, style.isBold(), 'l');
		ChatTextConverter.append(codes, style.isStrikethrough(), 'm');
		ChatTextConverter.append(codes, style.isUnderlined(), 'n');
		ChatTextConverter.append(codes, style.isItalic(), 'o');
		return codes.toString();
	}

	private static void append(final @NonNull StringBuilder codes, final boolean enabled, final char code) {
		if (enabled) {
			codes.append(LegacyTextMarkup.PREFIX).append(code);
		}
	}

}