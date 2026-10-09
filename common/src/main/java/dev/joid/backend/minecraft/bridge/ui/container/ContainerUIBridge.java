package dev.joid.backend.minecraft.bridge.ui.container;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipQueue;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.lib.bridge.ui.StackUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlayObject;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ContainerUIBridge extends StackUIBridge {

	private final GuiCompositor         compositor;
	private final TooltipQueue          tooltipQueue;
	private final MinecraftWindowBridge window;

	@Getter
	private ContainerUIScreen<?> screen;

	private ContainerUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window       = window;
		this.compositor   = GuiCompositor.create(render);
		this.tooltipQueue = TooltipQueue.create();
	}

	public static @NonNull ContainerUIBridge create(final @NonNull MinecraftRenderBridge render, final @NonNull MinecraftWindowBridge window) {
		return new ContainerUIBridge(render, window);
	}

	@Override
	public void open(final @NonNull UI ui) {
		if (ui instanceof ContainerUI) {
			throw new IllegalStateException("The container UI " + ui.getClass().getSimpleName() + " opens with its container on the server (ServerPlayer.openMenu), not with JOID.open");
		}

		super.open(ui);
	}

	public void added(final @NonNull ContainerUIScreen<?> screen) {
		this.screen = screen;
		super.add(screen.getUi());
	}

	public void removed(final @NonNull ContainerUIScreen<?> screen) {
		if (this.screen == screen) {
			this.screen = null;
			super.closeAll();
		}
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return !ui.getOverlay().active() && (ui instanceof ContainerUI || this.screen != null);
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return !UIDataOverlayObject.getOrDefault(clazz).active() && (ContainerUI.class.isAssignableFrom(clazz) || this.screen != null);
	}

	@Override
	public boolean canReplace(final @NonNull UI ui) {
		return !(ui instanceof ContainerUI);
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
	protected void detachScreen() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (this.screen != null && minecraft.gui.screen() == this.screen && minecraft.player != null) {
			this.screen = null;
			minecraft.player.closeContainer();
		}
	}

}