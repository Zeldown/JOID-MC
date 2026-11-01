package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.forge.event.ForgeClientEvents;
import dev.joid.backend.minecraft.forge.event.ForgeScreenEvents;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerUIRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeKeyBindRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register(final IEventBus modBus) {
		ForgeClientEvents.register();
		ForgeScreenEvents.register();
		ForgePayloads.registerSender();
		ForgeKeyBindRegistry.register(modBus);
		ForgeContainerUIRegistry.register(modBus);
		ForgeOverlayLayerRegistry.register(modBus);
		ForgeReloadListenerRegistry.register();
	}

}