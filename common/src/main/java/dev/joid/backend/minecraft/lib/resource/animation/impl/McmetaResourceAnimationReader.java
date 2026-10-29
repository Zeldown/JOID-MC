package dev.joid.backend.minecraft.lib.resource.animation.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import dev.joid.lib.resource.animation.IResourceAnimationReader;
import dev.joid.lib.resource.animation.ResourceAnimation;
import dev.joid.lib.resource.animation.ResourceAnimationFrame;
import dev.joid.lib.utils.image.ImageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.data.AnimationMetadataSection;

@RequiredArgsConstructor
public final class McmetaResourceAnimationReader implements IResourceAnimationReader {

	private final AnimationMetadataSection metadata;

	@Override
	public @NonNull ResourceAnimation read(final @NonNull InputStream stream) throws IOException {
		final BufferedImage image = ImageIO.read(stream);
		if (image == null) {
			throw new IOException("The animated texture is not a readable image");
		}

		final int width = this.metadata.getFrameWidth() > 0 ? this.metadata.getFrameWidth() : image.getWidth();
		final int height = this.metadata.getFrameHeight() > 0 ? this.metadata.getFrameHeight() : width;
		final int columns = image.getWidth() / width;
		final int count = columns * (image.getHeight() / height);
		final List<int[]> frames = new ArrayList<>();
		for (int i = 0; i < (this.metadata.getFrameCount() > 0 ? this.metadata.getFrameCount() : count); i++) {
			final int index = this.metadata.getFrameCount() > 0 ? this.metadata.getFrameIndex(i) : i;
			if (index < count) {
				frames.add(new int[] {index, this.metadata.getFrameCount() > 0 ? this.metadata.getFrameTimeSingle(i) : this.metadata.getFrameTime()});
			}
		}

		if (frames.isEmpty()) {
			throw new IOException("The animated texture has no frame inside its image");
		}

		final List<ResourceAnimationFrame> animation = new ArrayList<>();
		for (final int[] frame : frames) {
			final int[] pixels = new int[width * height];
			image.getRGB(frame[0] % columns * width, frame[0] / columns * height, width, height, pixels, 0, width);
			ImageUtils.bleedAlpha(pixels, width, height);
			animation.add(ResourceAnimationFrame.of(pixels, Math.max(1, frame[1]) * 50L));
		}

		return ResourceAnimation.of(width, height, 0, animation);
	}

}