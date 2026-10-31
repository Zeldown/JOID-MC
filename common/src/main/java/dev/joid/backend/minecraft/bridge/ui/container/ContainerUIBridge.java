package dev.joid.backend.minecraft.bridge.ui.container;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ContainerUIBridge extends BaseContainerUIBridge<GuiGraphicsExtractor> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	@Getter
	private ContainerUIScreen<?> screen;

	public ContainerUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = new GuiCompositor(render);
	}

	@Override
	public void open(final @NonNull UI ui) {
		if (ui instanceof ContainerUI) {
			throw new IllegalStateException("The container UI " + ui.getClass().getSimpleName() + " opens with its menu on the server (ServerPlayer.openMenu), not with JOID.open: register its screen with ContainerUI.screen(factory) in the menu screen registration of the loader (MenuScreens.register, RegisterMenuScreensEvent)");
		}

		super.open(ui);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected void detachScreen() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (this.screen != null && minecraft.gui.screen() == this.screen && minecraft.player != null) {
			this.screen = null;
			minecraft.player.closeContainer();
		}
	}

	public void added(final ContainerUIScreen<?> screen) {
		this.screen = screen;
		super.add(screen.getUi());
	}

	public void removed(final ContainerUIScreen<?> screen) {
		if (this.screen == screen) {
			this.screen = null;
			super.closeAll();
		}
	}

	public ContainerUIScreen<?> getScreen(final ContainerUI<?> ui) {
		return this.screen != null && this.screen.getUi() == ui ? this.screen : null;
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

	@Override
	protected boolean hasContainerScreen() {
		return this.screen != null;
	}

}