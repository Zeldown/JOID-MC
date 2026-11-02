package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricContainerTypeRegistry {

	public static void register() {
		for (final ContainerTypeRegistry<?> type : ContainerTypeRegistry.getRegistered()) {
			Registry.register(Registry.MENU, ResourceLocation.tryParse(type.getId()), type.getType());
		}
	}

}