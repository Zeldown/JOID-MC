package dev.joid.backend.minecraft.demo;

import java.util.function.Consumer;

import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlay;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoLauncher {

	public static void register() {
		UIDemoChoice.LIST.add(UIDemoMinecraft.class);
		UIDemoChoice.LIST.add(UIDemoOverlay.class);
	}

	public static void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		if (screen instanceof final PauseScreen pauseScreen && pauseScreen.showsPauseMenu()) {
			widgets.accept(Button.builder(Component.literal("JOID"), _ -> JOID.open(new UIDemoChoice())).bounds(4, 4, 40, 20).build());
		}
	}

}