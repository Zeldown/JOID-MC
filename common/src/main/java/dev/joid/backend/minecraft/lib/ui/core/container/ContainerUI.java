package dev.joid.backend.minecraft.lib.ui.core.container;

import java.util.function.Function;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.NonNull;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class ContainerUI<M extends AbstractContainerMenu> extends BaseContainerUI<M> {

	private double carriedSize;

	protected ContainerUI(final @NonNull M container) {
		super(container);
		this.carriedSize = 64D;
		super.getCarriedNode().onDraw(event -> this.drawCarried(event.getMouseX(), event.getMouseY()));
	}

	public static <M extends AbstractContainerMenu> @NonNull ScreenConstructor<M, ContainerUIScreen<M>> screen(final @NonNull Function<M, ? extends ContainerUI<M>> factory) {
		return (menu, inventory, _) -> new ContainerUIScreen<>(menu, inventory, factory.apply(menu));
	}

	public @NonNull ItemStack getCarried() {
		return super.getContainer().getCarried();
	}

	private void drawCarried(final double mouseX, final double mouseY) {
		final ContainerUIScreen<?> screen = BridgeHandler.UI.getBridge(ContainerUIBridge.class).getScreen(this);
		final ItemStack carried = super.getContainer().getCarried();
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