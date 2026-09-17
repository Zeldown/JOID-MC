package fr.augma.joidblaze3d.screen.node;

import java.util.function.Supplier;

import be.zeldown.joid.lib.ui.node.Node;
import fr.augma.joidblaze3d.draw.MCDrawUtils;
import fr.augma.joidblaze3d.screen.ScreenBridge;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.item.ItemStack;

@Getter
@SuppressWarnings("unchecked")
public class ItemNode extends Node {

	private Supplier<ItemStack> stack;

	private boolean durability;
	private boolean stackCount;
	private boolean tooltip;

	protected ItemNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.durability = true;
		this.stackCount = true;
		this.tooltip    = true;
	}

	public static @NonNull ItemNode create(final double x, final double y, final double size) {
		return new ItemNode(x, y, size, size);
	}

	public static @NonNull ItemNode create(final double x, final double y, final double width, final double height) {
		return new ItemNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final ItemStack stack = this.getItemStack();
		if (stack == null || stack.isEmpty()) {
			return;
		}

		MCDrawUtils.ITEM.drawItem(stack, super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.durability, this.stackCount);
		if (this.tooltip && super.isHovered(mouseX, mouseY, true)) {
			ScreenBridge.inst().drawHover(stack);
		}
	}

	public final ItemStack getItemStack() {
		return this.stack == null ? null : this.stack.get();
	}

	public final <T extends ItemNode> @NonNull T stack(final ItemStack stack) {
		return this.stack(() -> stack);
	}

	public final <T extends ItemNode> @NonNull T stack(final Supplier<ItemStack> stack) {
		this.stack = stack;
		return (T) this;
	}

	public final <T extends ItemNode> @NonNull T tooltip(final boolean tooltip) {
		this.tooltip = tooltip;
		return (T) this;
	}

	public final <T extends ItemNode> @NonNull T durability(final boolean durability) {
		this.durability = durability;
		return (T) this;
	}

	public final <T extends ItemNode> @NonNull T stackCount(final boolean stackCount) {
		this.stackCount = stackCount;
		return (T) this;
	}

}