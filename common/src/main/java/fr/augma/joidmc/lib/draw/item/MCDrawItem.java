package fr.augma.joidmc.lib.draw.item;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import be.zeldown.joid.lib.color.Color;
import fr.augma.joidmc.lib.bridge.render.RenderBridge;
import lombok.NonNull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class MCDrawItem {

	public void drawItem(final @NonNull ItemLike item, final double x, final double y, final double size) {
		this.drawItem(new ItemStack(item), x, y, size, size);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double size) {
		this.drawItem(stack, x, y, size, size);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height) {
		this.drawItem(stack, x, y, width, height, Color.WHITE);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final @NonNull Color color) {
		this.drawItem(stack, x, y, width, height, color, true, true, true, null);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final boolean durability, final boolean stackCount) {
		this.drawItem(stack, x, y, width, height, Color.WHITE, durability, stackCount, true, null);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final @NonNull Color color, final boolean durability, final boolean stackCount, final boolean cooldown, final String text) {
		((RenderBridge) BridgeHandler.RENDER.get()).item(stack, x, y, width, height, color, durability, stackCount, cooldown, text);
	}

}