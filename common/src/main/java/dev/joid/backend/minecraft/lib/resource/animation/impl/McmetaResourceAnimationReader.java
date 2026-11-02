package dev.joid.backend.minecraft.lib.resource.animation.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.mojang.datafixers.util.Pair;

import dev.joid.lib.resource.animation.IResourceAnimationReader;
import dev.joid.lib.resource.animation.ResourceAnimation;
import dev.joid.lib.resource.animation.ResourceAnimationFrame;
import dev.joid.lib.utils.image.ImageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;

@RequiredArgsConstructor
public final class McmetaResourceAnimationReader implements IResourceAnimationReader {

	private final AnimationMetadataSection metadata;

	@Override
	public @NonNull ResourceAnimation read(final @NonNull InputStream stream) throws IOException {
		final BufferedImage image = ImageIO.read(stream);
		if (image == null) {
			throw new IOException("The animated texture is not a readable image");
		}

		final Pair<Integer, Integer> size = this.metadata.getFrameSize(image.getWidth(), image.getHeight());
		final int width = size.getFirst();
		final int height = size.getSecond();
		final int columns = image.getWidth() / width;
		final int count = columns * (image.getHeight() / height);
		final List<AnimationFrame> frames = new ArrayList<>();
		this.metadata.forEachFrame((index, time) -> frames.add(new AnimationFrame(index, time)));
		if (frames.isEmpty()) {
			frames.addAll(McmetaResourceAnimationReader.sequence(count));
		}

		frames.removeIf(frame -> frame.getIndex() >= count);

		if (frames.isEmpty()) {
			throw new IOException("The animated texture has no frame inside its image");
		}

		final List<int[]> pixels = new ArrayList<>();
		for (final AnimationFrame frame : frames) {
			final int[] framePixels = new int[width * height];
			image.getRGB(frame.getIndex() % columns * width, frame.getIndex() / columns * height, width, height, framePixels, 0, width);
			pixels.add(framePixels);
		}

		final List<ResourceAnimationFrame> animation = new ArrayList<>();
		for (int i = 0; i < frames.size(); i++) {
			final int time = frames.get(i).getTime(this.metadata.getDefaultFrameTime());
			if (!this.metadata.isInterpolatedFrames()) {
				animation.add(McmetaResourceAnimationReader.frame(pixels.get(i), width, height, time));
				continue;
			}

			for (int tick = 0; tick < time; tick++) {
				animation.add(McmetaResourceAnimationReader.frame(McmetaResourceAnimationReader.mix(pixels.get(i), pixels.get((i + 1) % frames.size()), (float) tick / time), width, height, 1));
			}
		}

		return ResourceAnimation.of(width, height, 0, animation);
	}

	private static @NonNull List<AnimationFrame> sequence(final int count) {
		final List<AnimationFrame> frames = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			frames.add(new AnimationFrame(i));
		}
		return frames;
	}

	private static @NonNull ResourceAnimationFrame frame(final int[] pixels, final int width, final int height, final int ticks) {
		final int[] copy = pixels.clone();
		ImageUtils.bleedAlpha(copy, width, height);
		return ResourceAnimationFrame.of(copy, ticks * 50L);
	}

	private static @NonNull int[] mix(final int[] current, final int[] next, final float progress) {
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