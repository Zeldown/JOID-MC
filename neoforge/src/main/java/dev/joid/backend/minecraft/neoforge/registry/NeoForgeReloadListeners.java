package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeReloadListeners {

	public static void register(final AddClientReloadListenersEvent event) {
		for (final ReloadListener listener : ReloadListener.getRegistered()) {
			event.addListener(Identifier.parse(listener.getId()), (ResourceManagerReloadListener) _ -> listener.getOnReload().run());
		}
	}

}