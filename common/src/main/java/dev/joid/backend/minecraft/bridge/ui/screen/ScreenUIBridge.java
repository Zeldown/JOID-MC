package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.ui.core.UI;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ScreenUIBridge extends BaseScreenUIBridge<GuiGraphicsExtractor> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	private UIScreen screen;

	public ScreenUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = new GuiCompositor(render);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected void attachScreen() {
		if (this.screen == null) {
			this.screen = new UIScreen(this);
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

	public void removed(final UIScreen screen) {
		if (this.screen == screen) {
			this.screen = null;
			super.closeAll();
		}
	}

	public UIDataMinecraft getMinecraftData() {
		for (final UI ui : super.getUiList()) {
			if (!ui.getOverlay().active()) {
				return ui.getClass().getAnnotation(UIDataMinecraft.class);
			}
		}
		return null;
	}

	public boolean isInputCaptured() {
		return this.window.getTextInputArea() != null;
	}

	@Override
	protected long countFrame(final GuiGraphicsExtractor graphics) {
		return GuiFrameCounter.inst().getFrame(graphics);
	}

	@Override
	protected void requestCursor(final GuiGraphicsExtractor graphics) {
		this.window.requestCursor(graphics, this.window.getCursor());
		this.window.requestTextInput();
	}

	@Override
	protected void compositeFrame(final GuiGraphicsExtractor graphics, final Runnable draw) {
		this.compositor.composite(graphics, draw);
	}

	@Override
	protected void drawTooltip(final GuiGraphicsExtractor graphics, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(graphics, content, mouseX, mouseY);
	}

}