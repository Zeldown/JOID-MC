package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.fabric.network.FabricPayloads;
import dev.joid.backend.minecraft.fabric.registry.FabricContainerTypes;
import dev.joid.backend.minecraft.fabric.registry.FabricGameTests;

import net.fabricmc.api.ModInitializer;

public final class FabricJoid implements ModInitializer {

	@Override
	public void onInitialize() {
		Backend.init();
		FabricPayloads.register();
		FabricGameTests.register();
		FabricContainerTypes.register();
	}

}