package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.forge.event.ForgeClientEvents;
import dev.joid.backend.minecraft.forge.event.ForgeOverlayEvents;
import dev.joid.backend.minecraft.forge.event.ForgeScreenEvents;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypeRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeKeyBindRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register(final Object mod) {
		ForgeClientEvents.register();
		ForgeScreenEvents.register();
		ForgeOverlayEvents.register();
		ForgeKeyBindRegistry.register();
		ForgeReloadListenerRegistry.register();
		ForgePayloads.register();
		ForgeContainerTypeRegistry.register(mod);
	}

}