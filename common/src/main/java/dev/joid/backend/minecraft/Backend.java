package dev.joid.backend.minecraft;

import dev.joid.backend.minecraft.audio.AudioBridge;
import dev.joid.backend.minecraft.lib.asset.dto.locator.impl.IdentifierAssetLocator;
import dev.joid.backend.minecraft.lib.font.dto.markup.LegacyTextMarkup;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.IdentifierResourceResolver;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.SpriteResourceResolver;
import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.backend.minecraft.window.WindowBridge;
import dev.joid.internal.JOID;
import dev.joid.lib.asset.dto.locator.AssetLocator;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.font.dto.markup.TextMarkup;
import dev.joid.lib.resource.dto.resolver.ResourceResolver;
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
		TextMarkup.register(LegacyTextMarkup.inst());
		AssetLocator.register(new IdentifierAssetLocator());
		ResourceResolver.register(new SpriteResourceResolver());
		ResourceResolver.register(new IdentifierResourceResolver());
	}

}