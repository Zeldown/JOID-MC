package dev.joid.backend.minecraft.bridge.ui.container;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class ContainerUIBridge extends BaseContainerUIBridge<Void> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	@Getter
	private ContainerUIScreen<?> screen;

	public ContainerUIBridge(final IRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = new GuiCompositor(render, window);
	}

	@Override
	public void open(final @NonNull UI ui) {
		if (ui instanceof ContainerUI) {
			throw new IllegalStateException("The container UI " + ui.getClass().getSimpleName() + " opens with its container on the server (EntityPlayer.openGui), not with JOID.open: return ContainerUI.screen(factory).apply(container) from getClientGuiElement of the IGuiHandler of the mod");
		}

		super.open(ui);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected void detachScreen() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		if (this.screen != null && minecraft.currentScreen == this.screen && minecraft.thePlayer != null) {
			this.screen = null;
			minecraft.thePlayer.closeScreen();
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

	@Override
	protected boolean hasContainerScreen() {
		return this.screen != null;
	}

}