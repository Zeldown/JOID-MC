package dev.joid.backend.minecraft.demo.container;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DemoContainer extends Container {

	public static final ContainerTypeRegistry<DemoContainer> TYPE = ContainerTypeRegistry.create(MinecraftJoid.MOD_ID + ":demo/container", player -> new DemoContainer(player.inventory, player.worldObj.isRemote ? new InventoryBasic("Demo storage", true, 27) : OpenDemoContainerPayloadHandler.getStorage(player)));

	@Getter
	private final IInventory storage;

	public DemoContainer(final InventoryPlayer inventory, final IInventory storage) {
		this.storage = storage;
		storage.openInventory();
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				super.addSlotToContainer(new Slot(storage, column + row * 9, 8 + column * 18, 18 + row * 18));
			}
		}

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				super.addSlotToContainer(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 85 + row * 18));
			}
		}

		for (int column = 0; column < 9; column++) {
			super.addSlotToContainer(new Slot(inventory, column, 8 + column * 18, 143));
		}
	}

	@Override
	public void onContainerClosed(final EntityPlayer player) {
		super.onContainerClosed(player);
		this.storage.closeInventory();
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
		final Slot slot = (Slot) super.inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) {
			return null;
		}

		final ItemStack stack = slot.getStack();
		final ItemStack moved = stack.copy();
		if (index < 27 ? !super.mergeItemStack(stack, 27, super.inventorySlots.size(), true) : !super.mergeItemStack(stack, 0, 27, false)) {
			return null;
		}

		if (stack.stackSize == 0) {
			slot.putStack(null);
		} else {
			slot.onSlotChanged();
		}
		return moved;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		return this.storage.isUseableByPlayer(player);
	}

}