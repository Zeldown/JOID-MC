package dev.joid.backend.minecraft.lib.resource.decoder.impl;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpriteResourceDecoder implements IResourceDecoder {

	private final String name;

	private TextureAtlasSprite sprite;

	public static @NonNull SpriteResourceDecoder create(final @NonNull TextureAtlasSprite sprite) {
		return new SpriteResourceDecoder(sprite.getIconName());
	}

	@Override
	public void prepare(final @NonNull ResourceData resource) {
		final TextureMap atlas = Minecraft.getMinecraft().getTextureMapBlocks();
		final TextureAtlasSprite sprite = atlas.getAtlasSprite(this.name);
		if (sprite == atlas.getMissingSprite() && !this.name.equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
			throw new IllegalArgumentException("No sprite " + this.name + " in the block atlas of the game");
		}

		resource.texture(GlBorrowedTexture.of(() -> Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId()));
		this.region(resource, sprite);
	}

	@Override
	public void decode(final @NonNull ResourceData resource) {}

	@Override
	public void upload(final @NonNull ResourceData resource) {}

	@Override
	public void clear(final @NonNull ResourceData resource) {
		this.sprite = null;
	}

	@Override
	public void update(final @NonNull ResourceData resource) {
		final TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(this.name);
		if (this.sprite != null && sprite != this.sprite) {
			this.region(resource, sprite);
		}
	}

	private void region(final ResourceData resource, final TextureAtlasSprite sprite) {
		resource.region(sprite.getOriginX(), sprite.getOriginY(), sprite.getIconWidth(), sprite.getIconHeight());
		this.sprite = sprite;
	}

}