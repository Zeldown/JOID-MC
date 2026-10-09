package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerBinding;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerScreens {

	public static void register() {
		for (final ContainerBinding<?> binding : ContainerBinding.getRegistered()) {
			FabricContainerScreens.register(binding);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerBinding<M> binding) {
		MenuScreens.register(binding.getType(), ContainerUIScreen.constructor(binding.getFactory()));
	}

}