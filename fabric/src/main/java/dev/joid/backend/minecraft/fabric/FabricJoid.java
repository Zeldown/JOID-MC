package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.fabric.demo.FabricGameTests;
import dev.joid.backend.minecraft.fabric.network.FabricPayloads;
import dev.joid.backend.minecraft.fabric.registry.FabricContainerTypes;

import net.fabricmc.api.ModInitializer;

public final class FabricJoid implements ModInitializer {

	@Override
	public void onInitialize() {
		Backend.init();
		FabricPayloads.register();
		FabricContainerTypes.register();
		if (Backend.isDemo()) {
			FabricGameTests.register();
		}
	}

}