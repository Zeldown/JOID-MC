package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerUIRegistry {

	public static void register(final BusGroup modBusGroup) {
		FMLClientSetupEvent.getBus(modBusGroup).addListener(event -> event.enqueueWork(() -> ContainerUIRegistry.getRegistered().forEach(ForgeContainerUIRegistry::register)));
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerUIRegistry<M> binding) {
		MenuScreens.register(binding.getType(), ContainerUI.screen(binding.getFactory()));
	}

}