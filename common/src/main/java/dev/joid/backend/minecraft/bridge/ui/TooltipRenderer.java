package dev.joid.backend.minecraft.bridge.ui;

import java.util.ArrayList;
import java.util.List;

import dev.joid.lib.font.converter.TextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TooltipRenderer {

	public static void draw(final GuiGraphicsExtractor graphics, final Object content, final int mouseX, final int mouseY) {
		if (content instanceof final ItemStack stack) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, stack, mouseX, mouseY);
			return;
		}

		final List<Component> lines = TooltipRenderer.getLines(content);
		if (!lines.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, mouseX, mouseY);
		}
	}

	private static Component getLine(final Object line) {
		return line instanceof final Component component ? component : Component.literal(TextConverter.convert(line));
	}

	private static List<Component> getLines(final Object content) {
		final List<Component> lines = new ArrayList<>();
		if (content instanceof final Iterable<?> iterable) {
			for (final Object line : iterable) {
				lines.add(TooltipRenderer.getLine(line));
			}
		} else {
			lines.add(TooltipRenderer.getLine(content));
		}
		return lines;
	}

}