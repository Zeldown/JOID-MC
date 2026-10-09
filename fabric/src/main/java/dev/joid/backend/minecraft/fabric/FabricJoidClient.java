package dev.joid.backend.minecraft.fabric;

import net.fabricmc.api.ClientModInitializer;

public final class FabricJoidClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FabricClientTicks.register();
		FabricScreenEvents.register();
		FabricOverlayLayers.register();
		FabricReloadListeners.register();
	}

}