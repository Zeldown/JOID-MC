package dev.joid.backend.minecraft.bridge.ui.screen;

import java.util.ArrayList;
import java.util.List;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.IItemHoverBridge;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.base.glfw.input.GlfwInputForwarder;
import dev.joid.lib.bridge.ui.StackUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlayObject;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ScreenUIBridge extends StackUIBridge implements IItemHoverBridge {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	@Getter
	private final GlfwInputForwarder input;

	private UIScreen     screen;
	private boolean      replacing;
	private List<String> hoverList;
	private ItemStack    hoverStack;

	private ScreenUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = GuiCompositor.create(render);
		this.input      = GlfwInputForwarder.create(this);
	}

	public static @NonNull ScreenUIBridge create(final @NonNull MinecraftRenderBridge render, final @NonNull MinecraftWindowBridge window) {
		return new ScreenUIBridge(render, window);
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
	public boolean canHandle(final @NonNull UI ui) {
		return !ui.getOverlay().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return !UIDataOverlayObject.getOrDefault(clazz).active();
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull List<@NonNull String> lines, final double mouseX, final double mouseY) {
		this.hoverList = new ArrayList<>(lines);
	}

	@Override
	public void drawHover(final @NonNull ItemStack stack) {
		this.hoverStack = stack;
	}

	public void frame(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		this.input.flush();
		super.update();
		this.compositor.composite(graphics, super::draw);
		this.window.requestCursor(graphics, this.window.getCursor());

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