package dev.joid.impl.joidmc.lib.resource.dto.resolver.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.dto.ResourceData;
import dev.joid.lib.resource.dto.resolver.IResourceResolver;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;
import dev.joid.impl.joidmc.lib.resource.dto.decoder.impl.MCAnimationDecoder;
import lombok.NonNull;

import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class MCResourceResolver implements IResourceResolver, ResourceManagerReloadListener {

	private static final MCResourceResolver INSTANCE = new MCResourceResolver();

	private final Map<Identifier, Texture> textureMap;

	private MCResourceResolver() {
		this.textureMap = new ConcurrentHashMap<>();
	}

	public static @NonNull MCResourceResolver inst() {
		return MCResourceResolver.INSTANCE;
	}

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof Identifier;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input, final Consumer<Resource> callback) {
		final Identifier identifier = (Identifier) input;
		final Resource resource = builder.compute(identifier.toString(), () -> this.data(identifier));
		if (callback != null) {
			callback.accept(resource);
		}

		return resource;
	}

	@Override
	public void onResourceManagerReload(final ResourceManager manager) {
		this.textureMap.forEach((identifier, texture) -> texture.borrow(MCResourceResolver.getView(identifier)));
		ResourceBuilder.getBuilders().forEach(ResourceBuilder::reload);
	}

	private ResourceData data(final Identifier identifier) {
		final String key = identifier.toString();
		final AnimationMetadataSection section = MCResourceResolver.getSection(identifier);
		final BufferedImage image = section == null ? null : MCResourceResolver.getImage(identifier);
		if (image == null) {
			return new ResourceData(key, null).texture(this.borrow(identifier));
		}

		return new ResourceData(key, new MCAnimationDecoder(section, image));
	}

	private Texture borrow(final Identifier identifier) {
		return this.textureMap.computeIfAbsent(identifier, key -> new Texture((RenderBridge) BridgeHandler.RENDER.get())).borrow(MCResourceResolver.getView(identifier));
	}

	private static GpuTextureView getView(final Identifier identifier) {
		return Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView();
	}

	private static AnimationMetadataSection getSection(final Identifier identifier) {
		try {
			return Minecraft.getInstance().getResourceManager().getResourceOrThrow(identifier).metadata().getSection(AnimationMetadataSection.TYPE).orElse(null);
		} catch (final IOException silent) {
			return null;
		}
	}

	private static BufferedImage getImage(final Identifier identifier) {
		try (final InputStream stream = Minecraft.getInstance().getResourceManager().open(identifier)) {
			return ImageIO.read(stream);
		} catch (final IOException silent) {
			return null;
		}
	}

}