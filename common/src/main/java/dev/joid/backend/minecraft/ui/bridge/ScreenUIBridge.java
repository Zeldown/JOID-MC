package dev.joid.backend.minecraft.ui.bridge;

import java.util.ArrayList;
import java.util.List;

import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.composite.GuiCompositor;
import dev.joid.backend.minecraft.ui.screen.UIScreen;
import dev.joid.base.glfw.input.GlfwInputForwarder;
import dev.joid.lib.bridge.ui.StackUIBridge;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ScreenUIBridge extends StackUIBridge {

	private final GuiCompositor compositor;

	@Getter
	private final GlfwInputForwarder input;

	private UIScreen     screen;
	private boolean      replacing;
	private List<String> hoverList;
	private ItemStack    hoverStack;

	private ScreenUIBridge(final RenderBridge render) {
		this.compositor = GuiCompositor.create(render);
		this.input      = GlfwInputForwarder.create(this);
	}

	public static @NonNull ScreenUIBridge create(final @NonNull RenderBridge render) {
		return new ScreenUIBridge(render);
	}

	@Override
	public void open(final @NonNull UI ui) {
		this.replacing = true;
		try {
			super.open(ui);
		} finally {
			this.replacing = false;
		}

		if (!super.hasScreen()) {
			this.onLastScreenClose();
		}
	}

	public void removed(final @NonNull UIScreen screen) {
		if (this.screen == screen) {
			this.screen = null;
			super.closeAll();
		}
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		return window.getGuiScale() / (double) window.calculateScale(0, minecraft.isEnforceUnicode());
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull List<@NonNull String> lines, final double mouseX, final double mouseY) {
		this.hoverList = new ArrayList<>(lines);
	}

	public void drawHover(final @NonNull ItemStack stack) {
		this.hoverStack = stack;
	}

	public void extract(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		this.input.flush();
		super.update();
		this.compositor.composite(graphics, super::draw);

		final List<String> hoverList = this.hoverList;
		this.hoverList = null;
		if (hoverList != null && !hoverList.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, hoverList.stream().<Component>map(Component::literal).toList(), mouseX, mouseY);
		}

		final ItemStack hoverStack = this.hoverStack;
		this.hoverStack = null;
		if (hoverStack != null) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, hoverStack, mouseX, mouseY);
		}
	}

	@Override
	protected void onFirstScreenOpen() {
		if (this.screen == null) {
			this.screen = UIScreen.create(this);
			Minecraft.getInstance().gui.setScreen(this.screen);
		}
	}

	@Override
	protected void onLastScreenClose() {
		if (this.replacing || this.screen == null) {
			return;
		}

		final UIScreen screen = this.screen;
		this.screen = null;
		if (Minecraft.getInstance().gui.screen() == screen) {
			Minecraft.getInstance().gui.setScreen(null);
		}
	}

}