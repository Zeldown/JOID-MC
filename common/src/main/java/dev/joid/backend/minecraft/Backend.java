package dev.joid.backend.minecraft;

import java.io.File;
import java.util.function.Consumer;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.demo.DemoLauncher;
import dev.joid.backend.minecraft.lib.asset.dto.locator.impl.NamespacedAssetLocator;
import dev.joid.backend.minecraft.lib.font.dto.markup.LegacyTextMarkup;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.NamespacedResourceResolver;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.SpriteResourceResolver;
import dev.joid.base.openal.AlAudioBridge;
import dev.joid.base.openal.binding.Lwjgl3AlBinding;
import dev.joid.internal.JOID;
import dev.joid.lib.asset.dto.locator.AssetLocator;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.font.dto.markup.TextMarkup;
import dev.joid.lib.resource.dto.resolver.ResourceResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Backend {

	public static final String MOD_ID = "joid";

	private static boolean registered;

	public static void init() {
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
		BridgeHandler.AUDIO.register(AlAudioBridge.create(Lwjgl3AlBinding.inst()).hostGain(gain -> gain * Minecraft.getInstance().options.getFinalSoundSourceVolume(SoundSource.UI)));
		BridgeHandler.WINDOW.register(window);
		BridgeHandler.RENDER.register(render);
		BridgeHandler.UI.register(ScreenUIBridge.create(render, window));
		BridgeHandler.UI.register(ContainerUIBridge.create(render, window));
		BridgeHandler.UI.register(OverlayUIBridge.create(render, window));
		TextMarkup.register(LegacyTextMarkup.inst());
		AssetLocator.register(new NamespacedAssetLocator());
		ResourceResolver.register(new SpriteResourceResolver());
		ResourceResolver.register(new NamespacedResourceResolver());

		final boolean demo = Backend.isDemo();
		JOID.inst().setConfigDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).setDevMode(demo).setDemoMode(demo).load();
		if (demo) {
			DemoLauncher.register();
		}
	}

	public static void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		if (JOID.inst().isDemoMode()) {
			DemoLauncher.initScreen(screen, widgets);
		}
	}

	private static boolean isDemo() {
		return Backend.class.getResource("/dev/joid/backend/minecraft/demo/DemoLauncher.class") != null;
	}

}