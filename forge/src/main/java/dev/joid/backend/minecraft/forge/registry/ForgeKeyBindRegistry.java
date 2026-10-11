package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyBindRegistry {

	public static void register() {
		RegisterKeyMappingsEvent.BUS.addListener(event -> {
			for (final String category : KeyBindRegistry.getCategories()) {
				KeyMapping.Category.register(Identifier.parse(category));
			}

			for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
				event.register(KeyBindMapping.create(bind));
			}
		});
	}

}