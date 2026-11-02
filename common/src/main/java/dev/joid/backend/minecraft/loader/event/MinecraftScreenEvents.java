package dev.joid.backend.minecraft.loader.event;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftScreenEvents {

	public static void fireRender(final PoseStack pose) {
		OverlayLayerRenderer.extractScreen(pose);
	}

	public static boolean fireMouseMoved() {
		return OverlayInputForwarder.mouseMoved();
	}

	public static boolean fireMousePressed(final int button) {
		return OverlayInputForwarder.mousePressed(button);
	}

	public static boolean fireMouseReleased(final int button) {
		return OverlayInputForwarder.mouseReleased(button);
	}

	public static boolean fireMouseScrolled(final double notchesX, final double notchesY) {
		return OverlayInputForwarder.mouseScrolled(notchesX, notchesY);
	}

	public static boolean fireCharTyped(final int codepoint) {
		return OverlayInputForwarder.charTyped(codepoint);
	}

	public static boolean fireKeyPressed(final int code) {
		return OverlayInputForwarder.keyPressed(code);
	}

	public static boolean fireKeyReleased(final int code) {
		return OverlayInputForwarder.keyReleased(code);
	}

}