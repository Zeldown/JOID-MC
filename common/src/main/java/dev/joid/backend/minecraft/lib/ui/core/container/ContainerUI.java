package dev.joid.backend.minecraft.lib.ui.core.container;

import java.util.function.Function;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.backend.minecraft.loader.registry.ContainerBinding;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.structure.container.ContainerNode;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class ContainerUI<M extends AbstractContainerMenu> extends UI {

	@Getter
	private final M container;

	@Getter
	private final Node carriedNode;

	private double carriedSize;

	protected ContainerUI(final @NonNull M container) {
		this.container   = container;
		this.carriedSize = 64D;
		this.carriedNode = ContainerNode.create(0, 0, 0, 0).zindex(Integer.MAX_VALUE).interactive(false).onDraw((_, mouseX, mouseY) -> this.drawCarried(mouseX, mouseY));
	}

	public static <M extends AbstractContainerMenu> void bind(final @NonNull MenuType<M> type, final @NonNull Function<M, ? extends ContainerUI<M>> factory) {
		ContainerBinding.create(type, factory).register();
	}

	public @NonNull ItemStack getCarried() {
		return this.container.getCarried();
	}

	public ContainerUIScreen<?> getScreen() {
		final ContainerUIBridge bridge = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		final ContainerUIScreen<?> screen = bridge != null ? bridge.getScreen() : null;
		return screen != null && screen.getMenu() == this.container ? screen : null;
	}

	private void drawCarried(final double mouseX, final double mouseY) {
		final ContainerUIScreen<?> screen = this.getScreen();
		final ItemStack carried = this.container.getCarried();
		if (screen == null || carried.isEmpty()) {
			return;
		}

		final SlotNode hovered = screen.getHoveredSlotNode();
		if (hovered != null) {
			this.carriedSize = hovered.getItemSize();
		}

		final ItemStack stack = screen.isQuickCrafting() && screen.getQuickCraftSlots().size() > 1 ? carried.copyWithCount(screen.getQuickCraftRemainder()) : carried;
		final double x = mouseX - this.carriedSize / 2D;
		final double y = mouseY - this.carriedSize / 2D;
		DrawItem.inst().drawItem(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemBar(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemCooldown(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemCount(x, y, this.carriedSize, stack);
	}

}