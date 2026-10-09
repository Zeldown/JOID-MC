package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.registry.ContainerRegistry;
import dev.joid.backend.minecraft.ui.screen.ContainerUIScreen;
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