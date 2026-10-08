package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftBackend;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MinecraftBackend.MOD_ID, dist = Dist.CLIENT)
public final class NeoForgeJoidClient {

	public NeoForgeJoidClient(final IEventBus modBus) {
		NeoForge.EVENT_BUS.addListener(NeoForgeClientTicks::tick);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::init);
		modBus.addListener(NeoForgeReloadListeners::register);
	}

}