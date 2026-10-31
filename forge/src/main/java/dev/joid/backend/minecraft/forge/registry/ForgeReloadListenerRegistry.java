package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListenerRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addListener((final RegisterClientReloadListenersEvent event) -> {
			for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
				event.registerReloadListener((ResourceManagerReloadListener) manager -> listener.getOnReload().run());
			}
		});
	}

}