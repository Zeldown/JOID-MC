package dev.joid.impl.minecraft.fabric;

import dev.joid.impl.minecraft.lib.resource.dto.reload.ResourceReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricReloadListeners {

	public static void register() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ResourceReloadListener.ID, ResourceReloadListener.inst());
	}

}