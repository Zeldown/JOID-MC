package dev.joid.backend.minecraft.ui.bridge;

import lombok.NonNull;

import net.minecraft.world.item.ItemStack;

public interface IItemHoverBridge {

	public void drawHover(final @NonNull ItemStack stack);

}