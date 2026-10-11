package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerUIRegistry {

	public static void register() {
		for (final ContainerUIRegistry<?> binding : ContainerUIRegistry.getRegistered()) {
			FabricContainerUIRegistry.register(binding);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerUIRegistry<M> binding) {
		MenuScreens.<M, ContainerUIScreen<M>>register(binding.getType(), (menu, _, _) -> binding.getFactory().apply(menu).getScreen());
	}

}