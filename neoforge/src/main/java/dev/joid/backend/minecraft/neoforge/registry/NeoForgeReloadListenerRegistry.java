package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeReloadListenerRegistry {

	public static void register(final AddClientReloadListenersEvent event) {
		for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
			event.addListener(Identifier.parse(listener.getId()), (ResourceManagerReloadListener) _ -> listener.getOnReload().run());
		}
	}

}