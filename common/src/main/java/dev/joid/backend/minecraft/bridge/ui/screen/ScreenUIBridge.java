package dev.joid.backend.minecraft.bridge.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.ui.core.UI;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class ScreenUIBridge extends BaseScreenUIBridge<PoseStack> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	private UIScreen screen;

	public ScreenUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
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
			Minecraft.getInstance().setScreen(this.screen);
		}
	}

	@Override
	protected void detachScreen() {
		if (this.screen == null) {
			return;
		}

		final UIScreen screen = this.screen;
		this.screen = null;
		if (Minecraft.getInstance().screen == screen) {
			Minecraft.getInstance().setScreen(null);
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
	protected long countFrame(final PoseStack pose) {
		return GuiFrameCounter.inst().getFrame(pose);
	}

	@Override
	protected void requestCursor(final PoseStack pose) {
		this.window.requestCursor(this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final PoseStack pose, final Runnable draw) {
		this.compositor.composite(draw);
	}

	@Override
	protected void drawTooltip(final PoseStack pose, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(pose, content, mouseX, mouseY);
	}

}