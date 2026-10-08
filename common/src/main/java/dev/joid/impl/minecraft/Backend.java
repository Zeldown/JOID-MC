package dev.joid.impl.minecraft;

import dev.joid.impl.minecraft.audio.AudioBridge;
import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.impl.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.impl.minecraft.window.WindowBridge;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Backend {

	public static void register() {
		final RenderBridge render = new RenderBridge();
		JOID.checkVersion(JOID.VERSION);
		BridgeHandler.AUDIO.register(new AudioBridge());
		BridgeHandler.WINDOW.register(new WindowBridge());
		BridgeHandler.RENDER.register(render);
		BridgeHandler.UI.register(ScreenUIBridge.create(render));
	}

}