package dev.joid.backend.minecraft.demo;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.demo.smoke.ClientSmokeTest;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraftChoice;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.backend.minecraft.showcase.ShowcaseLauncher;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void init() {
		ContainerUIRegistry.create(DemoContainer.TYPE, UIDemoContainer::new).register();
		ContainerTypeRegistry.create(MinecraftJoid.MOD_ID + ":demo/container", DemoContainer.TYPE).register();
		KeyBindRegistry.create("joid.demo", Key.J).onPress(() -> JOID.open(new UIDemoMinecraftChoice())).register();
		OpenDemoContainerPayload.TYPE.onServer(OpenDemoContainerPayloadHandler::handle).register();
		if (ShowcaseLauncher.isActive()) {
			ShowcaseLauncher.init();
		}

		JOID.onReady(DemoLauncher::register);
	}

	private static void register() {
		UIDemoChoice.menu(UIDemoMinecraftChoice::new);
		if (Boolean.getBoolean("joid.smoke")) {
			ClientSmokeTest.start();
		}
	}

}