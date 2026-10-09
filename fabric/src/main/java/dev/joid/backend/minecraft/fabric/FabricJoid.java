package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.MinecraftBackend;

import net.fabricmc.api.ModInitializer;

public final class FabricJoid implements ModInitializer {

	@Override
	public void onInitialize() {
		MinecraftBackend.init();
		FabricPayloads.register();
		FabricTestFunctions.register();
		FabricContainerTypes.register();
	}

}