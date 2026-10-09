package dev.joid.backend.minecraft.lib.resource.decoder.impl;

import dev.joid.backend.minecraft.bridge.render.texture.GpuBorrowedTexture;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;

@Getter
public class SpriteResourceDecoder implements IResourceDecoder {

	private final SpriteId spriteId;

	private TextureAtlasSprite sprite;

	public SpriteResourceDecoder(final @NonNull SpriteId spriteId) {
		this.spriteId = spriteId;
	}

	@Override
	public void init(final @NonNull ResourceData resource) {}

	@Override
	public void prepare(final @NonNull ResourceData resource) {
		final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().get(this.spriteId);
		if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) && !this.spriteId.texture().equals(MissingTextureAtlasSprite.getLocation())) {
			throw new IllegalArgumentException("No sprite " + this.spriteId.texture() + " in the atlas " + this.spriteId.atlasLocation());
		}

		resource.texture(GpuBorrowedTexture.create(this::getAtlas));
		this.region(resource, sprite);
	}

	@Override
	public void decode(final @NonNull ResourceData resource) {}

	@Override
	public void upload(final @NonNull ResourceData resource) {}

	@Override
	public void update(final @NonNull ResourceData resource) {
		final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().get(this.spriteId);
		if (this.sprite != null && sprite != this.sprite) {
			this.region(resource, sprite);
		}
	}

	@Override
	public void clear(final @NonNull ResourceData resource) {
		this.sprite = null;
	}

	private void region(final ResourceData resource, final TextureAtlasSprite sprite) {
		final GpuTextureView atlas = this.getAtlas();
		resource.region(Math.round(sprite.getU0() * atlas.getWidth(0)), Math.round(sprite.getV0() * atlas.getHeight(0)), sprite.contents().width(), sprite.contents().height());
		this.sprite = sprite;
	}

	private GpuTextureView getAtlas() {
		return Minecraft.getInstance().getTextureManager().getTexture(this.spriteId.atlasLocation()).getTextureView();
	}

}