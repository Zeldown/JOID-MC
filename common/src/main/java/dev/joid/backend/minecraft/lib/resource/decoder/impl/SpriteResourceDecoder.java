package dev.joid.backend.minecraft.lib.resource.decoder.impl;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpriteResourceDecoder implements IResourceDecoder {

	private final Material material;

	private TextureAtlasSprite sprite;

	public static @NonNull SpriteResourceDecoder create(final @NonNull Material material) {
		return new SpriteResourceDecoder(material);
	}

	@Override
	public void prepare(final @NonNull ResourceData resource) {
		final TextureAtlasSprite sprite = this.material.sprite();
		if (sprite.getName().equals(MissingTextureAtlasSprite.getLocation()) && !this.material.texture().equals(MissingTextureAtlasSprite.getLocation())) {
			throw new IllegalArgumentException("No sprite " + this.material.texture() + " in the atlas " + this.material.atlasLocation());
		}

		resource.texture(GlBorrowedTexture.of(this::getAtlas));
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
		final TextureAtlasSprite sprite = this.material.sprite();
		if (this.sprite != null && sprite != this.sprite) {
			this.region(resource, sprite);
		}
	}

	private void region(final ResourceData resource, final TextureAtlasSprite sprite) {
		final float atlasWidth = sprite.getWidth() / (sprite.getU1() - sprite.getU0());
		final float atlasHeight = sprite.getHeight() / (sprite.getV1() - sprite.getV0());
		resource.region(Math.round(sprite.getU0() * atlasWidth), Math.round(sprite.getV0() * atlasHeight), sprite.getWidth(), sprite.getHeight());
		this.sprite = sprite;
	}

	private int getAtlas() {
		return Minecraft.getInstance().getTextureManager().getTexture(this.material.atlasLocation()).getId();
	}

}