package dev.joid.backend.minecraft.bridge.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.lib.asset.dto.impl.NamespacedAsset;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.NamespacedResourceResolver;
import dev.joid.lib.resource.dto.ResourceData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceReloader implements ResourceManagerReloadListener {

	public static final Identifier ID = Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "resources");

	private static final ResourceReloader INSTANCE = new ResourceReloader();

	private final Map<ResourceData, NamespacedAsset> tracked = Collections.synchronizedMap(new WeakHashMap<>());

	public static @NonNull ResourceReloader inst() {
		return ResourceReloader.INSTANCE;
	}

	@Override
	public void onResourceManagerReload(final ResourceManager resourceManager) {
		final List<Entry<ResourceData, NamespacedAsset>> entries;
		synchronized (this.tracked) {
			entries = new ArrayList<>(this.tracked.entrySet());
		}

		for (final Entry<ResourceData, NamespacedAsset> entry : entries) {
			final ResourceData data = entry.getKey();
			if (data.getDecoder() == null) {
				data.generated(false);
				continue;
			}

			data.clear();
			data.textures(null).data(null).decoder(NamespacedResourceResolver.decoder(entry.getValue())).generated(false).loaded(false).uploaded(false);
		}
	}

	public @NonNull ResourceData track(final @NonNull ResourceData data, final @NonNull NamespacedAsset asset) {
		this.tracked.put(data, asset);
		return data;
	}

}