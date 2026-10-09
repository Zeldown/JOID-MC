package dev.joid.backend.minecraft.bridge.ui.overlay;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayLayerRenderer {

	public static void extract(final @NonNull GuiGraphicsExtractor graphics) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.extract(graphics);
		}
	}

	public static void extract(final @NonNull GuiGraphicsExtractor graphics, final @NonNull OverlayLayer layer, final boolean post) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.extract(graphics, layer, post);
		}
	}

	public static void extractScreen(final @NonNull GuiGraphicsExtractor graphics) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.extractScreen(graphics);
		}
	}

	public static boolean isCancelled(final @NonNull OverlayLayer layer) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.isCancelled(layer);
	}

}