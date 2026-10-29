package dev.joid.backend.minecraft.bridge.ui.overlay;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.base.lwjgl2.input.Lwjgl2InputForwarder;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayInputForwarder {

	public static void forwardScreenInput() {
		final OverlayUIBridge bridge = BridgeHandler.UI.getBridge(OverlayUIBridge.class);
		final Minecraft minecraft = Minecraft.getMinecraft();
		if (bridge == null || bridge.getUiList().isEmpty() || minecraft.currentScreen == null || minecraft.currentScreen instanceof UIScreen) {
			return;
		}

		while (minecraft.currentScreen != null && Mouse.next()) {
			if (!Lwjgl2InputForwarder.forwardMouseEvent()) {
				minecraft.currentScreen.handleMouseInput();
			}
		}

		while (minecraft.currentScreen != null && Keyboard.next()) {
			if (!Lwjgl2InputForwarder.forwardKeyboardEvent()) {
				minecraft.currentScreen.handleKeyboardInput();
			}
		}
	}

}