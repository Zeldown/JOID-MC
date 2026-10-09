package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeContainerScreens {

	public static void register(final RegisterMenuScreensEvent event) {
		for (final ContainerDeclaration<?> declaration : ContainerRegistry.getDeclarations()) {
			NeoForgeContainerScreens.register(event, declaration);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final RegisterMenuScreensEvent event, final ContainerDeclaration<M> declaration) {
		event.register(declaration.type(), ContainerUIScreen.constructor(declaration.ui()));
	}

}