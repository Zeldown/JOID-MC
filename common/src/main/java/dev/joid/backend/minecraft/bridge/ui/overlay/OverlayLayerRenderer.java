package dev.joid.backend.minecraft.bridge.ui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayLayerRenderer {

	public static void extract(final PoseStack pose) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHud(pose);
		}
	}

	public static void extract(final PoseStack pose, final OverlayLayer layer, final boolean post) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawLayer(pose, layer, post);
		}
	}

	public static void extractScreen(final PoseStack pose) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawAboveScreen(pose);
		}
	}

	public static void extractHiddenHud(final PoseStack pose) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		if (bridge != null) {
			bridge.drawHiddenHud(pose);
		}
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		return bridge != null && bridge.isCancelled(layer);
	}

}