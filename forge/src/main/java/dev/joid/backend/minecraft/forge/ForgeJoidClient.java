package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.forge.event.ForgeClientEvents;
import dev.joid.backend.minecraft.forge.event.ForgeScreenEvents;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerScreens;
import dev.joid.backend.minecraft.forge.registry.ForgeOverlayLayers;
import dev.joid.backend.minecraft.forge.registry.ForgeReloadListeners;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.eventbus.api.bus.BusGroup;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register(final BusGroup modBusGroup) {
		ForgeClientEvents.register();
		ForgeScreenEvents.register();
		ForgeOverlayLayers.register();
		ForgePayloads.registerSender();
		ForgeReloadListeners.register();
		ForgeContainerScreens.register(modBusGroup);
	}

}