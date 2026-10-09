package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeScreenEvents {

	public static void init(final ScreenEvent.Init.Post event) {
		MinecraftBackend.initScreen(event.getScreen(), event::addListener);
	}

	public static void render(final ScreenEvent.Render.Post event) {
		OverlayLayerRenderer.extractScreen(event.getGuiGraphics());
	}

	public static void keyPressed(final ScreenEvent.KeyPressed.Pre event) {
		if (OverlayInputForwarder.keyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

	public static void charTyped(final ScreenEvent.CharacterTyped.Pre event) {
		if (OverlayInputForwarder.charTyped(event.getCodePoint())) {
			event.setCanceled(true);
		}
	}

	public static void mouseMoved(final ScreenEvent.MouseDragged.Pre event) {
		if (OverlayInputForwarder.mouseMoved()) {
			event.setCanceled(true);
		}
	}

	public static void mousePressed(final ScreenEvent.MouseButtonPressed.Pre event) {
		if (OverlayInputForwarder.mousePressed(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseReleased(final ScreenEvent.MouseButtonReleased.Pre event) {
		if (OverlayInputForwarder.mouseReleased(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseScrolled(final ScreenEvent.MouseScrolled.Pre event) {
		if (OverlayInputForwarder.mouseScrolled(event.getScrollDeltaY())) {
			event.setCanceled(true);
		}
	}

}