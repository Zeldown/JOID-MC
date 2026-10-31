package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeReloadListenerRegistry {

	public static void register(final RegisterClientReloadListenersEvent event) {
		for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
			event.registerReloadListener((ResourceManagerReloadListener) manager -> listener.getOnReload().run());
		}
	}

}