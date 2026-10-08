package dev.joid.impl.minecraft.fabric;

import dev.joid.impl.minecraft.registry.KeyMappingRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricKeyMappings {

	public static void register() {
		for (final KeyMapping.Category category : KeyMappingRegistry.getCategories()) {
			KeyMapping.Category.register(category.id());
		}

		for (final KeyMapping keyMapping : KeyMappingRegistry.getKeyMappings()) {
			KeyMappingHelper.registerKeyMapping(keyMapping);
		}
	}

}