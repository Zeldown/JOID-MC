package dev.joid.backend.minecraft.ui.overlay;

import dev.joid.backend.minecraft.ui.bridge.OverlayUIBridge;
import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.utils.click.ClickType;
import dev.joid.lib.utils.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayInputForwarder {

	public static boolean keyPressed(final int code) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.keyTyped((char) 0, GlfwKeys.getKey(code));
	}

	public static boolean charTyped(final int codepoint) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		boolean consumed = false;
		if (bridge != null) {
			for (final char c : Character.toChars(codepoint)) {
				consumed |= bridge.keyTyped(c, Key.UNKNOWN);
			}
		}
		return consumed;
	}

	public static boolean mouseMoved() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseMoved();
	}

	public static boolean mousePressed(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mousePressed(ClickType.from(button));
	}

	public static boolean mouseReleased(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseReleased(ClickType.from(button));
	}

	public static boolean mouseScrolled(final double notches) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseScroll(notches);
	}

}