package dev.joid.backend.minecraft.demo.test.scenario;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import dev.joid.backend.minecraft.demo.test.ClientTestBounds;
import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestImages;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.backend.minecraft.demo.test.object.ClientTestBlock;
import dev.joid.backend.minecraft.demo.test.object.ClientTestEntity;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.IFont;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.CircleNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.test.contract.BitmapFontContractSuite;
import dev.joid.test.contract.BorrowedTextureContractSuite;
import dev.joid.test.contract.RenderBridgeContractSuite;
import dev.joid.test.contract.StateGuardContractSuite;
import dev.joid.test.snapshot.SnapshotDifference;
import dev.joid.test.snapshot.SnapshotImage;
import dev.joid.test.snapshot.SnapshotRunner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenderScenarios {

	public static void verifyFontWidths(final ClientTestContext context) {
		RenderScenarios.verifyWidths(context, Arrays.asList("The quick brown fox jumps over the lazy dog", "\u00A7lBold\u00A7r and regular", "\u00A7cRed \u00A7l\u00A7oBold italic\u00A7r back", "\u00A7kSecret \u00A7nunder", "Unicode \u00E9\u00E8 \u2605 \u65E5\u672C", "\u00A7x\u00A71\u00A72\u00A73\u00A74\u00A75\u00A76Hex", "  spaces  ", "\u00A7zUnknown", "Hello, JOID! 123", "\u00C9t\u00E9 \u00FCber \u00F1", "Enchanting table"));
	}

	public static void verifyFontTrailingCode(final ClientTestContext context) {
		RenderScenarios.verifyWidths(context, Arrays.asList("Trailing\u00A7"));
	}

	private static void verifyWidths(final ClientTestContext context, final List<String> texts) {
		final IClientTestPilot pilot = context.getPilot();
		final List<String> failures = context.compute(() -> {
			final List<String> found = new ArrayList<>();
			for (final IFont font : RenderScenarios.fonts()) {
				final TextInfo info = TextInfo.create(font, RenderScenarios.fontSize());
				for (final String text : texts) {
					final int expected = pilot.measureText(font, text);
					final double actual = info.getWidth(text);
					if (Math.ceil(actual) != expected) {
						found.add(font + " \"" + text + "\": " + actual + " instead of " + expected);
					}
				}
			}
			return found;
		});
		failures.forEach(failure -> context.fail("MinecraftFont measures " + failure + " like the game"));
	}

	public static void verifyBitmapFont(final ClientTestContext context) {
		final IFont font = (IFont) ClientTestDemos.constant(ClientTestDemos.FONT, "DEFAULT");
		final RectNode background = RectNode.create(0, 0, 1920, 1080).color(Color.BLACK);
		final TextNode text = TextNode.create(960, 540).text(Text.create("HH", TextInfo.create(font, RenderScenarios.fontSize() * 4).color(Color.WHITE)));
		context.openOver(ObjectScenarios.ui(background, text));
		for (final int[] scale : new int[][] {{1, 1}, {2, 2}, {0, 4}}) {
			context.guiScale(scale[0]);
			final int[] bounds = context.compute(() -> ClientTestBounds.of(background));
			RenderScenarios.expectCrisp(context, "the GUI scale " + scale[0], context.capture("joid-bitmap-font-gui-scale-" + scale[0]), bounds, scale[1]);
		}

		context.closeScreens();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.guiScale(1);
		context.waitTicks(40);
		context.capture("joid-demo-minecraft-gui-scale-1");
		context.guiScale(0);
		context.closeScreens();
	}

	public static void verifyDepth(final ClientTestContext context) {
		final List<Supplier<NodeEffect<Node>>> effects = Arrays.asList(null, () -> RoundedNodeEffect.create(24F), CircleNodeEffect::create, () -> BlurNodeEffect.create(2F));
		final List<String> names = Arrays.asList("joid-depth", "joid-depth-rounded", "joid-depth-circle", "joid-depth-blur");
		for (int index = 0; index < effects.size(); index++) {
			final ContainerNode root = ContainerNode.create(0, 0, 1920, 1080);
			context.openOver(ObjectScenarios.ui(root));
			RenderScenarios.verifyDepth(context, names.get(index), effects.get(index), index == 3 ? 12 : 2, root);
			context.closeScreens();
		}
	}

	public static void verifyDepth(final ClientTestContext context, final String name, final Supplier<NodeEffect<Node>> effect, final int inset, final Node parent) {
		final List<RectNode> covers = new ArrayList<>();
		final Node grid = context.compute(() -> RenderScenarios.depthGrid(context.getPilot(), covers, effect));
		context.run(() -> grid.attach(parent));
		context.waitTicks(40);
		final BufferedImage image = context.capture(name);
		final List<int[]> bounds = context.compute(() -> covers.stream().map(ClientTestBounds::of).collect(Collectors.toList()));
		final String[] labels = {"item", "enchanted item", "block", "translucent block", "entity", "player"};
		final String[] cases = {"under a node added after it at the same zindex", "under a node of a higher zindex", "over a node added before it at the same zindex", "over a node of a lower zindex"};
		for (int index = 0; index < bounds.size(); index++) {
			final int[] rect = bounds.get(index);
			int drawn = 0;
			for (int row = inset; row < rect[3] - inset; row++) {
				for (int column = inset; column < rect[2] - inset; column++) {
					if ((image.getRGB(rect[0] + column, rect[1] + row) & 0xFFFFFF) != 0xDDDDDD) {
						drawn++;
					}
				}
			}

			final String label = name + ": " + labels[index / 4] + " " + cases[index % 4];
			if (index % 4 < 2 && drawn > 0) {
				context.fail(label + " shows " + drawn + " pixels through the node");
			} else if (index % 4 >= 2 && drawn < 100) {
				context.fail(label + " is hidden: " + drawn + " pixels drawn");
			}
		}
		context.run(() -> parent.remove(grid));
		context.waitTicks(10);
	}

	public static void verifyNestedRaster(final ClientTestContext context) {
		RenderScenarios.verifyNestedRaster(context, false);
	}

	public static void verifyNestedFeatureRaster(final ClientTestContext context) {
		RenderScenarios.verifyNestedRaster(context, true);
	}

	public static void verifyContracts(final ClientTestContext context) {
		final List<Class<?>> tests = context.getPilot().getContractTests();
		for (final Class<?> suite : Arrays.asList(RenderBridgeContractSuite.class, StateGuardContractSuite.class, BorrowedTextureContractSuite.class, BitmapFontContractSuite.class)) {
			context.check(tests.stream().anyMatch(suite::isAssignableFrom), "no contract test of the version extends " + suite.getSimpleName());
		}

		final Result result = context.compute(() -> JUnitCore.runClasses(tests.toArray(new Class<?>[0])), 1200);
		System.out.println("[JOID] " + result.getRunCount() + " contract tests ran, " + result.getIgnoreCount() + " skipped");
		for (final Failure failure : result.getFailures()) {
			context.fail("contract " + failure.getTestHeader() + ": " + failure.getMessage());
		}
	}

	public static void verifySnapshots(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final File output = new File(System.getProperty("joid.snapshot.output", "snapshots/renders"));
		final File references = new File(new File(System.getProperty("joid.snapshot.references", "snapshots/references")), pilot.getRenderPipeline());
		final List<String> failures = context.compute(() -> {
			final List<String> found = new ArrayList<>();
			final SnapshotRunner runner = SnapshotRunner.start(pilot.createSnapshotBackend());
			try {
				final File folder = new File(references, runner.getRenderer());
				for (final String scenario : SnapshotRunner.getScenarios()) {
					final Map<String, SnapshotImage> shots;
					try {
						shots = runner.run(scenario);
					} catch (final IllegalStateException exception) {
						found.add(scenario + ": " + exception.getMessage());
						continue;
					}

					for (final Map.Entry<String, SnapshotImage> shot : shots.entrySet()) {
						final File reference = new File(folder, shot.getKey() + ".png");
						shot.getValue().write(new File(output, shot.getKey() + ".png"));
						if (!reference.exists()) {
							shot.getValue().write(reference);
							System.out.println("[JOID] Recorded the snapshot reference " + reference);
							continue;
						}

						final SnapshotDifference difference = shot.getValue().compare(SnapshotImage.read(reference), 1);
						if (difference.getPixels() > 0) {
							found.add(shot.getKey() + ": " + difference.getPixels() + " pixels differ from the reference, maximum channel delta " + difference.getMaximum());
						}
					}
				}
			} finally {
				runner.stop();
			}
			return found;
		}, 1800);
		failures.forEach(context::fail);
	}

	private static void verifyNestedRaster(final ClientTestContext context, final boolean feature) {
		final String name = feature ? "joid-nested-feature-raster" : "joid-nested-raster";
		final Node[] nodes = context.compute(() -> context.getPilot().createNestedRasterNodes(feature));
		context.openOver(ObjectScenarios.ui(nodes));
		final List<int[]> bounds = context.compute(() -> Arrays.asList(ClientTestBounds.of(nodes[0]), ClientTestBounds.of(nodes[1])));
		final BufferedImage image = context.capture(name);
		context.closeScreens();
		if (feature) {
			ClientTestImages.expectDrawn(context, "the stone around a nested item raster", image, bounds.get(0));
		}

		ClientTestImages.expectDrawn(context, "the stone drawn without nesting", image, bounds.get(1));
		final int width = Math.min(bounds.get(0)[2], bounds.get(1)[2]);
		final int height = Math.min(bounds.get(0)[3], bounds.get(1)[3]);
		final int different = ClientTestImages.compare(image, bounds.get(0), bounds.get(1), 16);
		context.check(different <= width * height / 200, "the stone drawn " + (feature ? "around a nested item raster" : "after a nested DrawItem") + " differs from the same drawing without nesting on " + different + " pixels");
	}

	private static Node depthGrid(final IClientTestPilot pilot, final List<RectNode> covers, final Supplier<NodeEffect<Node>> effect) {
		final Color cover = new Color(221, 221, 221);
		final double size = effect == null ? 160D : 100D;
		final double margin = effect == null ? 0D : 25D;
		final Node grid = ContainerNode.create(0, 0, 1920, 1080);
		RectNode.create(0, 0, 1920, 1080).color(Color.BLACK).zindex(-10).attach(grid);
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 4; column++) {
				final double x = 300D + column * 300D;
				final double y = 40D + row * 170D;
				Node parent = grid;
				if (effect != null) {
					parent = ContainerNode.create(x - margin, y - margin, size + margin * 2D, size + margin * 2D).effect(effect.get().scope(NodeEffectScope.CHILDREN)).attach(grid);
				}

				final double left = effect == null ? x : margin;
				final double top = effect == null ? y : margin;
				final RectNode rect = RectNode.create(left, top, size, size).color(cover).zindex(column == 1 ? 1 : column == 3 ? -1 : 0);
				if (column > 0) {
					rect.attach(parent);
				}

				RenderScenarios.depthNode(pilot, row, left, top, size).attach(parent);
				if (column == 0) {
					rect.attach(parent);
				}
				covers.add(rect);
			}
		}
		return grid;
	}

	private static Node depthNode(final IClientTestPilot pilot, final int row, final double x, final double y, final double size) {
		switch (row) {
			case 0:
				return pilot.createItemNode(x, y, size, ClientTestItem.DIAMOND, false);
			case 1:
				return pilot.createItemNode(x, y, size, ClientTestItem.DIAMOND_SWORD, true);
			case 2:
				return pilot.createBlockNode(x, y, size, ClientTestBlock.STONE);
			case 3:
				return pilot.createBlockNode(x, y, size, ClientTestBlock.ICE);
			case 4:
				return pilot.createEntityNode(x, y, size, size, ClientTestEntity.ZOMBIE, 0D, 0D);
			default:
				return pilot.createEntityNode(x, y, size, size, ClientTestEntity.PLAYER_PROFILE, 0D, 0D);
		}
	}

	private static void expectCrisp(final ClientTestContext context, final String label, final BufferedImage image, final int[] bounds, final int texel) {
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		int blended = 0;
		for (int row = 0; row < bounds[3]; row++) {
			for (int column = 0; column < bounds[2]; column++) {
				final int color = image.getRGB(bounds[0] + column, bounds[1] + row) & 0xFFFFFF;
				if (color == 0xFFFFFF) {
					left = Math.min(left, column);
					top = Math.min(top, row);
					right = Math.max(right, column);
					bottom = Math.max(bottom, row);
				} else if (color != 0) {
					blended++;
				}
			}
		}

		final int width = right - left + 1;
		final int height = bottom - top + 1;
		context.check(blended == 0 && width == 11 * texel && height == 7 * texel, label + " draws \"HH\" " + width + "x" + height + " with " + blended + " blended pixels instead of " + 11 * texel + "x" + 7 * texel + " at " + texel + " pixels per texel");
	}

	private static float fontSize() {
		return (float) ClientTestDemos.constant(ClientTestDemos.FONT, "SIZE");
	}

	private static List<IFont> fonts() {
		final List<IFont> fonts = new ArrayList<>();
		for (final Field field : ClientTestDemos.load(ClientTestDemos.FONT).getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && IFont.class.isAssignableFrom(field.getType())) {
				fonts.add((IFont) ClientTestDemos.constant(ClientTestDemos.FONT, field.getName()));
			}
		}
		return fonts;
	}

}