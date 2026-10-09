package dev.joid.backend.minecraft.fabric.event;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricScreenEvents {

	public static void register() {
		ScreenEvents.AFTER_INIT.register((_, screen, _, _) -> {
			ScreenMouseEvents.allowMouseDrag(screen).register((_, _, _, _) -> !MinecraftScreenEvents.fireMouseMoved());
			ScreenKeyboardEvents.allowKeyPress(screen).register((_, event) -> !MinecraftScreenEvents.fireKeyPressed(event.key()));
			ScreenMouseEvents.allowMouseClick(screen).register((_, event) -> !MinecraftScreenEvents.fireMousePressed(event.button()));
			ScreenKeyboardEvents.allowCharType(screen).register((_, event) -> !MinecraftScreenEvents.fireCharTyped(event.codepoint()));
			ScreenMouseEvents.allowMouseRelease(screen).register((_, event) -> !MinecraftScreenEvents.fireMouseReleased(event.button()));
			ScreenMouseEvents.allowMouseScroll(screen).register((_, _, _, notchesX, notchesY) -> !MinecraftScreenEvents.fireMouseScrolled(notchesX, notchesY));
			ScreenEvents.afterExtract(screen).register((_, graphics, _, _, _) -> MinecraftScreenEvents.fireRender(graphics));
		});
	}

}