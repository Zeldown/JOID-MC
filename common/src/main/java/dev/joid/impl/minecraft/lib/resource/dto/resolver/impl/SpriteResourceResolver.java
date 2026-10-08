package dev.joid.impl.minecraft.lib.resource.dto.resolver.impl;

import java.util.function.Consumer;

import dev.joid.impl.minecraft.lib.resource.dto.decoder.impl.SpriteResourceDecoder;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.dto.ResourceData;
import dev.joid.lib.resource.dto.resolver.IResourceResolver;
import lombok.NonNull;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;

public class SpriteResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof SpriteId || input instanceof TextureAtlasSprite;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input, final Consumer<Resource> callback) {
		final SpriteId spriteId = input instanceof SpriteId ? (SpriteId) input : new SpriteId(((TextureAtlasSprite) input).atlasLocation(), ((TextureAtlasSprite) input).contents().name());
		final String uniqueId = "sprite:" + spriteId.atlasLocation() + "#" + spriteId.texture();
		final Resource resource = builder.compute(uniqueId, () -> new ResourceData(uniqueId, new SpriteResourceDecoder(spriteId)));
		if (callback != null) {
			callback.accept(resource);
		}
		return resource;
	}

}