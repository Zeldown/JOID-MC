package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerTypeRegistry {

	public static void register() {
		for (final ContainerTypeRegistry<?> type : ContainerTypeRegistry.getRegistered()) {
			Registry.register(BuiltInRegistries.MENU, Identifier.parse(type.getId()), type.getType());
		}
	}

}