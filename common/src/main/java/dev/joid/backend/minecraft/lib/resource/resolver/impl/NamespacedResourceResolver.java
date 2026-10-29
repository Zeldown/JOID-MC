package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import dev.joid.backend.minecraft.bridge.resource.ResourceReloader;
import dev.joid.backend.minecraft.lib.asset.impl.NamespacedAsset;
import dev.joid.backend.minecraft.lib.asset.locator.impl.NamespacedAssetLocator;
import dev.joid.backend.minecraft.lib.resource.animation.impl.McmetaResourceAnimationReader;
import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import dev.joid.lib.resource.decoder.impl.AnimatedResourceDecoder;
import dev.joid.lib.resource.format.ResourceFormat;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

public class NamespacedResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		if (input instanceof ResourceLocation) {
			return true;
		}

		final ResourceLocation location = NamespacedAssetLocator.parse(input);
		return location != null && (NamespacedResourceResolver.isLoaded(location) || NamespacedAsset.of(location).exists());
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final NamespacedAsset asset = NamespacedAsset.of(NamespacedAssetLocator.parse(input));
		return builder.compute(asset.getUniqueId(), () -> NamespacedResourceResolver.read(asset)).nearest();
	}

	public static @NonNull IResourceDecoder decoder(final @NonNull NamespacedAsset asset) {
		final AnimationMetadataSection animation = asset.getAnimation();
		return animation == null ? ResourceFormat.decoder(asset) : AnimatedResourceDecoder.create(asset, new McmetaResourceAnimationReader(animation));
	}

	private static @NonNull ResourceData read(final NamespacedAsset asset) {
		final ResourceLocation location = asset.getId();
		if (NamespacedResourceResolver.isLoaded(location) && asset.getAnimation() == null) {
			return ResourceReloader.inst().track(ResourceData.create(asset.getUniqueId(), null).texture(GlBorrowedTexture.of(() -> Minecraft.getMinecraft().getTextureManager().getTexture(location).getGlTextureId())), asset);
		}

		try {
			return ResourceReloader.inst().track(ResourceData.create(asset.getUniqueId(), NamespacedResourceResolver.decoder(asset)), asset);
		} catch (final RuntimeException exception) {
			final ResourceData data = ResourceData.create(asset.getUniqueId(), null);
			data.fail(exception);
			return data;
		}
	}

	private static boolean isLoaded(final ResourceLocation location) {
		final ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(location);
		return texture != null && texture.getGlTextureId() != -1;
	}

}