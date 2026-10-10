package dev.joid.backend.minecraft.bridge.ui.overlay;

import java.util.EnumSet;
import java.util.Set;

import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.input.mouse.MouseButton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayInputForwarder {

	private static final Set<MouseButton> PRESSED = EnumSet.noneOf(MouseButton.class);

	public static boolean keyPressed(final int code) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.keyPressed(GlfwKeys.getKey(code));
	}

	public static boolean charTyped(final int codepoint) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.charTyped(codepoint);
	}

	public static boolean mouseMoved() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseMoved();
	}

	public static boolean mousePressed(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge == null || !bridge.mousePressed(MouseButton.from(button))) {
			return false;
		}

		OverlayInputForwarder.PRESSED.add(MouseButton.from(button));
		return true;
	}

	public static boolean mouseReleased(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		final boolean consumed = bridge != null && bridge.mouseReleased(MouseButton.from(button));
		return OverlayInputForwarder.PRESSED.remove(MouseButton.from(button)) || consumed;
	}

	public static boolean mouseScrolled(final double notchesX, final double notchesY) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseScroll(notchesX, notchesY);
	}

}