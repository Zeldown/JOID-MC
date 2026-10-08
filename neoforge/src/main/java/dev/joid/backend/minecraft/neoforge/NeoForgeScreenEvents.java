package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftBackend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeScreenEvents {

	public static void init(final ScreenEvent.Init.Post event) {
		MinecraftBackend.initScreen(event.getScreen(), event::addListener);
	}

}