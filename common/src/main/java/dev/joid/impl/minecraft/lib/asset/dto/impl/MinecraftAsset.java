package dev.joid.impl.minecraft.lib.asset.dto.impl;

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
public final class MinecraftAsset extends Asset {

	private final Identifier identifier;

	private MinecraftAsset(final Identifier identifier) {
		super(identifier.toString());
		this.identifier = identifier;
	}

	public static @NonNull MinecraftAsset create(final @NonNull Identifier identifier) {
		return new MinecraftAsset(identifier);
	}

	@Override
	public @NonNull InputStream open() throws IOException {
		return this.getResource().open();
	}

	public boolean exists() {
		return Minecraft.getInstance().getResourceManager().getResource(this.identifier).isPresent();
	}

	public AnimationMetadataSection getAnimation() {
		try {
			return this.getResource().metadata().getSection(AnimationMetadataSection.TYPE).orElse(null);
		} catch (final IOException exception) {
			return null;
		}
	}

	private Resource getResource() throws IOException {
		return Minecraft.getInstance().getResourceManager().getResource(this.identifier).orElseThrow(() -> new FileNotFoundException("No resource " + this.identifier + " in the loaded resource packs"));
	}

}