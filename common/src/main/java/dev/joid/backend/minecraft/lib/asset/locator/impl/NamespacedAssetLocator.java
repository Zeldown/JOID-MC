package dev.joid.backend.minecraft.lib.asset.locator.impl;

import dev.joid.backend.minecraft.lib.asset.impl.NamespacedAsset;
import dev.joid.lib.asset.Asset;
import dev.joid.lib.asset.locator.IAssetLocator;
import lombok.NonNull;

import net.minecraft.resources.Identifier;

public class NamespacedAssetLocator implements IAssetLocator {

	@Override
	public boolean supports(final @NonNull Object handle) {
		if (handle instanceof Identifier) {
			return true;
		}

		final Identifier identifier = NamespacedAssetLocator.parse(handle);
		return identifier != null && NamespacedAsset.create(identifier).exists();
	}

	@Override
	public @NonNull Asset locate(final @NonNull Object handle) {
		return NamespacedAsset.create(NamespacedAssetLocator.parse(handle));
	}

	public static Identifier parse(final @NonNull Object handle) {
		if (handle instanceof Identifier) {
			return (Identifier) handle;
		}

		if (!(handle instanceof String) || ((String) handle).contains("://")) {
			return null;
		}

		return Identifier.tryParse((String) handle);
	}

}