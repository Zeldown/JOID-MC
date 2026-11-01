package dev.joid.backend.minecraft.lib.ui.core.container;

import java.util.function.Function;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public abstract class ContainerUI<C extends Container> extends BaseContainerUI<C> {

	private double carriedSize;

	protected ContainerUI(final @NonNull C container) {
		super(container);
		this.carriedSize = 64D;
		super.getCarriedNode().onDraw(event -> this.drawCarried(event.getMouseX(), event.getMouseY()));
	}

	public static <C extends Container> @NonNull Function<C, ContainerUIScreen<C>> screen(final @NonNull Function<C, ? extends ContainerUI<C>> factory) {
		return container -> new ContainerUIScreen<>(factory.apply(container));
	}

	public ItemStack getCarried() {
		return Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
	}

	private void drawCarried(final double mouseX, final double mouseY) {
		final ContainerUIScreen<?> screen = BridgeHandler.UI.getBridge(ContainerUIBridge.class).getScreen(this);
		final ItemStack carried = this.getCarried();
		if (screen == null || carried == null) {
			return;
		}

		final SlotNode hovered = screen.getHoveredSlotNode();
		if (hovered != null) {
			this.carriedSize = hovered.getItemSize();
		}

		final ItemStack stack = carried.copy();
		if (screen.isQuickCrafting() && screen.getQuickCraftSlots().size() > 1) {
			stack.stackSize = screen.getQuickCraftRemainder();
		}

		final double x = mouseX - this.carriedSize / 2D;
		final double y = mouseY - this.carriedSize / 2D;
		DrawItem.inst().drawItem(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemBar(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemCount(x, y, this.carriedSize, stack);
	}

}