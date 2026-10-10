package dev.joid.backend.minecraft.lib.ui.core.container;

import java.util.function.Function;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.backend.minecraft.loader.registry.ContainerBinding;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.dev.DevNode;
import dev.joid.lib.ui.node.impl.structure.container.ContainerNode;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class ContainerUI<M extends AbstractContainerMenu> extends UI {

	@Getter
	private final M container;

	@Getter
	private final Node carriedNode;

	private double   carriedSize;
	private Node     boundsNode;
	private double[] bounds;
	private boolean  boundsWarned;

	protected ContainerUI(final @NonNull M container) {
		this.container   = container;
		this.carriedSize = 64D;
		this.carriedNode = ContainerNode.create(0, 0, 0, 0).zindex(Integer.MAX_VALUE).interactive(false).onDraw((_, mouseX, mouseY) -> this.drawCarried(mouseX, mouseY));
	}

	public static <M extends AbstractContainerMenu> void bind(final @NonNull MenuType<M> type, final @NonNull Function<M, ? extends ContainerUI<M>> factory) {
		ContainerBinding.create(type, factory).register();
	}

	public final @NonNull ContainerUI<M> bounds(final @NonNull Node node) {
		this.boundsNode = node;
		this.bounds     = null;
		return this;
	}

	public final @NonNull ContainerUI<M> bounds(final double x, final double y, final double width, final double height) {
		this.boundsNode = null;
		this.bounds     = new double[] {x, y, width, height};
		return this;
	}

	public @NonNull double[] getBounds() {
		if (this.boundsNode != null) {
			return new double[] {this.boundsNode.getAbsoluteX(), this.boundsNode.getAbsoluteY(), this.boundsNode.getWidth(), this.boundsNode.getHeight()};
		}

		return this.bounds != null ? this.bounds.clone() : this.getNodeBounds();
	}

	public @NonNull ItemStack getCarried() {
		return this.container.getCarried();
	}

	public ContainerUIScreen<?> getScreen() {
		final ContainerUIBridge bridge = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		final ContainerUIScreen<?> screen = bridge != null ? bridge.getScreen() : null;
		return screen != null && screen.getMenu() == this.container ? screen : null;
	}

	private double[] getNodeBounds() {
		double left = Double.POSITIVE_INFINITY;
		double top = Double.POSITIVE_INFINITY;
		double right = Double.NEGATIVE_INFINITY;
		double bottom = Double.NEGATIVE_INFINITY;
		for (final Node node : super.getNodeList()) {
			if (node == this.carriedNode || node instanceof DevNode || !node.isVisible()) {
				continue;
			}

			left   = Math.min(left, node.getAbsoluteX());
			top    = Math.min(top, node.getAbsoluteY());
			right  = Math.max(right, node.getAbsoluteX() + node.getWidth());
			bottom = Math.max(bottom, node.getAbsoluteY() + node.getHeight());
		}

		if (left > right || top > bottom || left <= super.getViewX() && top <= super.getViewY() && right >= super.getViewX() + super.getViewWidth() && bottom >= super.getViewY() + super.getViewHeight()) {
			if (!this.boundsWarned && JOID.inst().isDevMode()) {
				this.boundsWarned = true;
				System.err.println("[JOID] The container UI " + this.getClass().getSimpleName() + " has no bounds and covers the whole view, call bounds(node) so that a click outside its panel drops the carried item");
			}

			if (left > right || top > bottom) {
				return new double[] {super.getViewX(), super.getViewY(), super.getViewWidth(), super.getViewHeight()};
			}
		}

		return new double[] {left, top, right - left, bottom - top};
	}

	private void drawCarried(final double mouseX, final double mouseY) {
		final ContainerUIScreen<?> screen = this.getScreen();
		final ItemStack carried = this.container.getCarried();
		if (screen == null || carried.isEmpty()) {
			return;
		}

		final SlotNode hovered = screen.getHoveredSlotNode();
		if (hovered != null) {
			this.carriedSize = hovered.getItemSize();
		}

		final ItemStack stack = screen.isQuickCrafting() && screen.getQuickCraftSlots().size() > 1 ? carried.copyWithCount(screen.getQuickCraftRemainder()) : carried;
		final double x = mouseX - this.carriedSize / 2D;
		final double y = mouseY - this.carriedSize / 2D;
		DrawItem.inst().drawItem(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemBar(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemCooldown(x, y, this.carriedSize, stack);
		DrawItem.inst().drawItemCount(x, y, this.carriedSize, stack);
	}

}