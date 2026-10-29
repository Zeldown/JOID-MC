package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftOverlayEvents {

	public static void fireHud() {
		OverlayLayerRenderer.draw();
	}

	public static void fireLayer(final OverlayLayer layer, final boolean post) {
		OverlayLayerRenderer.draw(layer, post);
	}

	public static void fireHiddenHud() {
		OverlayLayerRenderer.drawHiddenHud();
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		return OverlayLayerRenderer.isCancelled(layer);
	}

}