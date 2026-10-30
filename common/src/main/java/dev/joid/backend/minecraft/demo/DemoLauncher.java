package dev.joid.backend.minecraft.demo;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.demo.test.ClientTestPlan;
import dev.joid.backend.minecraft.demo.test.MinecraftClientTestPilot;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraftChoice;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.lib.JOID;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void init() {
		DemoContainer.TYPE.register();
		ContainerUIRegistry.create(DemoContainer.TYPE, UIDemoContainer::new).register();
		OpenDemoContainerPayload.TYPE.onServer(OpenDemoContainerPayloadHandler::handle).register();
		KeyBindRegistry.create("joid.demo", Key.J).onPress(() -> JOID.open(new UIDemoMinecraftChoice())).register();
		if (ClientTestPlan.isActive()) {
			ReloadListenerRegistry.create(MinecraftJoid.MOD_ID + ":demo/test").onReload(MinecraftClientTestPilot::countReload).register();
		}

		JOID.onReady(DemoLauncher::register);
	}

	private static void register() {
		UIDemoChoice.menu(UIDemoMinecraftChoice::new);
		if (ClientTestPlan.isActive()) {
			ClientTestPlan.start(new MinecraftClientTestPilot());
		}
	}

}