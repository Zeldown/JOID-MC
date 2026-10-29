package dev.joid.backend.minecraft.lib.font.converter.impl;

import dev.joid.lib.font.converter.ITextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.util.IChatComponent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatTextConverter implements ITextConverter {

	private static final ChatTextConverter INSTANCE = new ChatTextConverter();

	public static @NonNull ChatTextConverter inst() {
		return ChatTextConverter.INSTANCE;
	}

	@Override
	public boolean supports(final @NonNull Object text) {
		return text instanceof IChatComponent;
	}

	@Override
	public @NonNull String convert(final @NonNull Object text) {
		return ((IChatComponent) text).getFormattedText();
	}

}