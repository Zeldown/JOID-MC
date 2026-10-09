package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBind;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeKeyBinds {

	public static void register(final RegisterKeyMappingsEvent event) {
		for (final String category : KeyBind.getCategories()) {
			event.registerCategory(KeyBindMapping.category(category));
		}

		for (final KeyBind bind : KeyBind.getRegistered()) {
			event.register(KeyBindMapping.create(bind));
		}
	}

}