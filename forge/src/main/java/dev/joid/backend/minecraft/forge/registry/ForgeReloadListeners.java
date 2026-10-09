package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListeners {

	public static void register() {
		RegisterClientReloadListenersEvent.BUS.addListener(event -> event.registerReloadListener(ResourceReloader.inst()));
	}

}