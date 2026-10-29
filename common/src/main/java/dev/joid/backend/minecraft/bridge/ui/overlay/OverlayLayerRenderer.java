package dev.joid.backend.minecraft.bridge.ui.overlay;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayLayerRenderer {

	public static void draw() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHud(null);
		}
	}

	public static void draw(final OverlayLayer layer, final boolean post) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawLayer(null, layer, post);
		}
	}

	public static void drawScreen() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawAboveScreen(null);
		}
	}

	public static void drawHiddenHud() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHiddenHud(null);
		}
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.isCancelled(layer);
	}

}