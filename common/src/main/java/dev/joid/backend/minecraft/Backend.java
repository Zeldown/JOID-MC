package dev.joid.backend.minecraft;

import java.io.File;

import dev.joid.backend.minecraft.bridge.audio.MinecraftAudioGain;
import dev.joid.backend.minecraft.bridge.input.KeyBindKeyResolver;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import dev.joid.backend.minecraft.bridge.thread.MinecraftThreadBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.demo.DemoLauncher;
import dev.joid.backend.minecraft.lib.asset.locator.impl.NamespacedAssetLocator;
import dev.joid.backend.minecraft.lib.font.converter.impl.ChatTextConverter;
import dev.joid.backend.minecraft.lib.font.markup.impl.LegacyTextMarkup;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.GpuTextureResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.NamespacedResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.NativeImageResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.SpriteResourceResolver;
import dev.joid.backend.minecraft.loader.registry.ReloadListener;
import dev.joid.base.openal.AlAudioBridge;
import dev.joid.base.openal.binding.Lwjgl3AlBinding;
import dev.joid.internal.JOID;
import dev.joid.lib.asset.locator.AssetLocator;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.font.converter.TextConverter;
import dev.joid.lib.font.markup.TextMarkup;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.resource.resolver.ResourceResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Backend {

	public static final String MOD_ID = "joid";

	private static boolean registered;

	public static void init() {
		ReloadListener.create(Backend.MOD_ID + ":resources").onReload(ResourceReloader.inst()::reload).register();
		if (Backend.isDemo()) {
			DemoLauncher.init();
		}
	}

	public static void register() {
		if (Backend.registered) {
			return;
		}

		Backend.registered = true;
		final MinecraftRenderBridge render = new MinecraftRenderBridge();
		final MinecraftWindowBridge window = new MinecraftWindowBridge();
		JOID.checkVersion(JOID.VERSION);
		BridgeHandler.AUDIO.register(AlAudioBridge.create(Lwjgl3AlBinding.inst()).ownContext(false).gain(MinecraftAudioGain.inst()));
		BridgeHandler.WINDOW.register(window);
		BridgeHandler.RENDER.register(render);
		BridgeHandler.THREAD.register(new MinecraftThreadBridge());
		BridgeHandler.UI.register(ScreenUIBridge.create(render, window));
		BridgeHandler.UI.register(ContainerUIBridge.create(render, window));
		BridgeHandler.UI.register(OverlayUIBridge.create(render, window));
		TextMarkup.register(LegacyTextMarkup.inst());
		KeyResolver.register(new KeyBindKeyResolver());
		TextConverter.register(ChatTextConverter.inst());
		AssetLocator.register(new NamespacedAssetLocator());
		ResourceResolver.register(new SpriteResourceResolver());
		ResourceResolver.register(new GpuTextureResourceResolver());
		ResourceResolver.register(new NamespacedResourceResolver());
		ResourceResolver.register(new NativeImageResourceResolver());

		final boolean demo = Backend.isDemo();
		JOID.inst().setConfigDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).setDevMode(demo).setDemoMode(demo).load();
		if (demo) {
			DemoLauncher.register();
		}
	}

	public static boolean isDemo() {
		return Backend.class.getResource("/dev/joid/backend/minecraft/demo/DemoLauncher.class") != null;
	}

}