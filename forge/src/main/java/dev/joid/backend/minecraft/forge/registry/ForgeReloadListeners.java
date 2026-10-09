package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListeners {

	public static void register() {
		RegisterClientReloadListenersEvent.BUS.addListener(event -> {
			for (final ReloadListener listener : ReloadListener.getRegistered()) {
				event.registerReloadListener((ResourceManagerReloadListener) _ -> listener.getOnReload().run());
			}
		});
	}

}