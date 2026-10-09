package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricReloadListeners {

	public static void register() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ResourceReloader.ID, ResourceReloader.inst());
	}

}