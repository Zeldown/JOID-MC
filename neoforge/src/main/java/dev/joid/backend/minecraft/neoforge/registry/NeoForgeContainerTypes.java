package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeContainerTypes {

	public static void register(final RegisterEvent event) {
		event.register(Registries.MENU, helper -> {
			for (final ContainerDeclaration<?> declaration : ContainerRegistry.getDeclarations()) {
				helper.register(declaration.id(), declaration.type());
			}
		});
	}

}