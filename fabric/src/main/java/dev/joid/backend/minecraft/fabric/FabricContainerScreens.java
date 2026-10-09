package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.registry.ContainerRegistry;
import dev.joid.backend.minecraft.ui.screen.ContainerUIScreen;
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