package dev.joid.impl.minecraft.neoforge;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeScreenEvents {

	public static void init(final ScreenEvent.Init.Post event) {
		JoidMinecraft.initScreen(event.getScreen(), event::addListener);
	}

}