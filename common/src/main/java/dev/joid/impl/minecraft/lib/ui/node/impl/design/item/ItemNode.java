package dev.joid.impl.minecraft.lib.ui.node.impl.design.item;

import java.util.function.Supplier;

import dev.joid.impl.minecraft.lib.draw.item.DrawItem;
import dev.joid.impl.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.hover.HoverElement;
import dev.joid.lib.utils.signal.Signal;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.item.ItemStack;

@Getter
public class ItemNode extends Node {

	private static final HoverElement TOOLTIP = (node, _, _) -> ((ItemNode) node).drawTooltip();

	private ItemStack stack;

	private boolean count;
	private boolean bar;
	private boolean cooldown;
	private boolean tooltip;

	protected ItemNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.stack    = ItemStack.EMPTY;
		this.count    = true;
		this.bar      = true;
		this.cooldown = true;
	}

	public static @NonNull ItemNode create(final double x, final double y, final double width, final double height) {
		return new ItemNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final double size = Math.min(super.getWidth(), super.getHeight());
		final double x = super.getX() + (super.getWidth() - size) / 2D;
		final double y = super.getY() + (super.getHeight() - size) / 2D;
		DrawItem.inst().drawItem(x, y, size, this.stack);
		if (this.bar) {
			DrawItem.inst().drawItemBar(x, y, size, this.stack);
		}

		if (this.cooldown) {
			DrawItem.inst().drawItemCooldown(x, y, size, this.stack);
		}

		if (this.count) {
			DrawItem.inst().drawItemCount(x, y, size, this.stack);
		}
	}

	public final <T extends ItemNode> @NonNull T stack(final @NonNull ItemStack stack) {
		return this.stack(Signal.from(stack));
	}

	public final <T extends ItemNode> @NonNull T stack(final @NonNull Supplier<@NonNull ItemStack> stack) {
		return super.follow("stack", stack, value -> this.stack = value);
	}

	public final <T extends ItemNode> @NonNull T count(final boolean count) {
		return this.count(Signal.from(count));
	}

	public final <T extends ItemNode> @NonNull T count(final @NonNull Supplier<Boolean> count) {
		return super.follow("count", count, value -> this.count = value);
	}

	public final <T extends ItemNode> @NonNull T bar(final boolean bar) {
		return this.bar(Signal.from(bar));
	}

	public final <T extends ItemNode> @NonNull T bar(final @NonNull Supplier<Boolean> bar) {
		return super.follow("bar", bar, value -> this.bar = value);
	}

	public final <T extends ItemNode> @NonNull T cooldown(final boolean cooldown) {
		return this.cooldown(Signal.from(cooldown));
	}

	public final <T extends ItemNode> @NonNull T cooldown(final @NonNull Supplier<Boolean> cooldown) {
		return super.follow("cooldown", cooldown, value -> this.cooldown = value);
	}

	public final <T extends ItemNode> @NonNull T tooltip(final boolean tooltip) {
		return this.tooltip(Signal.from(tooltip));
	}

	public final <T extends ItemNode> @NonNull T tooltip(final @NonNull Supplier<Boolean> tooltip) {
		return super.follow("tooltip", tooltip, value -> {
			this.tooltip = value;
			if (value && !super.getHoverElementList().contains(ItemNode.TOOLTIP)) {
				super.hover(ItemNode.TOOLTIP);
			}
		});
	}

	private void drawTooltip() {
		final UI ui = super.getUi();
		if (this.tooltip && !this.stack.isEmpty() && ui != null && ui.getBridge() instanceof final ScreenUIBridge bridge) {
			bridge.drawHover(this.stack);
		}
	}

}