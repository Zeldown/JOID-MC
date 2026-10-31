package dev.joid.backend.minecraft.lib.resource.resolver.impl;

import java.awt.image.BufferedImage;

import com.mojang.blaze3d.platform.NativeImage;

import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.resource.ResourceData;
import dev.joid.lib.resource.decoder.impl.RasterResourceDecoder;
import dev.joid.lib.resource.resolver.IResourceResolver;
import lombok.NonNull;

public class NativeImageResourceResolver implements IResourceResolver {

	@Override
	public boolean supports(final @NonNull Object input) {
		return input instanceof NativeImage;
	}

	@Override
	public @NonNull Resource resolve(final @NonNull ResourceBuilder builder, final @NonNull Object input) {
		final NativeImage image = (NativeImage) input;
		final String uniqueId = "native_image_" + System.identityHashCode(image);
		return builder.compute(uniqueId, () -> ResourceData.create(uniqueId, RasterResourceDecoder.create(NativeImageResourceResolver.toBufferedImage(image)))).nearest();
	}

	private static BufferedImage toBufferedImage(final NativeImage image) {
		final BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				final int pixel = image.getPixelRGBA(x, y);
				copy.setRGB(x, y, pixel & 0xFF00FF00 | (pixel & 0xFF) << 16 | pixel >> 16 & 0xFF);
			}
		}
		return copy;
	}

}