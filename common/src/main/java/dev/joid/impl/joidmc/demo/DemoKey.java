package dev.joid.impl.joidmc.demo;

import org.lwjgl.glfw.GLFW;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.impl.joidmc.Constants;

import net.minecraft.client.KeyMapping;

public final class DemoKey {

	public static final KeyMapping KEY = new KeyMapping("key." + Constants.MOD_ID + ".demo", GLFW.GLFW_KEY_P, KeyMapping.Category.MISC);

	public static void tick() {
		while (DemoKey.KEY.consumeClick()) {
			JOID.open(new UIDemoChoice());
		}
	}

}