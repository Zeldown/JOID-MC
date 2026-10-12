package dev.joid.backend.minecraft;

import java.io.File;

import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import dev.joid.backend.minecraft.demo.DemoLauncher;
import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import dev.joid.internal.JOIDEnv;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftJoid {

	public static final String MOD_ID = "joid";

	private static boolean initialized;

	public static void init() {
		if (MinecraftJoid.initialized) {
			return;
		}

		MinecraftJoid.initialized = true;
		ReloadListenerRegistry.create(MinecraftJoid.MOD_ID + ":resources").onReload(ResourceReloader.inst()::reload).register();
		if (JOIDEnv.isDemoMode()) {
			DemoLauncher.init();
		}
	}

	public static void register() {
		if (!MinecraftBackend.inst().isRegistered()) {
			MinecraftBackend.inst().dataDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).register();
		}
	}

}