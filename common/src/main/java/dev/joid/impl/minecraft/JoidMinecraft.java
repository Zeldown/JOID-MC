package dev.joid.impl.minecraft;

import java.io.File;
import java.util.function.Consumer;

import dev.joid.impl.minecraft.demo.DemoLauncher;
import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoidMinecraft {

	public static final String MOD_ID = "joid";

	private static boolean started;

	public static void tick() {
		if (!JoidMinecraft.started) {
			JoidMinecraft.start();
		}
	}

	public static void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		if (JOID.inst().isDemoMode()) {
			DemoLauncher.initScreen(screen, widgets);
		}
	}

	private static void start() {
		JoidMinecraft.started = true;
		Backend.register();

		final boolean demo = JoidMinecraft.class.getResource("/dev/joid/impl/minecraft/demo/DemoLauncher.class") != null;
		JOID.inst().setConfigDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).setDevMode(demo).setDemoMode(demo).load();
		if (demo) {
			DemoLauncher.register();
		}
	}

}