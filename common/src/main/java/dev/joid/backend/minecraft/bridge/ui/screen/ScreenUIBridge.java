package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.ui.core.UI;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class ScreenUIBridge extends BaseScreenUIBridge<Void> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	private UIScreen screen;

	public ScreenUIBridge(final IRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = new GuiCompositor(render, window);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected void attachScreen() {
		if (this.screen == null) {
			this.screen = new UIScreen(this);
			Minecraft.getMinecraft().displayGuiScreen(this.screen);
		}
	}

	@Override
	protected void detachScreen() {
		if (this.screen == null) {
			return;
		}

		final UIScreen screen = this.screen;
		this.screen = null;
		if (Minecraft.getMinecraft().currentScreen == screen) {
			Minecraft.getMinecraft().displayGuiScreen(null);
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

	@Override
	protected long countFrame(final Void graphics) {
		return GuiFrameCounter.inst().getFrame();
	}

	@Override
	protected void requestCursor(final Void graphics) {
		this.window.requestCursor(this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final Void graphics, final Runnable draw) {
		this.compositor.composite(draw);
	}

	@Override
	protected void drawTooltip(final Void graphics, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(content, mouseX, mouseY);
	}

}