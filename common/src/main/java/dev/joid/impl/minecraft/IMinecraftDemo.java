package dev.joid.impl.minecraft;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public interface IMinecraftDemo {

	public void initScreen(final Screen screen, final Consumer<AbstractWidget> widgets);

}