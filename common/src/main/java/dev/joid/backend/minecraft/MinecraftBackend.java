package dev.joid.backend.minecraft;

import dev.joid.backend.minecraft.bridge.audio.MinecraftAudioGain;
import dev.joid.backend.minecraft.bridge.input.KeyBindKeyResolver;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.thread.MinecraftThreadBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.asset.locator.impl.NamespacedAssetLocator;
import dev.joid.backend.minecraft.lib.font.converter.impl.ChatTextConverter;
import dev.joid.backend.minecraft.lib.font.markup.impl.LegacyTextMarkup;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.GpuTextureResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.NamespacedResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.NativeImageResourceResolver;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.SpriteResourceResolver;
import dev.joid.base.openal.AlAudioBridge;
import dev.joid.base.openal.binding.Lwjgl3AlBinding;
import dev.joid.internal.JOID;
import dev.joid.lib.asset.locator.AssetLocator;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.audio.IAudioBridge;
import dev.joid.lib.bridge.backend.Backend;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.thread.IThreadBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.font.converter.TextConverter;
import dev.joid.lib.font.markup.TextMarkup;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.resource.resolver.ResourceResolver;
import lombok.NonNull;

public final class MinecraftBackend extends Backend<MinecraftBackend> {

	private static final MinecraftBackend INSTANCE = new MinecraftBackend();

	private MinecraftRenderBridge render;
	private MinecraftWindowBridge window;

	private MinecraftBackend() {
		super(JOID.VERSION);
	}

	public static @NonNull MinecraftBackend inst() {
		return MinecraftBackend.INSTANCE;
	}

	@Override
	protected @NonNull IAudioBridge createAudioBridge() {
		return AlAudioBridge.create(Lwjgl3AlBinding.inst()).ownContext(false).gain(MinecraftAudioGain.inst());
	}

	@Override
	protected @NonNull IWindowBridge createWindowBridge() {
		this.window = new MinecraftWindowBridge();
		return this.window;
	}

	@Override
	protected @NonNull IRenderBridge createRenderBridge() {
		this.render = new MinecraftRenderBridge();
		return this.render;
	}

	@Override
	protected @NonNull IThreadBridge createThreadBridge() {
		return new MinecraftThreadBridge();
	}

	@Override
	protected void registerDefaults() {
		BridgeHandler.UI.register(ScreenUIBridge.create(this.render, this.window), -30);
		BridgeHandler.UI.register(OverlayUIBridge.create(this.render, this.window), -10);
		BridgeHandler.UI.register(ContainerUIBridge.create(this.render, this.window), -20);
		TextMarkup.register(LegacyTextMarkup.inst());
		KeyResolver.register(new KeyBindKeyResolver());
		TextConverter.register(ChatTextConverter.inst());
		AssetLocator.register(new NamespacedAssetLocator());
		ResourceResolver.register(new SpriteResourceResolver());
		ResourceResolver.register(new GpuTextureResourceResolver());
		ResourceResolver.register(new NamespacedResourceResolver());
		ResourceResolver.register(new NativeImageResourceResolver());
	}

}