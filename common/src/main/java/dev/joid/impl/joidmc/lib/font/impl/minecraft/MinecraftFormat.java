package dev.joid.impl.joidmc.lib.font.impl.minecraft;

import dev.joid.lib.color.Color;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.dto.TextStyle;
import dev.joid.lib.font.dto.effect.ITextEffect;
import dev.joid.lib.font.dto.markup.ITextMarkup;
import lombok.NonNull;

public final class MinecraftFormat implements ITextMarkup {

	public static final MinecraftFormat MARKUP = new MinecraftFormat();

	public static final ITextEffect UNDERLINE     = new ITextEffect() {};
	public static final ITextEffect STRIKETHROUGH = new ITextEffect() {};
	public static final ITextEffect OBFUSCATED    = new ITextEffect() {};

	private static final String  CODE_LIST  = "0123456789abcdef";
	private static final Color[] COLOR_LIST = {
			new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170),
			new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170),
			new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255),
			new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255)};

	private MinecraftFormat() {}

	@Override
	public int parse(final @NonNull String text, final int index, final @NonNull TextStyle style) {
		if (text.charAt(index) != '&' || index + 1 >= text.length()) {
			return 0;
		}

		final char code = Character.toLowerCase(text.charAt(index + 1));
		final int color = MinecraftFormat.CODE_LIST.indexOf(code);
		if (color >= 0) {
			style.color(MinecraftFormat.COLOR_LIST[color]);
			return 2;
		}

		switch (code) {
			case 'l' -> style.weight(FontWeight.BOLD);
			case 'o' -> style.italic(true);
			case 'n' -> style.effect(MinecraftFormat.UNDERLINE);
			case 'm' -> style.effect(MinecraftFormat.STRIKETHROUGH);
			case 'k' -> style.effect(MinecraftFormat.OBFUSCATED);
			case 'r' -> MinecraftFormat.reset(style);
			default  -> {
				return 0;
			}
		}

		return 2;
	}

	private static void reset(final TextStyle style) {
		final TextStyle base = style.getBase();
		style.color(base.getColor()).italic(base.isItalic()).weight(base.getWeight());
		style.removeEffect(MinecraftFormat.UNDERLINE);
		style.removeEffect(MinecraftFormat.STRIKETHROUGH);
		style.removeEffect(MinecraftFormat.OBFUSCATED);
	}

}