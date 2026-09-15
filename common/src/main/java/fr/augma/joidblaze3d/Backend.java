package fr.augma.joidblaze3d;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.bridge.BridgeHandler;
import fr.augma.joidblaze3d.audio.AudioBridge;
import fr.augma.joidblaze3d.render.RenderBridge;
import fr.augma.joidblaze3d.screen.ScreenBridge;

public final class Backend {

	public static final String JOID_VERSION = "7.0.0";

	public static void register() {
		JOID.checkVersion(Backend.JOID_VERSION);
		BridgeHandler.AUDIO.register(new AudioBridge());
		BridgeHandler.WINDOW.register(ScreenBridge.inst());
		BridgeHandler.RENDER.register(new RenderBridge());
		BridgeHandler.UI.register(ScreenBridge.inst());
		JOID.inst().setDemoMode(true).load();
	}

}