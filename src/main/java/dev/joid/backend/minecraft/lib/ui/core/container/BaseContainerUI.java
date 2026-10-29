package dev.joid.backend.minecraft.lib.ui.core.container;

import dev.joid.internal.JOIDLog;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import lombok.Getter;
import lombok.NonNull;

public abstract class BaseContainerUI<M> extends UI {

	@Getter
	private final M container;

	@Getter
	private final Node carriedNode;

	private Node     boundsNode;
	private double[] bounds;

	protected BaseContainerUI(final @NonNull M container) {
		this.container   = container;
		this.carriedNode = ContainerNode.create(0, 0, 0, 0).zindex(Integer.MAX_VALUE).interactive(false);
	}

	public final <T extends BaseContainerUI<M>> @NonNull T bounds(final @NonNull Node node) {
		this.boundsNode = node;
		this.bounds     = null;
		return (T) this;
	}

	public final <T extends BaseContainerUI<M>> @NonNull T bounds(final double x, final double y, final double width, final double height) {
		this.boundsNode = null;
		this.bounds     = new double[] {x, y, width, height};
		return (T) this;
	}

	public @NonNull double[] getBounds() {
		if (this.boundsNode != null) {
			return new double[] {this.boundsNode.getAbsoluteX(), this.boundsNode.getAbsoluteY(), this.boundsNode.getWidth(), this.boundsNode.getHeight()};
		}

		return this.bounds != null ? this.bounds.clone() : this.getNodeBounds();
	}

	private double[] getNodeBounds() {
		double left = Double.POSITIVE_INFINITY;
		double top = Double.POSITIVE_INFINITY;
		double right = Double.NEGATIVE_INFINITY;
		double bottom = Double.NEGATIVE_INFINITY;
		for (final Node node : super.getNodeList()) {
			if (node == this.carriedNode || node == super.getDevNode() || !node.isVisible()) {
				continue;
			}

			left   = Math.min(left, node.getAbsoluteX());
			top    = Math.min(top, node.getAbsoluteY());
			right  = Math.max(right, node.getAbsoluteX() + node.getWidth());
			bottom = Math.max(bottom, node.getAbsoluteY() + node.getHeight());
		}

		if (left > right || top > bottom || left <= super.getViewX() && top <= super.getViewY() && right >= super.getViewX() + super.getViewWidth() && bottom >= super.getViewY() + super.getViewHeight()) {
			JOIDLog.devWarnOnce("ContainerUI.bounds." + this.getClass().getName(), () -> "The container UI " + this.getClass().getSimpleName() + " has no bounds and covers the whole view, call bounds(node) so that a click outside its panel drops the carried item");

			if (left > right || top > bottom) {
				return new double[] {super.getViewX(), super.getViewY(), super.getViewWidth(), super.getViewHeight()};
			}
		}

		return new double[] {left, top, right - left, bottom - top};
	}

}