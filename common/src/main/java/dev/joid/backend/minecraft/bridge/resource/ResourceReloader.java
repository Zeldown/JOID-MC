package dev.joid.backend.minecraft.bridge.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import dev.joid.backend.minecraft.lib.asset.impl.NamespacedAsset;
import dev.joid.backend.minecraft.lib.resource.resolver.impl.NamespacedResourceResolver;
import dev.joid.lib.resource.ResourceData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceReloader {

	private static final ResourceReloader INSTANCE = new ResourceReloader();

	private final Map<ResourceData, NamespacedAsset> tracked = Collections.synchronizedMap(new WeakHashMap<>());

	public static @NonNull ResourceReloader inst() {
		return ResourceReloader.INSTANCE;
	}

	public void reload() {
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