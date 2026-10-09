package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerBinding;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeContainerScreens {

	public static void register(final RegisterMenuScreensEvent event) {
		for (final ContainerBinding<?> binding : ContainerBinding.getRegistered()) {
			NeoForgeContainerScreens.register(event, binding);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final RegisterMenuScreensEvent event, final ContainerBinding<M> binding) {
		event.register(binding.getType(), ContainerUIScreen.constructor(binding.getFactory()));
	}

}