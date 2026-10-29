package dev.joid.backend.minecraft.lib.resource.decoder.impl;

import java.util.Arrays;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.IResourceDecoder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SpriteResourceDecoder implements IResourceDecoder {

	private final IIcon icon;

	private int[]            region;
	private ResourceLocation atlas;

	public static @NonNull SpriteResourceDecoder create(final @NonNull IIcon icon) {
		return new SpriteResourceDecoder(icon);
	}

	@Override
	public void prepare(final @NonNull ResourceData resource) {
		this.atlas = SpriteResourceDecoder.findAtlas(this.icon);
		if (this.atlas == null) {
			throw new IllegalArgumentException("The sprite " + this.icon.getIconName() + " is in neither the block atlas nor the item atlas of the game");
		}

		resource.texture(GlBorrowedTexture.of(() -> Minecraft.getMinecraft().getTextureManager().getTexture(this.atlas).getGlTextureId()));
		this.region(resource);
	}

	@Override
	public void decode(final @NonNull ResourceData resource) {}

	@Override
	public void upload(final @NonNull ResourceData resource) {}

	@Override
	public void clear(final @NonNull ResourceData resource) {
		this.region = null;
	}

	@Override
	public void update(final @NonNull ResourceData resource) {
		if (this.region != null) {
			this.region(resource);
		}
	}

	private void region(final ResourceData resource) {
		final int[] region = this.icon instanceof TextureAtlasSprite ? new int[] {((TextureAtlasSprite) this.icon).getOriginX(), ((TextureAtlasSprite) this.icon).getOriginY(), this.icon.getIconWidth(), this.icon.getIconHeight()} : new int[] {Math.round(this.icon.getMinU() * resource.getTexture(0).getWidth()), Math.round(this.icon.getMinV() * resource.getTexture(0).getHeight()), this.icon.getIconWidth(), this.icon.getIconHeight()};
		if (!Arrays.equals(region, this.region)) {
			resource.region(region[0], region[1], region[2], region[3]);
			this.region = region;
		}
	}

	private static ResourceLocation findAtlas(final IIcon icon) {
		for (final ResourceLocation atlas : new ResourceLocation[] {TextureMap.locationBlocksTexture, TextureMap.locationItemsTexture}) {
			final ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(atlas);
			if (texture instanceof TextureMap && ((TextureMap) texture).getTextureExtry(icon.getIconName()) == icon) {
				return atlas;
			}
		}
		return null;
	}

}