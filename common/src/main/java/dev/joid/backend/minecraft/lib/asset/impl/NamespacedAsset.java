package dev.joid.backend.minecraft.lib.asset.impl;

import java.io.IOException;
import java.io.InputStream;

import dev.joid.lib.asset.Asset;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

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
		return this.getResource().getInputStream();
	}

	public boolean exists() {
		try {
			this.getResource().getInputStream().close();
			return true;
		} catch (final IOException exception) {
			return false;
		}
	}

	public AnimationMetadataSection getAnimation() {
		try {
			return (AnimationMetadataSection) this.getResource().getMetadata("animation");
		} catch (final IOException | RuntimeException exception) {
			return null;
		}
	}

	private IResource getResource() throws IOException {
		return Minecraft.getMinecraft().getResourceManager().getResource(this.id);
	}

}