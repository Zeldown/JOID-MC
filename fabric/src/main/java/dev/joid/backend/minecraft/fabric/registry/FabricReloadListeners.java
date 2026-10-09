package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricReloadListeners {

	public static void register() {
		for (final ReloadListener listener : ReloadListener.getRegistered()) {
			ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.parse(listener.getId()), (ResourceManagerReloadListener) _ -> listener.getOnReload().run());
		}
	}

}