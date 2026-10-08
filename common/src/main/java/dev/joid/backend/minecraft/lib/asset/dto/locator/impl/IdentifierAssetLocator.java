package dev.joid.backend.minecraft.lib.asset.dto.locator.impl;

import dev.joid.backend.minecraft.lib.asset.dto.impl.IdentifierAsset;
import dev.joid.lib.asset.Asset;
import dev.joid.lib.asset.dto.locator.IAssetLocator;
import lombok.NonNull;

import net.minecraft.resources.Identifier;

public class IdentifierAssetLocator implements IAssetLocator {

	@Override
	public boolean supports(final @NonNull Object handle) {
		if (handle instanceof Identifier) {
			return true;
		}

		final Identifier identifier = IdentifierAssetLocator.parse(handle);
		return identifier != null && IdentifierAsset.create(identifier).exists();
	}

	@Override
	public @NonNull Asset locate(final @NonNull Object handle) {
		return IdentifierAsset.create(IdentifierAssetLocator.parse(handle));
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