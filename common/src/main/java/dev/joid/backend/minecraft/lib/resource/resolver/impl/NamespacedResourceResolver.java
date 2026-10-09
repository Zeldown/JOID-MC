package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import java.util.function.Consumer;

import dev.joid.backend.minecraft.bridge.render.texture.GpuBorrowedTexture;
import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import dev.joid.backend.minecraft.lib.asset.impl.NamespacedAsset;
import dev.joid.backend.minecraft.lib.asset.locator.impl.NamespacedAssetLocator;
import dev.joid.backend.minecraft.lib.resource.animation.impl.McmetaResourceAnimationReader;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import dev.joid.lib.resource.decoder.impl.AnimatedResourceDecoder;
import dev.joid.lib.resource.format.ResourceFormat;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.Identifier;

public class NamespacedResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		if (input instanceof Identifier) {
			return true;
		}

		final Identifier identifier = NamespacedAssetLocator.parse(input);
		return identifier != null && (NamespacedResourceResolver.isLoaded(identifier) || NamespacedAsset.create(identifier).exists());
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input, final Consumer<Resource> callback) {
		final NamespacedAsset asset = NamespacedAsset.create(NamespacedAssetLocator.parse(input));
		final Resource resource = builder.compute(asset.getUniqueId(), () -> NamespacedResourceResolver.read(asset)).nearest();
		if (callback != null) {
			callback.accept(resource);
		}
		return resource;
	}

	public static @NonNull IResourceDecoder decoder(final @NonNull NamespacedAsset asset) {
		final AnimationMetadataSection animation = asset.getAnimation();
		return animation == null ? ResourceFormat.decoder(asset) : new AnimatedResourceDecoder(asset, McmetaResourceAnimationReader.create(animation));
	}

	private static @NonNull ResourceData read(final @NonNull NamespacedAsset asset) {
		final Identifier identifier = asset.getId();
		if (NamespacedResourceResolver.isLoaded(identifier) && asset.getAnimation() == null) {
			return ResourceReloader.inst().track(new ResourceData(asset.getUniqueId(), null).texture(GpuBorrowedTexture.create(() -> Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView())), asset);
		}

		try {
			return ResourceReloader.inst().track(new ResourceData(asset.getUniqueId(), NamespacedResourceResolver.decoder(asset)), asset);
		} catch (final RuntimeException exception) {
			final ResourceData data = new ResourceData(asset.getUniqueId(), null);
			data.fail(exception);
			return data;
		}
	}

	private static boolean isLoaded(final @NonNull Identifier identifier) {
		return Minecraft.getInstance().getTextureManager().byPath.containsKey(identifier);
	}

}