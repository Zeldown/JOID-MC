package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.loader.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerScreens {

	public static void register() {
		for (final ContainerDeclaration<?> declaration : ContainerRegistry.getDeclarations()) {
			FabricContainerScreens.register(declaration);
		}
	}

	private static <M extends AbstractContainerMenu> void register(final ContainerDeclaration<M> declaration) {
		MenuScreens.register(declaration.type(), ContainerUIScreen.constructor(declaration.ui()));
	}

}