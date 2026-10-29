package dev.joid.backend.minecraft;

import dev.joid.backend.minecraft.bridge.audio.MinecraftAudioGain;
import dev.joid.backend.minecraft.bridge.thread.MinecraftThreadBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.font.markup.impl.LegacyTextMarkup;
import dev.joid.base.lwjgl2.openal.Lwjgl2AlBinding;
import dev.joid.base.lwjgl2.opengl.Lwjgl2GlBinding;
import dev.joid.base.openal.AlAudioBridge;
import dev.joid.base.opengl.render.GlRenderBridge;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.audio.IAudioBridge;
import dev.joid.lib.bridge.backend.Backend;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.thread.IThreadBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.font.markup.TextMarkup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftBackend extends Backend {

	private static final MinecraftBackend INSTANCE = new MinecraftBackend();

	private IRenderBridge         render;
	private MinecraftWindowBridge window;

	public static @NonNull MinecraftBackend inst() {
		return MinecraftBackend.INSTANCE;
	}

	@Override
	protected @NonNull IWindowBridge createWindowBridge() {
		this.window = new MinecraftWindowBridge();
		return this.window;
	}

	@Override
	protected @NonNull IRenderBridge createRenderBridge() {
		this.render = new GlRenderBridge(Lwjgl2GlBinding.inst());
		return this.render;
	}

	@Override
	protected @NonNull IAudioBridge createAudioBridge() {
		return AlAudioBridge.create(Lwjgl2AlBinding.inst()).ownContext(false).gain(MinecraftAudioGain.inst());
	}

	@Override
	protected @NonNull IThreadBridge createThreadBridge() {
		return new MinecraftThreadBridge();
	}

	@Override
	protected void registerDefaults() {
		BridgeHandler.UI.register(new ScreenUIBridge(this.render, this.window), -30);

		TextMarkup.register(LegacyTextMarkup.inst());
	}

}