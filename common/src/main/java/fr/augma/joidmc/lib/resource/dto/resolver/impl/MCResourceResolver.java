package fr.augma.joidmc.lib.resource.dto.resolver.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import be.zeldown.joid.lib.resource.Resource;
import be.zeldown.joid.lib.resource.ResourceBuilder;
import be.zeldown.joid.lib.resource.dto.ResourceData;
import be.zeldown.joid.lib.resource.dto.resolver.IResourceResolver;
import fr.augma.joidmc.lib.bridge.render.RenderBridge;
import fr.augma.joidmc.lib.bridge.render.texture.Texture;
import lombok.NonNull;

import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
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
		final Resource resource = builder.compute(identifier.toString(), () -> new ResourceData(identifier.toString(), null).texture(this.borrow(identifier)));
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

	private Texture borrow(final Identifier identifier) {
		return this.textureMap.computeIfAbsent(identifier, key -> new Texture((RenderBridge) BridgeHandler.RENDER.get())).borrow(MCResourceResolver.getView(identifier));
	}

	private static GpuTextureView getView(final Identifier identifier) {
		return Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView();
	}

}