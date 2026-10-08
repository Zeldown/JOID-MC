package dev.joid.backend.minecraft.lib.resource.dto.reload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.lib.asset.dto.impl.IdentifierAsset;
import dev.joid.backend.minecraft.lib.resource.dto.resolver.impl.IdentifierResourceResolver;
import dev.joid.lib.resource.dto.ResourceData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceReloadListener implements ResourceManagerReloadListener {

	public static final Identifier ID = Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "resources");

	private static final ResourceReloadListener INSTANCE = new ResourceReloadListener();

	private final Map<ResourceData, IdentifierAsset> tracked = Collections.synchronizedMap(new WeakHashMap<>());

	public static @NonNull ResourceReloadListener inst() {
		return ResourceReloadListener.INSTANCE;
	}

	@Override
	public void onResourceManagerReload(final ResourceManager resourceManager) {
		final List<Entry<ResourceData, IdentifierAsset>> entries;
		synchronized (this.tracked) {
			entries = new ArrayList<>(this.tracked.entrySet());
		}

		for (final Entry<ResourceData, IdentifierAsset> entry : entries) {
			final ResourceData data = entry.getKey();
			if (data.getDecoder() == null) {
				data.generated(false);
				continue;
			}

			data.clear();
			data.textures(null).data(null).decoder(IdentifierResourceResolver.decoder(entry.getValue())).generated(false).loaded(false).uploaded(false);
		}
	}

	public @NonNull ResourceData track(final @NonNull ResourceData data, final @NonNull IdentifierAsset asset) {
		this.tracked.put(data, asset);
		return data;
	}

}