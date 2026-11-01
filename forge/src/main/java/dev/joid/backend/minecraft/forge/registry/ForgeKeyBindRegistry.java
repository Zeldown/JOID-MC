package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyBindRegistry {

	public static void register() {
		for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
			ClientRegistry.registerKeyBinding(KeyBindMapping.create(bind));
		}
	}

}