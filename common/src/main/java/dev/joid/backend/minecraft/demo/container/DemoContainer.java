package dev.joid.backend.minecraft.demo.container;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DemoContainer extends AbstractContainerMenu {

	public static final MenuType<DemoContainer> TYPE = new MenuType<>(DemoContainer::new);

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

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				super.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 85 + row * 18));
			}
		}

		for (int column = 0; column < 9; column++) {
			super.addSlot(new Slot(inventory, column, 8 + column * 18, 143));
		}
	}

	@Override
	public void removed(final Player player) {
		super.removed(player);
		this.storage.stopOpen(player);
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
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return moved;
	}

	@Override
	public boolean stillValid(final Player player) {
		return this.storage.stillValid(player);
	}

}