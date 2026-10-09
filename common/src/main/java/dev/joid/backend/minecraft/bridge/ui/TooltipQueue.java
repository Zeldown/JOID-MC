package dev.joid.backend.minecraft.bridge.ui;

import java.util.ArrayList;
import java.util.List;

import dev.joid.lib.font.converter.TextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TooltipQueue {

	private Object content;

	public static @NonNull TooltipQueue create() {
		return new TooltipQueue();
	}

	public void push(final @NonNull Object content) {
		this.content = content;
	}

	public void flush(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		final Object content = this.content;
		this.content = null;
		if (content instanceof final ItemStack stack) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, stack, mouseX, mouseY);
		} else if (content != null) {
			final List<Component> lines = TooltipQueue.getLines(content);
			if (!lines.isEmpty()) {
				graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, mouseX, mouseY);
			}
		}
	}

	private static List<Component> getLines(final Object content) {
		final List<Component> lines = new ArrayList<>();
		if (content instanceof final Iterable<?> iterable) {
			for (final Object line : iterable) {
				lines.add(TooltipQueue.getLine(line));
			}
		} else {
			lines.add(TooltipQueue.getLine(content));
		}
		return lines;
	}

	private static Component getLine(final Object line) {
		return line instanceof final Component component ? component : Component.literal(TextConverter.convert(line));
	}

}