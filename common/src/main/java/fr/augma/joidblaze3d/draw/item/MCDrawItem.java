package fr.augma.joidblaze3d.draw.item;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import fr.augma.joidblaze3d.render.RenderBridge;
import lombok.NonNull;

import net.minecraft.world.item.ItemStack;

public final class MCDrawItem {

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double size) {
		this.drawItem(stack, x, y, size, size, true, true);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height) {
		this.drawItem(stack, x, y, width, height, true, true);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final boolean durability, final boolean stackCount) {
		((RenderBridge) BridgeHandler.RENDER.get()).item(stack, x, y, width, height, durability, stackCount);
	}

}