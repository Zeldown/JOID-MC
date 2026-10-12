package dev.joid.backend.minecraft.neoforge.demo;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.neoforge.network.NeoForgePayloads;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerTypeRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MinecraftJoid.MOD_ID)
public final class NeoForgeJoidDemo {

	public NeoForgeJoidDemo(final IEventBus modBus) {
		MinecraftJoid.init();
		modBus.addListener(NeoForgePayloads::register);
		modBus.addListener(NeoForgeGameTestRegistry::register);
		modBus.addListener(NeoForgeContainerTypeRegistry::register);
	}

}