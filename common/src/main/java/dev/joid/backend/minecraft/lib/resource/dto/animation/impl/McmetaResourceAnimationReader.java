package dev.joid.backend.minecraft.lib.resource.dto.animation.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import dev.joid.lib.resource.dto.animation.IResourceAnimationReader;
import dev.joid.lib.resource.dto.animation.ResourceAnimation;
import dev.joid.lib.resource.dto.animation.ResourceAnimationFrame;
import dev.joid.lib.utils.image.ImageUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class McmetaResourceAnimationReader implements IResourceAnimationReader {

	private final AnimationMetadataSection metadata;

	public static @NonNull McmetaResourceAnimationReader create(final @NonNull AnimationMetadataSection metadata) {
		return new McmetaResourceAnimationReader(metadata);
	}

	@Override
	public @NonNull ResourceAnimation read(final @NonNull InputStream stream) throws IOException {
		final BufferedImage image = ImageIO.read(stream);
		if (image == null) {
			throw new IOException("The animated texture is not a readable image");
		}

		final FrameSize size = this.metadata.calculateFrameSize(image.getWidth(), image.getHeight());
		final int columns = image.getWidth() / size.width();
		final int count = columns * (image.getHeight() / size.height());
		final List<AnimationFrame> frames = new ArrayList<>();
		for (final AnimationFrame frame : this.metadata.frames().orElseGet(() -> McmetaResourceAnimationReader.sequence(count))) {
			if (frame.index() < count) {
				frames.add(frame);
			}
		}

		if (frames.isEmpty()) {
			throw new IOException("The animated texture has no frame inside its image");
		}

		final List<int[]> pixels = new ArrayList<>();
		for (final AnimationFrame frame : frames) {
			final int[] framePixels = new int[size.width() * size.height()];
			image.getRGB(frame.index() % columns * size.width(), frame.index() / columns * size.height(), size.width(), size.height(), framePixels, 0, size.width());
			pixels.add(framePixels);
		}

		final List<ResourceAnimationFrame> animation = new ArrayList<>();
		for (int i = 0; i < frames.size(); i++) {
			final int time = frames.get(i).timeOr(this.metadata.defaultFrameTime());
			if (!this.metadata.interpolatedFrames()) {
				animation.add(McmetaResourceAnimationReader.frame(pixels.get(i), size, time));
				continue;
			}

			for (int tick = 0; tick < time; tick++) {
				animation.add(McmetaResourceAnimationReader.frame(McmetaResourceAnimationReader.mix(pixels.get(i), pixels.get((i + 1) % frames.size()), (float) tick / time), size, 1));
			}
		}

		return ResourceAnimation.create(size.width(), size.height(), 0, animation);
	}

	private static @NonNull List<AnimationFrame> sequence(final int count) {
		final List<AnimationFrame> frames = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			frames.add(new AnimationFrame(i));
		}
		return frames;
	}

	private static @NonNull ResourceAnimationFrame frame(final @NonNull int[] pixels, final @NonNull FrameSize size, final int ticks) {
		final int[] copy = pixels.clone();
		ImageUtils.bleedAlpha(copy, size.width(), size.height());
		return ResourceAnimationFrame.create(copy, ticks * 50L);
	}

	private static @NonNull int[] mix(final @NonNull int[] current, final @NonNull int[] next, final float progress) {
		final int[] mixed = new int[current.length];
		for (int i = 0; i < mixed.length; i++) {
			int pixel = 0;
			for (int shift = 0; shift < 32; shift += 8) {
				final int from = current[i] >>> shift & 0xFF;
				final int to = next[i] >>> shift & 0xFF;
				pixel |= Math.round(from + (to - from) * progress) << shift;
			}
			mixed[i] = pixel;
		}
		return mixed;
	}

}