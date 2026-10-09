package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.fabric.event.FabricClientEvents;
import dev.joid.backend.minecraft.fabric.event.FabricScreenEvents;
import dev.joid.backend.minecraft.fabric.network.FabricPayloads;
import dev.joid.backend.minecraft.fabric.registry.FabricContainerScreens;
import dev.joid.backend.minecraft.fabric.registry.FabricOverlayLayers;
import dev.joid.backend.minecraft.fabric.registry.FabricReloadListeners;

import net.fabricmc.api.ClientModInitializer;

public final class FabricJoidClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FabricClientEvents.register();
		FabricScreenEvents.register();
		FabricOverlayLayers.register();
		FabricPayloads.registerSender();
		FabricReloadListeners.register();
		FabricContainerScreens.register();
	}

}