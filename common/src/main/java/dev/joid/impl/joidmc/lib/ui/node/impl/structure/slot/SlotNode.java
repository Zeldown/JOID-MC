package dev.joid.impl.joidmc.lib.ui.node.impl.structure.slot;

import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.draw.DrawUtils;
import be.zeldown.joid.lib.resource.Resource;
import be.zeldown.joid.lib.ui.node.Node;
import dev.joid.impl.joidmc.lib.bridge.ui.ScreenBridge;
import dev.joid.impl.joidmc.lib.draw.MCDrawUtils;
import dev.joid.impl.joidmc.lib.screen.JOIDMenuScreen;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unchecked")
public class SlotNode extends Node {

	@Getter private final Slot slot;

	@Getter private Resource placeholder;
	@Getter private double   placeholderPadding;

	@Getter private boolean durability;
	@Getter private boolean stackCount;
	@Getter private boolean cooldown;

	protected SlotNode(final Slot slot, final double x, final double y, final double size) {
		super(x, y, size, size);
		this.slot       = slot;
		this.durability = true;
		this.stackCount = true;
		this.cooldown   = true;
	}

	public static @NonNull SlotNode create(final @NonNull Slot slot, final double x, final double y, final double size) {
		return new SlotNode(slot, x, y, size);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final JOIDMenuScreen<?> screen = SlotNode.getScreen();
		if (screen == null) {
			return;
		}

		if (this.placeholder != null) {
			DrawUtils.RESOURCE.drawResource(super.getX() + this.placeholderPadding, super.getY() + this.placeholderPadding, super.getWidth() - this.placeholderPadding * 2D, super.getHeight() - this.placeholderPadding * 2D, this.placeholder);
		}

		final ItemStack stack = screen.getRenderStack(this.slot);
		if (!stack.isEmpty()) {
			MCDrawUtils.ITEM.drawItem(stack, super.getX(), super.getY(), super.getWidth(), super.getHeight(), Color.WHITE, this.durability, this.stackCount, this.cooldown, null);
		}

		if (super.isHovered(mouseX, mouseY, true)) {
			DrawUtils.SHAPE.drawRect(super.getX(), super.getY(), super.w(), super.h(), Color.WHITE.copyAlpha(super.hoverValue(0.6F)));
		}
	}

	public final <T extends SlotNode> @NonNull T durability(final boolean durability) {
		this.durability = durability;
		return (T) this;
	}

	public final <T extends SlotNode> @NonNull T stackCount(final boolean stackCount) {
		this.stackCount = stackCount;
		return (T) this;
	}

	public final <T extends SlotNode> @NonNull T cooldown(final boolean cooldown) {
		this.cooldown = cooldown;
		return (T) this;
	}

	public final <T extends SlotNode> @NonNull T placeholder(final Resource placeholder) {
		this.placeholder = placeholder;
		return (T) this;
	}

	public final <T extends SlotNode> @NonNull T placeholder(final Resource placeholder, final double padding) {
		this.placeholder        = placeholder;
		this.placeholderPadding = padding;
		return (T) this;
	}

	public final <T extends SlotNode> @NonNull T placeholderPadding(final double placeholderPadding) {
		this.placeholderPadding = placeholderPadding;
		return (T) this;
	}

	private static JOIDMenuScreen<?> getScreen() {
		final Screen host = ScreenBridge.inst().getHost();
		return host instanceof final JOIDMenuScreen<?> screen ? screen : null;
	}

}