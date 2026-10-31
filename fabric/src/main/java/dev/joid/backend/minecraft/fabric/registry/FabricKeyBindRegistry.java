package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricKeyBindRegistry {

	public static void register() {
		for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
			KeyBindingHelper.registerKeyBinding(KeyBindMapping.create(bind));
		}
	}

}