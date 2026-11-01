package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerUIRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addListener((final FMLClientSetupEvent event) -> event.enqueueWork(() -> ContainerUIRegistry.getRegistered().forEach(ForgeContainerUIRegistry::register)));
	}

	private static <M extends Container> void register(final ContainerUIRegistry<M> binding) {
		ScreenManager.register(binding.getType(), ContainerUI.screen(binding.getFactory()));
	}

}