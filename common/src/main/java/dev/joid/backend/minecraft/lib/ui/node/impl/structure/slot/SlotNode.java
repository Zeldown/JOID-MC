package dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot;

import java.util.function.Supplier;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.signal.Signal;
import dev.joid.lib.ui.node.Node;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

@Getter
public class SlotNode extends Node {

	private static final Color QUICK_CRAFT = Color.argb(0x80FFFFFF).lock();

	private Slot  slot;
	private Color color;
	private Color hoveredColor;

	protected SlotNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.color        = Color.TRANSPARENT;
		this.hoveredColor = Color.argb(0x80FFFFFF);

		super.<SlotNode>require("slot", "call slot(...) before attaching it", node -> node.slot != null);
	}

	public static @NonNull SlotNode create(final double x, final double y, final double width, final double height) {
		return new SlotNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		DrawUtils.SHAPE.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.color);
		final ContainerUIScreen<?> screen = super.getUi() instanceof ContainerUI ? BridgeHandler.UI.getBridge(ContainerUIBridge.class).getScreen((ContainerUI<?>) super.getUi()) : null;
		if (screen == null || !this.slot.func_111238_b()) {
			return;
		}

		final double size = this.getItemSize();
		final double x = super.getX() + (super.getWidth() - size) / 2D;
		final double y = super.getY() + (super.getHeight() - size) / 2D;
		final ItemStack carried = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
		ItemStack stack = this.slot.getStack();
		String count = null;
		boolean quickCraft = false;
		if (screen.isQuickCrafting() && screen.getQuickCraftSlots().contains(this.slot) && carried != null) {
			if (screen.getQuickCraftSlots().size() == 1) {
				this.drawHighlight(screen);
				return;
			}

			if (Container.func_94527_a(this.slot, carried, true) && screen.inventorySlots.canDragIntoSlot(this.slot)) {
				final int max = Math.min(carried.getMaxStackSize(), this.slot.getSlotStackLimit());
				stack = carried.copy();
				Container.func_94525_a(screen.getQuickCraftSlots(), screen.getQuickCraftType(), stack, this.slot.getStack() == null ? 0 : this.slot.getStack().stackSize);
				if (stack.stackSize > max) {
					count = EnumChatFormatting.YELLOW.toString() + max;
					stack.stackSize = max;
				}
				quickCraft = true;
			}
		}

		final IIcon icon = stack == null ? this.slot.getBackgroundIconIndex() : null;
		if (icon != null) {
			DrawUtils.RESOURCE.drawResource(x, y, size, size, Resource.of(icon));
		} else if (stack != null) {
			if (quickCraft) {
				DrawUtils.SHAPE.drawRect(x, y, size, size, SlotNode.QUICK_CRAFT);
			}

			DrawItem.inst().drawItem(x, y, size, stack);
			DrawItem.inst().drawItemBar(x, y, size, stack);
			if (count != null) {
				DrawItem.inst().drawItemCount(x, y, size, stack, count);
			} else {
				DrawItem.inst().drawItemCount(x, y, size, stack);
			}
		}

		this.drawHighlight(screen);
	}

	public double getItemSize() {
		return Math.min(super.getWidth(), super.getHeight()) * 16D / 18D;
	}

	public final <T extends SlotNode> @NonNull T slot(final @NonNull Slot slot) {
		return this.slot(Signal.from(slot));
	}

	public final <T extends SlotNode> @NonNull T slot(final @NonNull Supplier<@NonNull Slot> slot) {
		return super.follow("slot", slot, value -> this.slot = value);
	}

	public final <T extends SlotNode> @NonNull T color(final @NonNull Color color) {
		return this.color(Signal.from(color));
	}

	public final <T extends SlotNode> @NonNull T color(final @NonNull Supplier<@NonNull Color> color) {
		return super.follow("color", color, value -> this.color = value);
	}

	public final <T extends SlotNode> @NonNull T hoveredColor(final Color hoveredColor) {
		return this.hoveredColor(Signal.from(hoveredColor));
	}

	public final <T extends SlotNode> @NonNull T hoveredColor(final @NonNull Supplier<Color> hoveredColor) {
		return super.follow("hoveredColor", hoveredColor, value -> this.hoveredColor = value);
	}

	private void drawHighlight(final ContainerUIScreen<?> screen) {
		if (this.hoveredColor != null && screen.getHoveredSlot() == this.slot) {
			DrawUtils.SHAPE.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.hoveredColor);
		}
	}

}