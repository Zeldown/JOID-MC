package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricReloadListenerRegistry {

	public static void register() {
		for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
			ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

				@Override
				public ResourceLocation getFabricId() {
					return ResourceLocation.tryParse(listener.getId());
				}

				@Override
				public void onResourceManagerReload(final ResourceManager manager) {
					listener.getOnReload().run();
				}

			});
		}
	}

}