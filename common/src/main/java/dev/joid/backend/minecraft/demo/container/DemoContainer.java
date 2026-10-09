package dev.joid.backend.minecraft.demo.container;

import lombok.Getter;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DemoContainer extends AbstractContainerMenu {

	public static final MenuType<DemoContainer> TYPE = new MenuType<>(DemoContainer::new, FeatureFlags.VANILLA_SET);

	@Getter
	private final Container storage;

	public DemoContainer(final int containerId, final Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(27));
	}

	public DemoContainer(final int containerId, final Inventory inventory, final Container storage) {
		super(DemoContainer.TYPE, containerId);
		AbstractContainerMenu.checkContainerSize(storage, 27);
		this.storage = storage;
		storage.startOpen(inventory.player);
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				super.addSlot(new Slot(storage, column + row * 9, 8 + column * 18, 18 + row * 18));
			}
		}

		super.addStandardInventorySlots(inventory, 8, 85);
	}

	@Override
	public boolean stillValid(final Player player) {
		return this.storage.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		final Slot slot = super.slots.get(slotIndex);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		final ItemStack stack = slot.getItem();
		final ItemStack moved = stack.copy();
		if (slotIndex < 27 ? !super.moveItemStackTo(stack, 27, super.slots.size(), true) : !super.moveItemStackTo(stack, 0, 27, false)) {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return moved;
	}

	@Override
	public void removed(final Player player) {
		super.removed(player);
		this.storage.stopOpen(player);
	}

}