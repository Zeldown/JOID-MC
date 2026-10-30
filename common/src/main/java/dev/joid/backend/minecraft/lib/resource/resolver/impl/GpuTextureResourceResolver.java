package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import java.util.function.Supplier;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import dev.joid.backend.minecraft.bridge.render.texture.MinecraftBorrowedTexture;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class GpuTextureResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof GpuTextureView || input instanceof GpuTexture || input instanceof AbstractTexture || input instanceof RenderTarget;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final String uniqueId = "gpu_texture_" + System.identityHashCode(input);
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, null).texture(MinecraftBorrowedTexture.of(GpuTextureResourceResolver.getView(input)))).nearest();
	}

	private static Supplier<GpuTextureView> getView(final Object input) {
		if (input instanceof final AbstractTexture texture) {
			return texture::getTextureView;
		}

		if (input instanceof final RenderTarget target) {
			return target::getColorTextureView;
		}

		final GpuTextureView view = input instanceof final GpuTexture texture ? RenderSystem.getDevice().createTextureView(texture) : (GpuTextureView) input;
		return () -> view;
	}

}