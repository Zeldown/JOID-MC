package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import java.util.function.IntSupplier;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class GpuTextureResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof AbstractTexture || input instanceof RenderTarget;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final String uniqueId = "gpu_texture_" + System.identityHashCode(input);
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, null).texture(GlBorrowedTexture.of(GpuTextureResourceResolver.getTexture(input)))).nearest();
	}

	private static IntSupplier getTexture(final Object input) {
		if (input instanceof RenderTarget) {
			return ((RenderTarget) input)::getColorTextureId;
		}

		return ((AbstractTexture) input)::getId;
	}

}