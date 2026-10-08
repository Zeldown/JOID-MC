package dev.joid.impl.minecraft.fabric;

import dev.joid.impl.minecraft.JoidMinecraft;

import net.fabricmc.api.ClientModInitializer;

public final class FabricJoidClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		JoidMinecraft.bootstrap();
		FabricClientTicks.register();
		FabricScreenEvents.register();
		FabricReloadListeners.register();
	}

}