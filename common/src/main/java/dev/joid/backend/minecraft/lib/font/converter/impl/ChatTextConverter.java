package dev.joid.backend.minecraft.lib.font.converter.impl;

import dev.joid.backend.minecraft.lib.font.markup.LegacyTextColor;
import dev.joid.backend.minecraft.lib.font.markup.impl.LegacyTextMarkup;
import dev.joid.lib.font.converter.ITextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatTextConverter implements ITextConverter {

	private static final ChatTextConverter INSTANCE = new ChatTextConverter();

	public static @NonNull ChatTextConverter inst() {
		return ChatTextConverter.INSTANCE;
	}

	@Override
	public boolean supports(final @NonNull Object text) {
		return text instanceof ITextComponent;
	}

	@Override
	public @NonNull String convert(final @NonNull Object text) {
		final StringBuilder converted = new StringBuilder();
		final StringBuilder codes = new StringBuilder();
		for (final ITextComponent component : (ITextComponent) text) {
			final String next = ChatTextConverter.codes(component.getStyle());
			if (!next.contentEquals(codes)) {
				converted.append(LegacyTextMarkup.PREFIX).append('r').append(next);
				codes.setLength(0);
				codes.append(next);
			}

			converted.append(component.getUnformattedComponentText());
		}
		return converted.toString();
	}

	private static @NonNull String codes(final Style style) {
		final StringBuilder codes = new StringBuilder();
		final TextFormatting formatting = style.getColor();
		final LegacyTextColor color = formatting != null && formatting.isColor() ? LegacyTextColor.byCode(Character.forDigit(formatting.getColorIndex(), 16)) : null;
		if (color != null) {
			codes.append(LegacyTextMarkup.PREFIX).append(color.getCode());
		}

		ChatTextConverter.append(codes, style.getObfuscated(), 'k');
		ChatTextConverter.append(codes, style.getBold(), 'l');
		ChatTextConverter.append(codes, style.getStrikethrough(), 'm');
		ChatTextConverter.append(codes, style.getUnderlined(), 'n');
		ChatTextConverter.append(codes, style.getItalic(), 'o');
		return codes.toString();
	}

	private static void append(final StringBuilder codes, final boolean enabled, final char code) {
		if (enabled) {
			codes.append(LegacyTextMarkup.PREFIX).append(code);
		}
	}

}