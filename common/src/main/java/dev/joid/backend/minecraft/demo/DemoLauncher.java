package dev.joid.backend.minecraft.demo;

import dev.joid.backend.minecraft.demo.smoke.ClientSmokeTest;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraftChoice;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.lib.JOID;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void init() {
		KeyBindRegistry.create("joid.demo", Key.J).onPress(() -> JOID.open(new UIDemoMinecraftChoice())).register();
		if (Boolean.getBoolean("joid.smoke")) {
			ClientSmokeTest.start();
		}

		JOID.onReady(DemoLauncher::register);
	}

	private static void register() {
		UIDemoChoice.menu(UIDemoMinecraftChoice::new);
	}

}