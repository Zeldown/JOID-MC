package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.neoforge.event.NeoForgeClientEvents;
import dev.joid.backend.minecraft.neoforge.event.NeoForgeScreenEvents;
import dev.joid.backend.minecraft.neoforge.network.NeoForgePayloads;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerUIRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeKeyBindRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeReloadListenerRegistry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Backend.MOD_ID, dist = Dist.CLIENT)
public final class NeoForgeJoidClient {

	public NeoForgeJoidClient(final IEventBus modBus) {
		NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::tick);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::render);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::charTyped);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::keyPressed);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseMoved);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::keyReleased);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mousePressed);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseReleased);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseScrolled);
		modBus.addListener(NeoForgeKeyBindRegistry::register);
		modBus.addListener(NeoForgeContainerUIRegistry::register);
		modBus.addListener(NeoForgeOverlayLayerRegistry::register);
		modBus.addListener(NeoForgeReloadListenerRegistry::register);
		NeoForgePayloads.registerSender();
	}

}