package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.neoforge.event.NeoForgeClientEvents;
import dev.joid.backend.minecraft.neoforge.event.NeoForgeScreenEvents;
import dev.joid.backend.minecraft.neoforge.network.NeoForgePayloads;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerTypeRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeContainerUIRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeKeyBindRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.neoforge.registry.NeoForgeReloadListenerRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftJoid.MOD_ID)
public final class NeoForgeJoidClient {

	public NeoForgeJoidClient() {
		if (!FMLEnvironment.dist.isClient()) {
			return;
		}

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftJoid.init();

		MinecraftForge.EVENT_BUS.addListener(NeoForgeClientEvents::tick);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeClientEvents::render);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::render);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::charTyped);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::keyPressed);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseMoved);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::keyReleased);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mousePressed);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseReleased);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeScreenEvents::mouseScrolled);
		MinecraftForge.EVENT_BUS.addListener(NeoForgeOverlayLayerRegistry::cancel);

		modBus.addListener(NeoForgeKeyBindRegistry::register);
		modBus.addListener(NeoForgeContainerUIRegistry::register);
		modBus.addListener(NeoForgeOverlayLayerRegistry::register);
		modBus.addListener(NeoForgeContainerTypeRegistry::register);
		modBus.addListener(NeoForgeReloadListenerRegistry::register);

		NeoForgePayloads.register();
	}

}