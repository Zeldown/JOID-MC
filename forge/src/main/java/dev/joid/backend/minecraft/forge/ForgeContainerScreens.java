package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.registry.ContainerRegistry;
import dev.joid.backend.minecraft.ui.screen.ContainerUIScreen;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerScreens {

	public static void register(final BusGroup modBusGroup) {
		FMLClientSetupEvent.getBus(modBusGroup).addListener(event -> event.enqueueWork(() -> ContainerRegistry.getDeclarations().forEach(ForgeContainerScreens::register)));
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerDeclaration<M> declaration) {
		MenuScreens.register(declaration.type(), ContainerUIScreen.constructor(declaration.ui()));
	}

}