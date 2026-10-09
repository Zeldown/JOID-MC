package dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot;

import java.util.function.Supplier;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.signal.Signal;
import dev.joid.lib.ui.node.Node;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Getter
public class SlotNode extends Node {

	private static final Color QUICK_CRAFT = new Color(0x80FFFFFF);

	private Slot  slot;
	private Color color;
	private Color hoveredColor;

	protected SlotNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.color        = Color.TRANSPARENT;
		this.hoveredColor = new Color(0x80FFFFFF);
	}

	public static @NonNull SlotNode create(final double x, final double y, final double width, final double height) {
		return new SlotNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		DrawUtils.SHAPE.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.color);
		final ContainerUIScreen<?> screen = super.getUi() instanceof final ContainerUI<?> ui ? ui.getScreen() : null;
		if (this.slot == null || screen == null || !this.slot.isActive()) {
			return;
		}

		final double size = this.getItemSize();
		final double x = super.getX() + (super.getWidth() - size) / 2D;
		final double y = super.getY() + (super.getHeight() - size) / 2D;
		final ItemStack carried = screen.getMenu().getCarried();
		ItemStack stack = this.slot.getItem();
		String count = null;
		boolean quickCraft = false;
		if (screen.isQuickCrafting() && screen.getQuickCraftSlots().contains(this.slot) && !carried.isEmpty()) {
			if (screen.getQuickCraftSlots().size() == 1) {
				this.drawHighlight(screen);
				return;
			}

			final int max = Math.min(carried.getMaxStackSize(), this.slot.getMaxStackSize(carried));
			final int placed = AbstractContainerMenu.getQuickCraftPlaceCount(screen.getQuickCraftSlots().size(), screen.getQuickCraftType(), carried) + (stack.isEmpty() ? 0 : stack.getCount());
			if (placed > max) {
				count = ChatFormatting.YELLOW.toString() + max;
			}

			stack = carried.copyWithCount(Math.min(placed, max));
			quickCraft = true;
		}

		final Identifier icon = stack.isEmpty() ? this.slot.getNoItemIcon() : null;
		if (icon != null) {
			DrawUtils.RESOURCE.drawResource(x, y, size, size, Resource.of(new SpriteId(Sheets.GUI_SHEET, icon)));
		} else {
			if (quickCraft) {
				DrawUtils.SHAPE.drawRect(x, y, size, size, SlotNode.QUICK_CRAFT);
			}

			DrawItem.inst().drawItem(x, y, size, stack);
			DrawItem.inst().drawItemBar(x, y, size, stack);
			DrawItem.inst().drawItemCooldown(x, y, size, stack);
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
		if (this.hoveredColor != null && screen.getHoveredSlot() == this.slot && this.slot.isHighlightable()) {
			DrawUtils.SHAPE.drawRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), this.hoveredColor);
		}
	}

}