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
		ScreenEvents.AFTER_INIT.register((minecraft, screen, width, height) -> {
			ScreenEvents.afterRender(screen).register((current, graphics, mouseX, mouseY, partialTick) -> MinecraftScreenEvents.fireRender(graphics));
			ScreenKeyboardEvents.allowKeyPress(screen).register((current, key, scanCode, modifiers) -> !MinecraftScreenEvents.fireKeyPressed(key));
			ScreenKeyboardEvents.allowKeyRelease(screen).register((current, key, scanCode, modifiers) -> !MinecraftScreenEvents.fireKeyReleased(key));
			ScreenMouseEvents.allowMouseClick(screen).register((current, mouseX, mouseY, button) -> !MinecraftScreenEvents.fireMousePressed(button));
			ScreenMouseEvents.allowMouseRelease(screen).register((current, mouseX, mouseY, button) -> !MinecraftScreenEvents.fireMouseReleased(button));
			ScreenMouseEvents.allowMouseScroll(screen).register((current, mouseX, mouseY, scrollX, scrollY) -> !MinecraftScreenEvents.fireMouseScrolled(scrollX, scrollY));
		});
	}

}