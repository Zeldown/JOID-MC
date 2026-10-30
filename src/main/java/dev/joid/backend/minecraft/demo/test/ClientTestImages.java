package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientTestImages {

	public static void expectColor(final ClientTestContext context, final String label, final BufferedImage image, final int[] bounds, final int color, final boolean same) {
		final int actual = image.getRGB(bounds[0] + 4, bounds[1] + 4) & 0xFFFFFF;
		if (actual == color != same) {
			context.fail(label + " at " + Arrays.toString(bounds) + " is " + Integer.toHexString(actual) + (same ? " instead of " : " like ") + Integer.toHexString(color));
		}
	}

	public static void expectChange(final ClientTestContext context, final String label, final List<BufferedImage> captures, final int[] bounds, final boolean same) {
		boolean changed = false;
		for (int capture = 1; capture < captures.size() && !changed; capture++) {
			for (int row = 0; row < bounds[3] && !changed; row++) {
				for (int column = 0; column < bounds[2] && !changed; column++) {
					changed = captures.get(capture - 1).getRGB(bounds[0] + column, bounds[1] + row) != captures.get(capture).getRGB(bounds[0] + column, bounds[1] + row);
				}
			}
		}

		if (changed == same) {
			context.fail(label + (same ? " changed" : " did not change") + " between the captures");
		}
	}

	public static void expectDrawn(final ClientTestContext context, final String label, final BufferedImage image, final int[] bounds) {
		final int background = image.getRGB(bounds[0], bounds[1]);
		int drawn = 0;
		for (int row = 0; row < bounds[3]; row++) {
			for (int column = 0; column < bounds[2]; column++) {
				if (image.getRGB(bounds[0] + column, bounds[1] + row) != background) {
					drawn++;
				}
			}
		}

		if (drawn < bounds[2] * bounds[3] / 10) {
			context.fail(label + " at " + Arrays.toString(bounds) + " is almost empty: " + drawn + " drawn pixels");
		}
	}

	public static void expectTexture(final ClientTestContext context, final String label, final BufferedImage image, final int x, final int y, final BufferedImage texture) {
		int different = 0;
		for (int row = 0; row < 64; row++) {
			for (int column = 0; column < 64; column++) {
				final int expected = texture.getRGB(column * texture.getWidth() / 64, row * texture.getHeight() / 64);
				if (expected >>> 24 == 255 && ClientTestImages.delta(expected, image.getRGB(x + column, y + row)) > 1) {
					different++;
				}
			}
		}

		if (different > 0) {
			context.fail(label + ": " + different + " opaque pixels differ from the texture of the game");
		}
	}

	public static int countDifferent(final BufferedImage image, final int[] bounds, final int background) {
		int count = 0;
		for (int y = Math.max(0, bounds[1]); y < Math.min(image.getHeight(), bounds[1] + bounds[3]); y++) {
			for (int x = Math.max(0, bounds[0]); x < Math.min(image.getWidth(), bounds[0] + bounds[2]); x++) {
				if ((image.getRGB(x, y) & 0xFFFFFF) != background) {
					count++;
				}
			}
		}
		return count;
	}

	public static int compare(final BufferedImage image, final int[] first, final int[] second, final int tolerance) {
		final int width = Math.min(first[2], second[2]);
		final int height = Math.min(first[3], second[3]);
		int different = 0;
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (ClientTestImages.delta(image.getRGB(second[0] + column, second[1] + row), image.getRGB(first[0] + column, first[1] + row)) > tolerance) {
					different++;
				}
			}
		}
		return different;
	}

	public static double difference(final BufferedImage first, final BufferedImage second) {
		long difference = 0L;
		for (int y = 0; y < first.getHeight(); y++) {
			for (int x = 0; x < first.getWidth(); x++) {
				for (int shift = 0; shift < 24; shift += 8) {
					difference += Math.abs((first.getRGB(x, y) >> shift & 0xFF) - (second.getRGB(x, y) >> shift & 0xFF));
				}
			}
		}
		return difference / (first.getWidth() * first.getHeight() * 3D);
	}

	public static double edges(final BufferedImage image, final int[] bounds) {
		long edges = 0L;
		for (int y = bounds[1]; y < bounds[1] + bounds[3]; y++) {
			for (int x = bounds[0]; x < bounds[0] + bounds[2] - 1; x++) {
				for (int shift = 0; shift < 24; shift += 8) {
					edges += Math.abs((image.getRGB(x, y) >> shift & 0xFF) - (image.getRGB(x + 1, y) >> shift & 0xFF));
				}
			}
		}
		return edges / ((bounds[2] - 1) * bounds[3] * 3D);
	}

	public static int delta(final int expected, final int actual) {
		int delta = 0;
		for (int shift = 0; shift < 24; shift += 8) {
			delta = Math.max(delta, Math.abs((expected >> shift & 0xFF) - (actual >> shift & 0xFF)));
		}
		return delta;
	}

	public static int[] faces(final BufferedImage image, final int[] bounds) {
		final double[][] points = {{0.5D, 0.25D}, {0.28D, 0.62D}, {0.72D, 0.62D}};
		final int[] faces = new int[points.length];
		for (int face = 0; face < points.length; face++) {
			final int x = (int) (bounds[0] + bounds[2] * points[face][0]);
			final int y = (int) (bounds[1] + bounds[3] * points[face][1]);
			final int radius = Math.max(1, bounds[2] / 16);
			int sum = 0;
			for (int row = -radius; row <= radius; row++) {
				for (int column = -radius; column <= radius; column++) {
					sum += image.getRGB(x + column, y + row) >> 8 & 0xFF;
				}
			}
			faces[face] = sum / ((2 * radius + 1) * (2 * radius + 1));
		}
		return faces;
	}

}