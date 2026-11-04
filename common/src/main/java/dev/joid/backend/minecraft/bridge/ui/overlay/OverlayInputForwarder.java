package dev.joid.backend.minecraft.bridge.ui.overlay;

import dev.joid.base.lwjgl3.sdl.input.SdlKeys;
import dev.joid.base.lwjgl3.sdl.input.SdlMouseButtons;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.util.StringUtil;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayInputForwarder {

	public static boolean mouseMoved() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseMoved();
	}

	public static boolean mousePressed(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mousePressed(SdlMouseButtons.getButton(button));
	}

	public static boolean mouseReleased(final int button) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseReleased(SdlMouseButtons.getButton(button));
	}

	public static boolean mouseScrolled(final double notchesX, final double notchesY) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.mouseScroll(notchesX, notchesY);
	}

	public static boolean charTyped(final int codepoint) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && StringUtil.isAllowedChatCharacter(codepoint) && bridge.charTyped(codepoint);
	}

	public static boolean keyPressed(final int code) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge == null) {
			return false;
		}

		final Key physicalKey = SdlKeys.getPhysicalKey(code);
		final Key key = SdlKeys.getKey(code);
		return bridge.getKeyRepeats().press(physicalKey) ? bridge.keyPressed(physicalKey, key) : bridge.isKeyConsumed(key);
	}

	public static boolean keyReleased(final int code) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge == null) {
			return false;
		}

		final Key physicalKey = SdlKeys.getPhysicalKey(code);
		bridge.getKeyRepeats().release(physicalKey);
		return bridge.keyReleased(physicalKey, SdlKeys.getKey(code));
	}

}