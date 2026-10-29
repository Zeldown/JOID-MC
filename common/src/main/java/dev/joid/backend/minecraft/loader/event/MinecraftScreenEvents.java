package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftScreenEvents {

	public static void fireRender() {
		OverlayLayerRenderer.drawScreen();
	}

	public static void fireInput() {
		OverlayInputForwarder.forwardScreenInput();
	}

}