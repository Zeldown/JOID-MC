package dev.joid.backend.minecraft.demo.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.demo.ui.DemoEntry;
import dev.joid.lib.JOID;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientTestBounds {

	public static int[] of(final Node node) {
		final UI ui = node.getUi();
		return new int[] {(int) Math.round(ui.getView().toScreenX(node.getAbsoluteX())), (int) Math.round(ui.getView().toScreenY(node.getAbsoluteY())), (int) Math.round(ui.getView().toScreenWidth(node.getWidth())), (int) Math.round(ui.getView().toScreenHeight(node.getHeight()))};
	}

	public static int[] first(final String ui) {
		return ClientTestBounds.of(ClientTestBounds.root(ui));
	}

	public static int[] firstRect(final String ui) {
		return ClientTestBounds.of(ClientTestBounds.root(ui).getChild(0, RectNode.class));
	}

	public static Node root(final String ui) {
		return JOID.getUi(ClientTestDemos.type(ui)).getNodeList().get(0);
	}

	public static List<int[]> all(final String ui, final String node) {
		return ClientTestBounds.all(JOID.getUi(ClientTestDemos.type(ui)), ClientTestDemos.nodeType(node));
	}

	public static List<int[]> all(final UI ui, final Class<? extends Node> type) {
		final List<int[]> bounds = new ArrayList<>();
		for (final Node node : ui.getNodeList()) {
			ClientTestBounds.collect(ui, node, type, bounds);
		}
		return bounds;
	}

	public static int[] entry(final String label) {
		final List<String> labels = ClientTestDemos.entries().stream().map(DemoEntry::getLabel).collect(Collectors.toList());
		final int index = labels.indexOf(label);
		if (index < 0) {
			throw new AssertionError("UIDemoMinecraftChoice does not list " + label);
		}
		return ClientTestBounds.of(ClientTestBounds.root(ClientTestDemos.CHOICE).getChild(index, RectNode.class));
	}

	private static void collect(final UI ui, final Node node, final Class<? extends Node> type, final List<int[]> bounds) {
		if (type.isInstance(node)) {
			final Node parent = node.getParent() != null ? node.getParent() : node;
			final double left = Math.max(node.getAbsoluteX(), parent.getAbsoluteX());
			final double top = Math.max(node.getAbsoluteY(), parent.getAbsoluteY());
			final double right = Math.min(node.getAbsoluteX() + node.getWidth(), parent.getAbsoluteX() + parent.getWidth());
			final double bottom = Math.min(node.getAbsoluteY() + node.getHeight(), parent.getAbsoluteY() + parent.getHeight());
			bounds.add(new int[] {(int) Math.round(ui.getView().toScreenX(left)), (int) Math.round(ui.getView().toScreenY(top)), (int) Math.round(ui.getView().toScreenWidth(right - left)), (int) Math.round(ui.getView().toScreenHeight(bottom - top))});
		}

		for (final Node child : node.getChildren(Node.class)) {
			ClientTestBounds.collect(ui, child, type, bounds);
		}
	}

}