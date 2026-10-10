package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipQueue;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.base.glfw.input.GlfwInputForwarder;
import dev.joid.lib.bridge.ui.StackUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlayObject;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ScreenUIBridge extends StackUIBridge {

	private final GuiCompositor         compositor;
	private final TooltipQueue          tooltipQueue;
	private final MinecraftWindowBridge window;

	@Getter
	private final GlfwInputForwarder input;

	private UIScreen screen;

	private ScreenUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window       = window;
		this.compositor   = GuiCompositor.create(render);
		this.tooltipQueue = TooltipQueue.create();
		this.input        = GlfwInputForwarder.create(this);
	}

	public static @NonNull ScreenUIBridge create(final @NonNull MinecraftRenderBridge render, final @NonNull MinecraftWindowBridge window) {
		return new ScreenUIBridge(render, window);
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

	public UIDataMinecraft getMinecraftData() {
		for (final UI ui : super.getUiList().ordered()) {
			if (!ui.getOverlay().active()) {
				return ui.getClass().getAnnotation(UIDataMinecraft.class);
			}
		}
		return null;
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull Object content, final double mouseX, final double mouseY) {
		this.tooltipQueue.push(content);
	}

	public void frame(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		super.update();
		this.compositor.composite(graphics, super::draw);
		this.window.requestCursor(graphics, this.window.getCursor());
		this.tooltipQueue.flush(graphics, mouseX, mouseY);
	}

	@Override
	protected void attachScreen() {
		if (this.screen == null) {
			this.screen = UIScreen.create(this);
			Minecraft.getInstance().gui.setScreen(this.screen);
		}
	}

	@Override
	protected void detachScreen() {
		if (this.screen == null) {
			return;
		}

		final UIScreen screen = this.screen;
		this.screen = null;
		if (Minecraft.getInstance().gui.screen() == screen) {
			Minecraft.getInstance().gui.setScreen(null);
		}
	}

}