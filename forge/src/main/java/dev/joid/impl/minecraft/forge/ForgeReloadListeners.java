package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.lib.resource.dto.reload.ResourceReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListeners {

	public static void register() {
		RegisterClientReloadListenersEvent.BUS.addListener(event -> event.registerReloadListener(ResourceReloadListener.inst()));
	}

}