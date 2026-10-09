package dev.joid.backend.minecraft.fabric.event;

import dev.joid.backend.minecraft.loader.event.ScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricScreenEvents {

	public static void register() {
		net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((_, screen, _, _) -> {
			ScreenMouseEvents.allowMouseDrag(screen).register((_, _, _, _) -> !ScreenEvents.fireMouseMoved());
			ScreenKeyboardEvents.allowKeyPress(screen).register((_, event) -> !ScreenEvents.fireKeyPressed(event.key()));
			ScreenMouseEvents.allowMouseClick(screen).register((_, event) -> !ScreenEvents.fireMousePressed(event.button()));
			ScreenKeyboardEvents.allowCharType(screen).register((_, event) -> !ScreenEvents.fireCharTyped(event.codepoint()));
			ScreenMouseEvents.allowMouseRelease(screen).register((_, event) -> !ScreenEvents.fireMouseReleased(event.button()));
			ScreenMouseEvents.allowMouseScroll(screen).register((_, _, _, _, notches) -> !ScreenEvents.fireMouseScrolled(notches));
			net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.afterExtract(screen).register((_, graphics, _, _, _) -> ScreenEvents.fireRender(graphics));
		});
	}

}