package fr.augma.joidmc;

import be.zeldown.joid.demo.ui.UIDemoChoice;
import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.bridge.BridgeHandler;
import be.zeldown.joid.lib.resource.dto.resolver.ResourceResolver;
import fr.augma.joidmc.audio.AudioBridge;
import fr.augma.joidmc.demo.UIDemoEntity;
import fr.augma.joidmc.demo.UIDemoItem;
import fr.augma.joidmc.render.RenderBridge;
import fr.augma.joidmc.render.resource.MCResourceResolver;
import fr.augma.joidmc.screen.ScreenBridge;
import fr.augma.joidmc.screen.overlay.OverlayBridge;

public final class JOIDMC {

	public static void register(final boolean dev) {
		JOID.checkVersion(Constants.JOID_VERSION);
		BridgeHandler.AUDIO.register(new AudioBridge());
		BridgeHandler.WINDOW.register(ScreenBridge.inst());
		BridgeHandler.RENDER.register(new RenderBridge());
		BridgeHandler.UI.register(OverlayBridge.inst());
		BridgeHandler.UI.register(ScreenBridge.inst());
		ResourceResolver.register(MCResourceResolver.inst());
		JOID.inst().setDevMode(dev).setDemoMode(dev).load();
		if (dev) {
			UIDemoChoice.LIST.add(UIDemoItem.class);
			UIDemoChoice.LIST.add(UIDemoEntity.class);
		}
	}

}