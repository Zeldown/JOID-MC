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
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::render);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::charTyped);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::keyPressed);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseMoved);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mousePressed);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseReleased);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseScrolled);
		modBus.addListener(NeoForgeOverlayLayers::register);
		modBus.addListener(NeoForgeReloadListeners::register);
		modBus.addListener(NeoForgeContainerScreens::register);
		NeoForgePayloads.registerSender();
	}

}