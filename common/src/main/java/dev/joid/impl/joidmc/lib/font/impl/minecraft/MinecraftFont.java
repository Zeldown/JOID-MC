package dev.joid.impl.joidmc.lib.font.impl.minecraft;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.lib.font.IFont;
import dev.joid.lib.font.IFontProvider;
import dev.joid.lib.font.dto.FontBounds;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.dto.TextStyle;
import dev.joid.lib.font.dto.markup.TextMarkup;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class MinecraftFont implements IFont, IFontProvider {

	public static final MinecraftFont MINECRAFT = new MinecraftFont();

	private MinecraftFont() {}

	@Override
	public @NonNull IFontProvider getFontProvider() {
		return this;
	}

	@Override
	public @NonNull FontBounds drawText(final double x, final double y, final @NonNull String text, final @NonNull TextInfo info) {
		final Color shadow = info.getShadowColor();
		if (shadow != null) {
			this.render(text, x + info.getShadowX(), y + info.getShadowY(), info, shadow);
		}

		this.render(text, x, y, info, null);
		return new FontBounds(this.getWidth(text, info), this.getHeight(text, info));
	}

	@Override
	public double getWidth(final @NonNull String text, final @NonNull TextInfo info) {
		final Font font = Minecraft.getInstance().font;
		final TextStyle style = info.getStyle().derive();
		final double scale = MinecraftFont.getScale(info);
		final double spacing = info.getLetterSpacing() * info.getFontSize();
		double width = 0D;
		double current = 0D;
		for (int index = 0; index < text.length();) {
			final int consumed = TextMarkup.parse(info.getMarkups(), text, index, style);
			if (consumed > 0) {
				index += consumed;
				continue;
			}

			final int codepoint = text.codePointAt(index);
			index += Character.charCount(codepoint);
			if (codepoint == '\n') {
				width   = Math.max(width, current);
				current = 0D;
				continue;
			}

			current += font.width(FormattedCharSequence.forward(Character.toString(codepoint), MinecraftFont.getStyle(style))) * scale + spacing;
		}

		return Math.max(width, current);
	}

	@Override
	public double getHeight(final @NonNull String text, final @NonNull TextInfo info) {
		return text.split("\n", -1).length * this.getLineHeight(info);
	}

	@Override
	public double getLineHeight(final @NonNull TextInfo info) {
		return (info.getLineHeight() > 0F ? info.getLineHeight() : 1D) * info.getFontSize();
	}

	private void render(final String text, final double x, final double y, final TextInfo info, final Color shadow) {
		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		final Font font = Minecraft.getInstance().font;
		final TextStyle style = info.getStyle().derive();
		final StringBuilder run = new StringBuilder();
		final double scale = MinecraftFont.getScale(info);
		final double spacing = info.getLetterSpacing() * info.getFontSize();
		final double lineHeight = this.getLineHeight(info);
		double cursor = x;
		double line = y;
		int color = MinecraftFont.getColor(style, shadow);
		Style format = MinecraftFont.getStyle(style);
		for (int index = 0; index < text.length();) {
			final int consumed = TextMarkup.parse(info.getMarkups(), text, index, style);
			if (consumed > 0) {
				index += consumed;
				cursor = MinecraftFont.flush(render, font, run, cursor, line, scale, spacing, color, format);
				color  = MinecraftFont.getColor(style, shadow);
				format = MinecraftFont.getStyle(style);
				continue;
			}

			final int codepoint = text.codePointAt(index);
			index += Character.charCount(codepoint);
			if (codepoint == '\n') {
				MinecraftFont.flush(render, font, run, cursor, line, scale, spacing, color, format);
				cursor = x;
				line  += lineHeight;
				continue;
			}

			run.appendCodePoint(codepoint);
			if (spacing != 0D) {
				cursor = MinecraftFont.flush(render, font, run, cursor, line, scale, spacing, color, format);
			}
		}

		MinecraftFont.flush(render, font, run, cursor, line, scale, spacing, color, format);
	}

	private static double flush(final RenderBridge render, final Font font, final StringBuilder run, final double x, final double y, final double scale, final double spacing, final int color, final Style format) {
		if (run.isEmpty()) {
			return x;
		}

		final String value = run.toString();
		run.setLength(0);
		render.getGlyph().draw(font.prepareText(FormattedCharSequence.forward(value, format), 0F, 0F, color, false, false, 0), x, y, scale, scale);
		return x + font.width(FormattedCharSequence.forward(value, format)) * scale + spacing;
	}

	private static Style getStyle(final TextStyle style) {
		return Style.EMPTY
				.withBold(style.getWeight().getValue() >= FontWeight.BOLD.getValue())
				.withItalic(style.isItalic())
				.withUnderlined(style.getEffects().contains(MinecraftFormat.UNDERLINE))
				.withStrikethrough(style.getEffects().contains(MinecraftFormat.STRIKETHROUGH))
				.withObfuscated(style.getEffects().contains(MinecraftFormat.OBFUSCATED));
	}

	private static int getColor(final TextStyle style, final Color shadow) {
		return (shadow != null ? shadow : style.getColor()).getRGB();
	}

	private static double getScale(final TextInfo info) {
		return info.getFontSize() / (double) Minecraft.getInstance().font.lineHeight;
	}

}