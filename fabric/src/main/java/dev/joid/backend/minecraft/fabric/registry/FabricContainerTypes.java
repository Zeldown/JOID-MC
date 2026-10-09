package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerTypes {

	public static void register() {
		for (final ContainerType<?> type : ContainerType.getRegistered()) {
			Registry.register(BuiltInRegistries.MENU, Identifier.parse(type.getId()), type.getType());
		}
	}

}