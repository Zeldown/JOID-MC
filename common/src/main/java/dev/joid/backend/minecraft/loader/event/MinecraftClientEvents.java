package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftClientEvents {

	public static void fireTick() {
		MinecraftJoid.register();
	}

	public static void fireFrame() {
		GuiFrameCounter.inst().nextFrame();
		final MinecraftWindowBridge window = BridgeHandler.WINDOW.getBridge(MinecraftWindowBridge.class);
		if (window != null) {
			window.applyCursor();
		}
	}

}