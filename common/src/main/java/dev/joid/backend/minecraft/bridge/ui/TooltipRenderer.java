package dev.joid.backend.minecraft.bridge.ui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.bridge.render.MinecraftGlState;
import dev.joid.lib.font.converter.TextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TooltipRenderer {

	private static final TooltipScreen SCREEN = new TooltipScreen();

	public static void draw(final PoseStack pose, final Object content, final int mouseX, final int mouseY) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		TooltipRenderer.SCREEN.init(minecraft, window.getGuiScaledWidth(), window.getGuiScaledHeight());
		MinecraftGlState.preserve(() -> {
			if (content instanceof ItemStack) {
				TooltipRenderer.SCREEN.drawStack(pose, (ItemStack) content, mouseX, mouseY);
				return;
			}

			final List<Component> lines = TooltipRenderer.getLines(content);
			if (!lines.isEmpty()) {
				TooltipRenderer.SCREEN.renderComponentTooltip(pose, lines, mouseX, mouseY);
			}
		});
	}

	private static Component getLine(final Object line) {
		return line instanceof Component ? (Component) line : new TextComponent(TextConverter.convert(line));
	}

	private static List<Component> getLines(final Object content) {
		final List<Component> lines = new ArrayList<>();
		if (content instanceof Iterable<?>) {
			for (final Object line : (Iterable<?>) content) {
				lines.add(TooltipRenderer.getLine(line));
			}
		} else {
			lines.add(TooltipRenderer.getLine(content));
		}
		return lines;
	}

	private static final class TooltipScreen extends Screen {

		private TooltipScreen() {
			super(TextComponent.EMPTY);
		}

		private void drawStack(final PoseStack pose, final ItemStack stack, final int mouseX, final int mouseY) {
			super.renderTooltip(pose, stack, mouseX, mouseY);
		}

	}

}