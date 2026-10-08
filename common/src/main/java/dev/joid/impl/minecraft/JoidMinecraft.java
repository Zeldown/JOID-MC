package dev.joid.impl.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoidMinecraft {

	public static final String MOD_ID = "joid";

	private static final List<IMinecraftDemo> DEMOS = new ArrayList<>();

	private static boolean started;

	public static void bootstrap() {
		for (final IMinecraftDemo minecraftDemo : ServiceLoader.load(IMinecraftDemo.class, JoidMinecraft.class.getClassLoader())) {
			JoidMinecraft.DEMOS.add(minecraftDemo);
		}
	}

	public static void tick() {
		if (!JoidMinecraft.started) {
			JoidMinecraft.start();
		}
	}

	public static void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		for (final IMinecraftDemo minecraftDemo : JoidMinecraft.DEMOS) {
			minecraftDemo.initScreen(screen, widgets);
		}
	}

	private static void start() {
		JoidMinecraft.started = true;
		Backend.register();
		JOID.inst().setConfigDir(new File(Minecraft.getInstance().gameDirectory, "config/joid")).setDevMode(!JoidMinecraft.DEMOS.isEmpty()).setDemoMode(!JoidMinecraft.DEMOS.isEmpty()).load();
	}

}