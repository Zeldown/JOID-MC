package dev.joid.backend.minecraft.lib.resource.dto.decoder.impl;

import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.texture.Texture;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.resource.dto.ResourceData;
import dev.joid.lib.resource.dto.decoder.IResourceDecoder;
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

	private Texture            texture;
	private TextureAtlasSprite sprite;
	private long               frame;

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

		this.texture = Texture.create((RenderBridge) BridgeHandler.RENDER.get());
		resource.texture(this.texture).width(sprite.contents().width()).height(sprite.contents().height());
	}

	@Override
	public void decode(final @NonNull ResourceData resource) {}

	@Override
	public void upload(final @NonNull ResourceData resource) {}

	@Override
	public void update(final @NonNull ResourceData resource) {
		final TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().get(this.spriteId);
		final long frame = Minecraft.getInstance().getFrameTimeNs();
		if (this.texture == null || sprite == this.sprite && (!sprite.isAnimated() || frame == this.frame)) {
			return;
		}

		final GpuTextureView atlas = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
		final int x = Math.round(sprite.getU0() * atlas.getWidth(0));
		final int y = Math.round(sprite.getV0() * atlas.getHeight(0));
		this.texture.copy(atlas, x, y, sprite.contents().width(), sprite.contents().height());
		resource.width(sprite.contents().width()).height(sprite.contents().height());
		this.sprite = sprite;
		this.frame  = frame;
	}

	@Override
	public void clear(final @NonNull ResourceData resource) {
		this.texture = null;
		this.sprite  = null;
	}

}