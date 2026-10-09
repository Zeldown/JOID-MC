package dev.joid.backend.minecraft.demo;

import java.util.function.Consumer;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.container.DemoContainerGameTest;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlay;
import dev.joid.backend.minecraft.loader.network.PayloadRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import dev.joid.backend.minecraft.loader.registry.TestFunctionRegistry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void init() {
		ContainerRegistry.register(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "demo/container"), DemoContainer.TYPE, UIDemoContainer.Storage::new);
		PayloadRegistry.register(OpenDemoContainerPayload.TYPE, OpenDemoContainerPayload.STREAM_CODEC, OpenDemoContainerPayloadHandler::handle);
		TestFunctionRegistry.register(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "demo/container"), DemoContainerGameTest::run);
	}

	public static void register() {
		UIDemoChoice.LIST.add(UIDemoMinecraft.class);
		UIDemoChoice.LIST.add(UIDemoOverlay.class);
		UIDemoChoice.LIST.add(UIDemoContainer.class);
	}

	public static void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		if (screen instanceof final PauseScreen pauseScreen && pauseScreen.showsPauseMenu()) {
			widgets.accept(Button.builder(Component.literal("JOID"), _ -> JOID.open(new UIDemoChoice())).bounds(4, 4, 40, 20).build());
		}
	}

}