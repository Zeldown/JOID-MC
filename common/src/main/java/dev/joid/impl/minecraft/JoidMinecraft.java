package dev.joid.impl.minecraft;

import java.io.File;
import java.util.ServiceLoader;

import dev.joid.impl.minecraft.registry.KeyMappingRegistry;
import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoidMinecraft {

	public static final String MOD_ID = "joid";

	private static boolean demo;
	private static boolean started;

	public static void bootstrap() {
		for (final IMinecraftDemo minecraftDemo : ServiceLoader.load(IMinecraftDemo.class, JoidMinecraft.class.getClassLoader())) {
			minecraftDemo.bootstrap();
			JoidMinecraft.demo = true;
		}
	}

	public static void tick() {
		if (!JoidMinecraft.started) {
			JoidMinecraft.start();
		}

		KeyMappingRegistry.tick();
	}

	private static void start() {
		JoidMinecraft.started = true;
		Backend.register();
		JOID.inst().setConfigDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).setDevMode(JoidMinecraft.demo).setDemoMode(JoidMinecraft.demo).load();
	}

}