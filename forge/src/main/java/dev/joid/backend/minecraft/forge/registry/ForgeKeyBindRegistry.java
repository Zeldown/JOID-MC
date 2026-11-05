package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeKeyBindRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addListener((final FMLClientSetupEvent event) -> event.enqueueWork(() -> {
			for (final KeyBindRegistry bind : KeyBindRegistry.getRegistered()) {
				ClientRegistry.registerKeyBinding(KeyBindMapping.create(bind));
			}
		}));
	}

}