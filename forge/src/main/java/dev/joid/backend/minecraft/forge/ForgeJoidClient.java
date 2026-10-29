package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.forge.event.ForgeClientEvents;
import dev.joid.backend.minecraft.forge.event.ForgeOverlayEvents;
import dev.joid.backend.minecraft.forge.event.ForgeScreenEvents;
import dev.joid.backend.minecraft.forge.registry.ForgeKeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register() {
		ForgeClientEvents.register();
		ForgeScreenEvents.register();
		ForgeOverlayEvents.register();
		ForgeKeyBindRegistry.register();
	}

}