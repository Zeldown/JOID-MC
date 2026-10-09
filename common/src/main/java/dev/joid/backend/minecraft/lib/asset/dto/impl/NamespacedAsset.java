package dev.joid.backend.minecraft.lib.asset.dto.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dev.joid.lib.asset.Asset;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

@Getter
public final class NamespacedAsset extends Asset {

	private final Identifier id;

	private NamespacedAsset(final Identifier id) {
		super(id.toString());
		this.id = id;
	}

	public static @NonNull NamespacedAsset create(final @NonNull Identifier id) {
		return new NamespacedAsset(id);
	}

	@Override
	public @NonNull InputStream open() throws IOException {
		return this.getResource().open();
	}

	public boolean exists() {
		return Minecraft.getInstance().getResourceManager().getResource(this.id).isPresent();
	}

	public AnimationMetadataSection getAnimation() {
		try {
			return this.getResource().metadata().getSection(AnimationMetadataSection.TYPE).orElse(null);
		} catch (final IOException exception) {
			return null;
		}
	}

	private Resource getResource() throws IOException {
		return Minecraft.getInstance().getResourceManager().getResource(this.id).orElseThrow(() -> new FileNotFoundException("No resource " + this.id + " in the loaded resource packs"));
	}

}