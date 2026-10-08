package dev.joid.impl.joidmc;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.font.dto.markup.TextMarkup;
import dev.joid.lib.resource.dto.resolver.ResourceResolver;
import dev.joid.impl.joidmc.demo.ui.entity.UIDemoEntity;
import dev.joid.impl.joidmc.demo.ui.font.UIDemoFont;
import dev.joid.impl.joidmc.demo.ui.guiscale.UIDemoGuiScale;
import dev.joid.impl.joidmc.demo.ui.guiscale.UIDemoGuiScaleFixed;
import dev.joid.impl.joidmc.demo.ui.guiscale.UIDemoGuiScaleLimit;
import dev.joid.impl.joidmc.demo.ui.item.UIDemoItem;
import dev.joid.impl.joidmc.lib.bridge.audio.AudioBridge;
import dev.joid.impl.joidmc.lib.font.impl.minecraft.MinecraftFormat;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.ui.OverlayBridge;
import dev.joid.impl.joidmc.lib.bridge.ui.ScreenBridge;
import dev.joid.impl.joidmc.lib.resource.dto.resolver.impl.MCResourceResolver;

public final class JOIDMC {

	public static void register(final boolean dev) {
		JOID.checkVersion(Constants.JOID_VERSION);
		BridgeHandler.AUDIO.register(new AudioBridge());
		BridgeHandler.WINDOW.register(ScreenBridge.inst());
		BridgeHandler.RENDER.register(new RenderBridge());
		BridgeHandler.UI.register(OverlayBridge.inst());
		BridgeHandler.UI.register(ScreenBridge.inst());
		ResourceResolver.register(MCResourceResolver.inst());
		TextMarkup.register(MinecraftFormat.MARKUP);
		JOID.inst().setDevMode(dev).setDemoMode(dev).load();
		if (dev) {
			UIDemoChoice.LIST.add(UIDemoItem.class);
			UIDemoChoice.LIST.add(UIDemoEntity.class);
			UIDemoChoice.LIST.add(UIDemoGuiScale.class);
			UIDemoChoice.LIST.add(UIDemoGuiScaleLimit.class);
			UIDemoChoice.LIST.add(UIDemoGuiScaleFixed.class);
			UIDemoChoice.LIST.add(UIDemoFont.class);
		}
	}

}