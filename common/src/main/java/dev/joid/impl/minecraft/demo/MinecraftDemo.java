package dev.joid.impl.minecraft.demo;

import java.util.function.Consumer;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.impl.minecraft.IMinecraftDemo;
import dev.joid.internal.JOID;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MinecraftDemo implements IMinecraftDemo {

	@Override
	public void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets) {
		if (screen instanceof final PauseScreen pauseScreen && pauseScreen.showsPauseMenu()) {
			widgets.accept(Button.builder(Component.literal("JOID"), _ -> JOID.open(new UIDemoChoice())).bounds(4, 4, 40, 20).build());
		}
	}

}