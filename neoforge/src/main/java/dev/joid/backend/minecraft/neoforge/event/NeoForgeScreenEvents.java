package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeScreenEvents {

	public static void render(final ScreenEvent.Render.Post event) {
		MinecraftScreenEvents.fireRender(event.getGuiGraphics());
	}

	public static void keyPressed(final ScreenEvent.KeyPressed.Pre event) {
		if (MinecraftScreenEvents.fireKeyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

	public static void charTyped(final ScreenEvent.CharacterTyped.Pre event) {
		if (MinecraftScreenEvents.fireCharTyped(event.getCodePoint())) {
			event.setCanceled(true);
		}
	}

	public static void mouseMoved(final ScreenEvent.MouseDragged.Pre event) {
		if (MinecraftScreenEvents.fireMouseMoved()) {
			event.setCanceled(true);
		}
	}

	public static void mousePressed(final ScreenEvent.MouseButtonPressed.Pre event) {
		if (MinecraftScreenEvents.fireMousePressed(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseReleased(final ScreenEvent.MouseButtonReleased.Pre event) {
		if (MinecraftScreenEvents.fireMouseReleased(event.getButton())) {
			event.setCanceled(true);
		}
	}

	public static void mouseScrolled(final ScreenEvent.MouseScrolled.Pre event) {
		if (MinecraftScreenEvents.fireMouseScrolled(event.getScrollDeltaX(), event.getScrollDeltaY())) {
			event.setCanceled(true);
		}
	}

}