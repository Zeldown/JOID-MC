package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyBindRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addListener((final RegisterKeyMappingsEvent event) -> {
			for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
				event.register(KeyBindMapping.create(bind));
			}
		});
	}

}