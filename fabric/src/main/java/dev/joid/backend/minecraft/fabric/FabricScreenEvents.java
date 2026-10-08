package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.MinecraftBackend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricScreenEvents {

	public static void register() {
		ScreenEvents.AFTER_INIT.register((_, screen, _, _) -> MinecraftBackend.initScreen(screen, Screens.getWidgets(screen)::add));
	}

}