package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import dev.joid.backend.minecraft.lib.resource.decoder.impl.SpriteResourceDecoder;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof TextureAtlasSprite;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final TextureAtlasSprite sprite = (TextureAtlasSprite) input;
		final String uniqueId = "sprite:" + sprite.getIconName();
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, SpriteResourceDecoder.create(sprite))).nearest();
	}

}