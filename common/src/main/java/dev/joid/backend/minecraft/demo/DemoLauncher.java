package dev.joid.backend.minecraft.demo;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.container.DemoContainerGameTest;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.demo.registry.GameTestRegistry;
import dev.joid.backend.minecraft.demo.smoke.ClientSmokeTest;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.demo.ui.DemoEntry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void init() {
		ContainerUIRegistry.create(DemoContainer.TYPE, UIDemoContainer::new).register();
		ContainerTypeRegistry.create(Backend.MOD_ID + ":demo/container", DemoContainer.TYPE).register();
		KeyBindRegistry.create("joid.demo", Key.J).onPress(() -> JOID.open(new UIDemoChoice())).register();
		OpenDemoContainerPayload.TYPE.onServer(OpenDemoContainerPayloadHandler::handle).register();
		GameTestRegistry.create(Backend.MOD_ID + ":demo/container", DemoContainerGameTest::run).register();
	}

	public static void register() {
		UIDemoChoice.LIST.add(DemoEntry.create(UIDemoMinecraft.class));
		UIDemoChoice.LIST.add(DemoEntry.create("UIDemoOverlayLayer", UIDemoOverlayLayer::toggle).state(UIDemoOverlayLayer::isOpen).hover(UIDemoOverlayLayer.class.getName()));
		UIDemoChoice.LIST.add(DemoEntry.create("UIDemoContainer", OpenDemoContainerPayload.INSTANCE::sendToServer).hover(UIDemoContainer.class.getName()));
		if (Boolean.getBoolean("joid.smoke")) {
			ClientSmokeTest.start();
		}
	}

}