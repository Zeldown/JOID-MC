package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.registry.KeyMappingRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyMappings {

	public static void register() {
		for (final KeyMapping.Category category : KeyMappingRegistry.getCategories()) {
			KeyMapping.Category.register(category.id());
		}

		RegisterKeyMappingsEvent.BUS.addListener(ForgeKeyMappings::register);
	}

	private static void register(final RegisterKeyMappingsEvent event) {
		for (final KeyMapping keyMapping : KeyMappingRegistry.getKeyMappings()) {
			event.register(keyMapping);
		}
	}

}