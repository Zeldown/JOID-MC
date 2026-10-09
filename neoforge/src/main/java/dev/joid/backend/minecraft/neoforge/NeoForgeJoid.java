package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftBackend;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MinecraftBackend.MOD_ID)
public final class NeoForgeJoid {

	public NeoForgeJoid(final IEventBus modBus) {
		MinecraftBackend.init();
		modBus.addListener(NeoForgePayloads::register);
		modBus.addListener(NeoForgeTestFunctions::register);
		modBus.addListener(NeoForgeContainerTypes::register);
	}

}