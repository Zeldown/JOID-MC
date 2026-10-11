package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeContainerUIRegistry {

	public static void register(final RegisterMenuScreensEvent event) {
		for (final ContainerUIRegistry<?> binding : ContainerUIRegistry.getRegistered()) {
			NeoForgeContainerUIRegistry.register(event, binding);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final RegisterMenuScreensEvent event, final ContainerUIRegistry<M> binding) {
		event.register(binding.getType(), ContainerUI.screen(binding.getFactory()));
	}

}