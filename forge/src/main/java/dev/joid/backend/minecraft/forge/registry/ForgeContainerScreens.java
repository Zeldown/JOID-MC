package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerBinding;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerScreens {

	public static void register(final BusGroup modBusGroup) {
		FMLClientSetupEvent.getBus(modBusGroup).addListener(event -> event.enqueueWork(() -> ContainerBinding.getRegistered().forEach(ForgeContainerScreens::register)));
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerBinding<M> binding) {
		MenuScreens.register(binding.getType(), ContainerUIScreen.constructor(binding.getFactory()));
	}

}