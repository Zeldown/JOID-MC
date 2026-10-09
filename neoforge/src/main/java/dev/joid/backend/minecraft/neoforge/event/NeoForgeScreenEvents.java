package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.loader.event.ScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeScreenEvents {

	public static void init(final ScreenEvent.Init.Post event) {
		ScreenEvents.fireInit(event.getScreen(), event::addListener);
	}

	public static void render(final ScreenEvent.Render.Post event) {
		ScreenEvents.fireRender(event.getGuiGraphics());
	}

	public static void keyPressed(final ScreenEvent.KeyPressed.Pre event) {
		if (ScreenEvents.fireKeyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

	public static void charTyped(final ScreenEvent.CharacterTyped.Pre event) {
		if (ScreenEvents.fireCharTyped(event.getCodePoint())) {
			event.setCanceled(true);
		}
	}

	public static void mouseMoved(final ScreenEvent.MouseDragged.Pre event) {
		if (ScreenEvents.fireMouseMoved()) {
			event.setCanceled(true);
		}
	}

	public static void mousePressed(final ScreenEvent.MouseButtonPressed.Pre event) {
		if (ScreenEvents.fireMousePressed(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseReleased(final ScreenEvent.MouseButtonReleased.Pre event) {
		if (ScreenEvents.fireMouseReleased(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseScrolled(final ScreenEvent.MouseScrolled.Pre event) {
		if (ScreenEvents.fireMouseScrolled(event.getScrollDeltaY())) {
			event.setCanceled(true);
		}
	}

}