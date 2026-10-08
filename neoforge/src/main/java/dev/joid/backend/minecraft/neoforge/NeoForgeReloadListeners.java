package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.lib.resource.dto.reload.ResourceReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeReloadListeners {

	public static void register(final AddClientReloadListenersEvent event) {
		event.addListener(ResourceReloadListener.ID, ResourceReloadListener.inst());
	}

}