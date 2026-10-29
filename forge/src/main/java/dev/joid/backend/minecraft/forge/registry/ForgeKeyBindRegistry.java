package dev.joid.backend.minecraft.forge.registry;

import cpw.mods.fml.client.registry.ClientRegistry;
import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyBindRegistry {

	public static void register() {
		for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
			ClientRegistry.registerKeyBinding(KeyBindMapping.create(bind));
		}
	}

}