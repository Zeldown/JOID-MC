package dev.joid.impl.minecraft.lib.resource.dto.resolver.impl;

import java.util.function.Consumer;

import dev.joid.impl.minecraft.lib.asset.dto.impl.MinecraftAsset;
import dev.joid.impl.minecraft.lib.asset.dto.locator.impl.MinecraftAssetLocator;
import dev.joid.impl.minecraft.lib.resource.dto.animation.impl.McmetaResourceAnimationReader;
import dev.joid.impl.minecraft.lib.resource.dto.reload.ResourceReloadListener;
import dev.joid.impl.minecraft.render.texture.BorrowedTexture;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.dto.ResourceData;
import dev.joid.lib.resource.dto.decoder.IResourceDecoder;
import dev.joid.lib.resource.dto.decoder.impl.AnimatedResourceDecoder;
import dev.joid.lib.resource.dto.format.ResourceFormat;
import dev.joid.lib.resource.dto.resolver.IResourceResolver;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.Identifier;

public class IdentifierResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		if (input instanceof Identifier) {
			return true;
		}

		final Identifier identifier = MinecraftAssetLocator.parse(input);
		return identifier != null && (IdentifierResourceResolver.isLoaded(identifier) || MinecraftAsset.create(identifier).exists());
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input, final Consumer<Resource> callback) {
		final MinecraftAsset asset = MinecraftAsset.create(MinecraftAssetLocator.parse(input));
		final Resource resource = builder.compute(asset.getUniqueId(), () -> IdentifierResourceResolver.read(asset)).nearest();
		if (callback != null) {
			callback.accept(resource);
		}
		return resource;
	}

	public static @NonNull IResourceDecoder decoder(final @NonNull MinecraftAsset asset) {
		final AnimationMetadataSection animation = asset.getAnimation();
		return animation == null ? ResourceFormat.decoder(asset) : new AnimatedResourceDecoder(asset, McmetaResourceAnimationReader.create(animation));
	}

	private static @NonNull ResourceData read(final @NonNull MinecraftAsset asset) {
		final Identifier identifier = asset.getIdentifier();
		if (IdentifierResourceResolver.isLoaded(identifier) && asset.getAnimation() == null) {
			return ResourceReloadListener.inst().track(new ResourceData(asset.getUniqueId(), null).texture(BorrowedTexture.of(() -> Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView())), asset);
		}

		try {
			return ResourceReloadListener.inst().track(new ResourceData(asset.getUniqueId(), IdentifierResourceResolver.decoder(asset)), asset);
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