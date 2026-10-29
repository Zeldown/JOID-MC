package dev.joid.backend.minecraft.lib.asset.locator.impl;

import dev.joid.backend.minecraft.lib.asset.impl.NamespacedAsset;
import dev.joid.lib.asset.Asset;
import dev.joid.lib.asset.locator.IAssetLocator;
import lombok.NonNull;
import net.minecraft.util.ResourceLocation;

public class NamespacedAssetLocator implements IAssetLocator {

	@Override
	public boolean supports(final @NonNull Object handle) {
		if (handle instanceof ResourceLocation) {
			return true;
		}

		final ResourceLocation location = NamespacedAssetLocator.parse(handle);
		return location != null && NamespacedAsset.of(location).exists();
	}

	@Override
	public @NonNull Asset locate(final @NonNull Object handle) {
		return NamespacedAsset.of(NamespacedAssetLocator.parse(handle));
	}

	public static ResourceLocation parse(final @NonNull Object handle) {
		if (handle instanceof ResourceLocation) {
			return (ResourceLocation) handle;
		}

		if (!(handle instanceof String) || ((String) handle).contains("://") || !((String) handle).matches("([a-z0-9_.-]+:)?[a-z0-9_./-]+")) {
			return null;
		}

		return new ResourceLocation((String) handle);
	}

}