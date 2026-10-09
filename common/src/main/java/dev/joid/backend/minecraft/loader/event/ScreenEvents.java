package dev.joid.backend.minecraft.loader.event;

import java.util.function.Consumer;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScreenEvents {

	public static void fireInit(final @NonNull Screen screen, final @NonNull Consumer<AbstractWidget> widgets) {
		Backend.initScreen(screen, widgets);
	}

	public static void fireRender(final @NonNull GuiGraphicsExtractor graphics) {
		OverlayLayerRenderer.extractScreen(graphics);
	}

	public static boolean fireMouseMoved() {
		return OverlayInputForwarder.mouseMoved();
	}

	public static boolean fireKeyPressed(final int code) {
		return OverlayInputForwarder.keyPressed(code);
	}

	public static boolean fireCharTyped(final int codepoint) {
		return OverlayInputForwarder.charTyped(codepoint);
	}

	public static boolean fireMousePressed(final int button) {
		return OverlayInputForwarder.mousePressed(button);
	}

	public static boolean fireMouseReleased(final int button) {
		return OverlayInputForwarder.mouseReleased(button);
	}

	public static boolean fireMouseScrolled(final double notches) {
		return OverlayInputForwarder.mouseScrolled(notches);
	}

}