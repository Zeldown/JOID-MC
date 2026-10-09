package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.ui.overlay.OverlayLayerRenderer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricScreenEvents {

	public static void register() {
		ScreenEvents.AFTER_INIT.register((_, screen, _, _) -> {
			MinecraftBackend.initScreen(screen, Screens.getWidgets(screen)::add);
			ScreenMouseEvents.allowMouseDrag(screen).register((_, _, _, _) -> !OverlayInputForwarder.mouseMoved());
			ScreenKeyboardEvents.allowKeyPress(screen).register((_, event) -> !OverlayInputForwarder.keyPressed(event.key()));
			ScreenEvents.afterExtract(screen).register((_, graphics, _, _, _) -> OverlayLayerRenderer.extractScreen(graphics));
			ScreenMouseEvents.allowMouseClick(screen).register((_, event) -> !OverlayInputForwarder.mousePressed(event.button()));
			ScreenKeyboardEvents.allowCharType(screen).register((_, event) -> !OverlayInputForwarder.charTyped(event.codepoint()));
			ScreenMouseEvents.allowMouseRelease(screen).register((_, event) -> !OverlayInputForwarder.mouseReleased(event.button()));
			ScreenMouseEvents.allowMouseScroll(screen).register((_, _, _, _, notches) -> !OverlayInputForwarder.mouseScrolled(notches));
		});
	}

}