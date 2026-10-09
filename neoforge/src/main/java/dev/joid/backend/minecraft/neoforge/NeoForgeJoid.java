package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.neoforge.network.NeoForgePayloads;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerTypes;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeGameTests;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MinecraftBackend.MOD_ID)
public final class NeoForgeJoid {

	public NeoForgeJoid(final IEventBus modBus) {
		MinecraftBackend.init();
		modBus.addListener(NeoForgePayloads::register);
		modBus.addListener(NeoForgeGameTests::register);
		modBus.addListener(NeoForgeContainerTypes::register);
	}

}