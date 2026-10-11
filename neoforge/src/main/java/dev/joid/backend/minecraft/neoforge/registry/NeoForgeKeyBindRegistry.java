package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeKeyBindRegistry {

	public static void register(final RegisterKeyMappingsEvent event) {
		for (final String category : KeyBindRegistry.getCategories()) {
			event.registerCategory(KeyBindMapping.category(category));
		}

		for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
			event.register(KeyBindMapping.create(bind));
		}
	}

}