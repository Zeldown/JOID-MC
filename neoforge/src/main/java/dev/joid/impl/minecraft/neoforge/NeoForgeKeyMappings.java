package dev.joid.impl.minecraft.neoforge;

import dev.joid.impl.minecraft.registry.KeyMappingRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeKeyMappings {

	public static void register(final RegisterKeyMappingsEvent event) {
		for (final KeyMapping.Category category : KeyMappingRegistry.getCategories()) {
			event.registerCategory(category);
		}

		for (final KeyMapping keyMapping : KeyMappingRegistry.getKeyMappings()) {
			event.register(keyMapping);
		}
	}

}