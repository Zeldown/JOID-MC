package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.neoforge.demo.NeoForgeGameTestRegistry;
import dev.joid.backend.minecraft.neoforge.network.NeoForgePayloads;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerTypeRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Backend.MOD_ID)
public final class NeoForgeJoid {

	public NeoForgeJoid(final IEventBus modBus) {
		Backend.init();
		modBus.addListener(NeoForgePayloads::register);
		modBus.addListener(NeoForgeContainerTypeRegistry::register);
		if (Backend.isDemo()) {
			modBus.addListener(NeoForgeGameTestRegistry::register);
		}
	}

}