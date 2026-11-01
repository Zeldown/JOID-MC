package dev.joid.backend.minecraft.lib.asset.impl;

import java.io.IOException;
import java.io.InputStream;

import dev.joid.lib.asset.Asset;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

@Getter
public final class NamespacedAsset extends Asset {

	private final ResourceLocation id;

	private NamespacedAsset(final ResourceLocation id) {
		super(id.toString());
		this.id = id;
	}

	public static @NonNull NamespacedAsset of(final @NonNull ResourceLocation id) {
		return new NamespacedAsset(id);
	}

	@Override
	public @NonNull InputStream open() throws IOException {
		return Minecraft.getInstance().getResourceManager().getResource(this.id).getInputStream();
	}

	public boolean exists() {
		return Minecraft.getInstance().getResourceManager().hasResource(this.id);
	}

	public AnimationMetadataSection getAnimation() {
		try (final Resource resource = Minecraft.getInstance().getResourceManager().getResource(this.id)) {
			return resource.getMetadata(AnimationMetadataSection.SERIALIZER);
		} catch (final IOException exception) {
			return null;
		}
	}

}