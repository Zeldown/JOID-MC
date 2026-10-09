package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerTypes {

	public static void register() {
		for (final ContainerDeclaration<?> declaration : ContainerRegistry.getDeclarations()) {
			Registry.register(BuiltInRegistries.MENU, declaration.id(), declaration.type());
		}
	}

}