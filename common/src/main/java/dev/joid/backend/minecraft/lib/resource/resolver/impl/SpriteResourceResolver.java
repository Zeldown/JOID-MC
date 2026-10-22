package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import dev.joid.backend.minecraft.lib.resource.decoder.impl.SpriteResourceDecoder;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;

public class SpriteResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof SpriteId || input instanceof TextureAtlasSprite;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final SpriteId spriteId = input instanceof SpriteId ? (SpriteId) input : new SpriteId(((TextureAtlasSprite) input).atlasLocation(), ((TextureAtlasSprite) input).contents().name());
		final String uniqueId = "sprite:" + spriteId.atlasLocation() + "#" + spriteId.texture();
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, new SpriteResourceDecoder(spriteId))).nearest();
	}

}