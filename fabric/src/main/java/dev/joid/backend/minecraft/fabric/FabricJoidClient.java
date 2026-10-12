package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.fabric.demo.FabricGameTestRegistry;
import dev.joid.backend.minecraft.fabric.event.FabricClientEvents;
import dev.joid.backend.minecraft.fabric.event.FabricScreenEvents;
import dev.joid.backend.minecraft.fabric.network.FabricPayloads;
import dev.joid.backend.minecraft.fabric.registry.FabricContainerTypeRegistry;
import dev.joid.backend.minecraft.fabric.registry.FabricContainerUIRegistry;
import dev.joid.backend.minecraft.fabric.registry.FabricKeyBindRegistry;
import dev.joid.backend.minecraft.fabric.registry.FabricOverlayLayerRegistry;
import dev.joid.backend.minecraft.fabric.registry.FabricReloadListenerRegistry;

import net.fabricmc.api.ClientModInitializer;

public final class FabricJoidClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Backend.init();
		FabricPayloads.register();
		FabricContainerTypeRegistry.register();
		if (Backend.isDemo()) {
			FabricGameTestRegistry.register();
		}

		FabricClientEvents.register();
		FabricScreenEvents.register();
		FabricPayloads.registerSender();
		FabricKeyBindRegistry.register();
		FabricContainerUIRegistry.register();
		FabricOverlayLayerRegistry.register();
		FabricReloadListenerRegistry.register();
	}

}