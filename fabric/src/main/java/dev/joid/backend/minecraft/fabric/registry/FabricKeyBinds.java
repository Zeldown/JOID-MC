package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBind;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricKeyBinds {

	public static void register() {
		for (final String category : KeyBind.getCategories()) {
			KeyMapping.Category.register(Identifier.parse(category));
		}

		for (final KeyBind bind : KeyBind.getRegistered()) {
			KeyMappingHelper.registerKeyMapping(KeyBindMapping.create(bind));
		}
	}

}