package dev.joid.impl.joidmc.lib.font.impl.minecraft;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import be.zeldown.joid.lib.font.FontProvider;
import be.zeldown.joid.lib.font.dto.font.FontBounds;
import be.zeldown.joid.lib.font.dto.font.IFont;
import be.zeldown.joid.lib.font.dto.text.TextInfo;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public final class MinecraftFont implements IFont, FontProvider {

	public static final MinecraftFont MINECRAFT = new MinecraftFont();

	private MinecraftFont() {}

	@Override
	public @NonNull FontProvider getFontProvider() {
		return this;
	}

	@Override
	public @NonNull FontBounds drawText(final double x, final double y, final @NonNull String text, final @NonNull TextInfo info) {
		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		final String[] lineArray = text.split("\n", -1);
		for (int index = 0; index < lineArray.length; index++) {
			render.text(lineArray[index], x, y + index * this.getLineHeight(info), MinecraftFont.getScale(info), info.getColor().getRGB(), info.getShadowColor() != null);
		}
		return new FontBounds(this.getWidth(text, info), this.getHeight(text, info));
	}

	@Override
	public double getWidth(final @NonNull String text, final @NonNull TextInfo info) {
		final Font font = Minecraft.getInstance().font;
		int width = 0;
		for (final String line : text.split("\n", -1)) {
			width = Math.max(width, font.width(line));
		}
		return width * MinecraftFont.getScale(info);
	}

	@Override
	public double getHeight(final @NonNull String text, final @NonNull TextInfo info) {
		return text.split("\n", -1).length * this.getLineHeight(info);
	}

	@Override
	public double getLineHeight(final @NonNull TextInfo info) {
		return Minecraft.getInstance().font.lineHeight * MinecraftFont.getScale(info);
	}

	private static double getScale(final TextInfo info) {
		return info.getFontSize() / (double) Minecraft.getInstance().font.lineHeight;
	}

}