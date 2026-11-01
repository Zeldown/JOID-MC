package dev.joid.backend.minecraft.bridge.ui;

import java.util.List;

import dev.joid.backend.minecraft.bridge.render.MinecraftGlState;
import dev.joid.lib.font.converter.TextConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TooltipRenderer {

	private static final TooltipScreen SCREEN = new TooltipScreen();

	public static void draw(final Object content, final int mouseX, final int mouseY) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final ScaledResolution resolution = new ScaledResolution(minecraft);
		TooltipRenderer.SCREEN.setWorldAndResolution(minecraft, resolution.getScaledWidth(), resolution.getScaledHeight());
		MinecraftGlState.preserve(() -> {
			if (content instanceof ItemStack) {
				TooltipRenderer.SCREEN.drawStack((ItemStack) content, mouseX, mouseY);
				return;
			}

			final List<String> lines = TextConverter.convertLines(content);
			if (!lines.isEmpty()) {
				TooltipRenderer.SCREEN.drawLines(lines, mouseX, mouseY);
			}
		});
	}

	private static final class TooltipScreen extends GuiScreen {

		private void drawStack(final ItemStack stack, final int mouseX, final int mouseY) {
			super.renderToolTip(stack, mouseX, mouseY);
		}

		private void drawLines(final List<String> lines, final int mouseX, final int mouseY) {
			super.drawHoveringText(lines, mouseX, mouseY, super.fontRenderer);
		}

	}

}