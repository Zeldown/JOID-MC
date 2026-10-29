package dev.joid.backend.minecraft.bridge.ui.overlay;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayLayerRenderer {

	public static void extract(final GuiGraphicsExtractor graphics) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHud(graphics);
		}
	}

	public static void extract(final GuiGraphicsExtractor graphics, final OverlayLayer layer, final boolean post) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawLayer(graphics, layer, post);
		}
	}

	public static void extractScreen(final GuiGraphicsExtractor graphics) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawAboveScreen(graphics);
		}
	}

	public static void extractHiddenHud(final GuiGraphicsExtractor graphics) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHiddenHud(graphics);
		}
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.isCancelled(layer);
	}

}