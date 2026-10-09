package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeReloadListeners {

	public static void register(final AddClientReloadListenersEvent event) {
		event.addListener(ResourceReloader.ID, ResourceReloader.inst());
	}

}