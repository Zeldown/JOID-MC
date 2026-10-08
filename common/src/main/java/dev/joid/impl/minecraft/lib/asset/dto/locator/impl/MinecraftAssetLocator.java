package dev.joid.impl.minecraft.lib.asset.dto.locator.impl;

import dev.joid.impl.minecraft.lib.asset.dto.impl.MinecraftAsset;
import dev.joid.lib.asset.Asset;
import dev.joid.lib.asset.dto.locator.IAssetLocator;
import lombok.NonNull;

import net.minecraft.resources.Identifier;

public class MinecraftAssetLocator implements IAssetLocator {

	@Override
	public boolean supports(final @NonNull Object handle) {
		if (handle instanceof Identifier) {
			return true;
		}

		final Identifier identifier = MinecraftAssetLocator.parse(handle);
		return identifier != null && MinecraftAsset.create(identifier).exists();
	}

	@Override
	public @NonNull Asset locate(final @NonNull Object handle) {
		return MinecraftAsset.create(MinecraftAssetLocator.parse(handle));
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