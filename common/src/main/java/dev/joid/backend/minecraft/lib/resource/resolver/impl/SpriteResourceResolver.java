package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import dev.joid.backend.minecraft.lib.resource.decoder.impl.SpriteResourceDecoder;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

public class SpriteResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof Material || input instanceof TextureAtlasSprite;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final Material material = input instanceof Material ? (Material) input : new Material(((TextureAtlasSprite) input).atlas().location(), ((TextureAtlasSprite) input).getName());
		final String uniqueId = "sprite:" + material.atlasLocation() + "#" + material.texture();
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, SpriteResourceDecoder.create(material))).nearest();
	}

}