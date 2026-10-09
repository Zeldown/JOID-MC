package dev.joid.backend.minecraft.bridge.ui.container;

import java.util.ArrayList;
import java.util.List;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.IItemHoverBridge;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ContainerUIBridge extends StackUIBridge implements IItemHoverBridge {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	@Getter
	private ContainerUIScreen<?> screen;

	private List<String> hoverList;
	private ItemStack    hoverStack;

	private ContainerUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window     = window;
		this.compositor = GuiCompositor.create(render);
	}

	public static @NonNull ContainerUIBridge create(final @NonNull MinecraftRenderBridge render, final @NonNull MinecraftWindowBridge window) {
		return new ContainerUIBridge(render, window);
	}

	@Override
	public void open(final @NonNull UI ui) {
		throw new IllegalStateException("The container UI " + ui.getClass().getSimpleName() + " opens with its container on the server (ServerPlayer.openMenu), not with JOID.open");
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
		return ui instanceof ContainerUI && !ui.getOverlay().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return ContainerUI.class.isAssignableFrom(clazz) && !UIDataOverlayObject.getOrDefault(clazz).active();
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
	protected void onLastScreenClose() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (this.screen != null && minecraft.gui.screen() == this.screen && minecraft.player != null) {
			this.screen = null;
			minecraft.player.closeContainer();
		}
	}

}