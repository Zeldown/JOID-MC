package dev.joid.impl.minecraft.fabric.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.joml.Vector2d;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.font.UIDemoFont;
import dev.joid.demo.ui.shader.UIDemoEffect;
import dev.joid.demo.ui.shader.UIDemoShader;
import dev.joid.impl.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.impl.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.impl.minecraft.render.texture.Texture;
import dev.joid.impl.minecraft.snapshot.SnapshotBackend;
import dev.joid.impl.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.impl.minecraft.ui.screen.UIScreen;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.UI;
import dev.joid.test.snapshot.SnapshotDifference;
import dev.joid.test.snapshot.SnapshotImage;
import dev.joid.test.snapshot.SnapshotRunner;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public final class FabricJoidClientGameTest implements FabricClientGameTest {

	@Override
	public void runTest(final ClientGameTestContext context) {
		context.waitFor(_ -> BridgeHandler.UI.getBridge(ScreenUIBridge.class) != null);
		context.getInput().resizeWindow(1920, 1080);
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			singleplayer.getConnection().waitForChunksRender();
			context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
			context.waitForScreen(PauseScreen.class);
			context.waitTicks(5);
			context.takeScreenshot("joid-pause-menu");
			FabricJoidClientGameTest.click(context, "JOID");
			context.waitForScreen(UIScreen.class);
			context.waitTicks(40);
			context.takeScreenshot("joid-demo-choice");
			FabricJoidClientGameTest.guiScale(context, 2, 0.5D, "joid-demo-choice-gui-scale-2");
			FabricJoidClientGameTest.guiScale(context, 0, 1D, "joid-demo-choice-gui-scale-auto");

			FabricJoidClientGameTest.screenshot(context, new UIDemoFont(), "joid-demo-font");
			FabricJoidClientGameTest.screenshot(context, new UIDemoShader(), "joid-demo-shader");
			FabricJoidClientGameTest.screenshot(context, new UIDemoEffect(), "joid-demo-effect");
			FabricJoidClientGameTest.screenshot(context, new UIDemoMinecraft(), "joid-demo-minecraft");
			FabricJoidClientGameTest.verifyResources(context);
			FabricJoidClientGameTest.verifyReload(context);
			context.runOnClient(FabricJoidClientGameTest::verifyWidths);
			context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
			context.waitTicks(20);
			context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
			context.waitForScreen(null);
		}

		context.runOnClient(_ -> FabricJoidClientGameTest.verify(JUnitCore.runClasses(FabricRenderBridgeContractTest.class)));
		context.runOnClient(_ -> FabricJoidClientGameTest.verifySnapshots(new File(System.getProperty("joid.snapshot.output", "snapshots/renders")), new File(System.getProperty("joid.snapshot.references", "snapshots/references"))));
	}

	private static void click(final ClientGameTestContext context, final String label) {
		final Vector2d center = context.computeOnClient(minecraft -> FabricJoidClientGameTest.center(minecraft, label));
		context.getInput().setCursorPos(center.x, center.y);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.getInput().setCursorPos(960D, 540D);
	}

	private static Vector2d center(final Minecraft minecraft, final String label) {
		for (final GuiEventListener child : minecraft.gui.screen().children()) {
			if (child instanceof final Button button && button.getMessage().getString().equals(label)) {
				final int guiScale = minecraft.getWindow().getGuiScale();
				return new Vector2d((button.getX() + button.getWidth() / 2D) * guiScale, (button.getY() + button.getHeight() / 2D) * guiScale);
			}
		}

		throw new AssertionError("The screen " + minecraft.gui.screen().getClass().getName() + " has no button " + label);
	}

	private static void screenshot(final ClientGameTestContext context, final UI ui, final String name) {
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		context.runOnClient(_ -> JOID.open(ui));
		context.waitTicks(40);
		context.takeScreenshot(name);
	}

	private static void guiScale(final ClientGameTestContext context, final int guiScale, final double interfaceScale, final String name) {
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(guiScale);
			minecraft.resizeGui();
		});
		context.waitTicks(5);
		context.takeScreenshot(name);
		context.runOnClient(_ -> {
			final double actual = JOID.getUI(UIDemoChoice.class).getView().getInterfaceScale();
			if (actual != interfaceScale) {
				throw new AssertionError("The GUI scale " + guiScale + " gives the interface scale " + actual + " instead of " + interfaceScale);
			}
		});
	}

	private static void verifyResources(final ClientGameTestContext context) {
		final List<BufferedImage> captures = new ArrayList<>();
		for (final String name : List.of("a", "b", "c")) {
			captures.add(FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-resources-" + name)));
			context.waitTicks(captures.size() * 2 + 1);
		}

		final List<String> failures = new ArrayList<>();
		FabricJoidClientGameTest.expectChange(failures, "animated sprite fire_0", captures, 1040, 736, 128, false);
		FabricJoidClientGameTest.expectChange(failures, "interpolated sprite sea_lantern", captures, 1240, 736, 128, false);
		FabricJoidClientGameTest.expectChange(failures, "decoded .mcmeta sea_lantern", captures, 1520, 736, 128, false);
		FabricJoidClientGameTest.expectChange(failures, "decoded .mcmeta pulse", captures, 1712, 736, 128, false);
		FabricJoidClientGameTest.expectChange(failures, "static sprite diamond", captures, 560, 720, 64, true);
		FabricJoidClientGameTest.expectTexture(context, failures, "sprite item/diamond", captures.get(0), 560, 720, "textures/item/diamond.png");
		FabricJoidClientGameTest.expectTexture(context, failures, "decoded block/diamond_block", captures.get(0), 80, 820, "textures/block/diamond_block.png");
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " resource checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyReload(final ClientGameTestContext context) {
		final boolean openGl = context.computeOnClient(_ -> RenderSystem.getDevice().getDeviceInfo().backendName().equals("OpenGL"));
		FabricJoidClientGameTest.reload(context);
		final ITexture decoded = context.computeOnClient(_ -> Resource.of("minecraft:textures/block/sea_lantern.png").getTexture());
		final int textures = openGl ? context.computeOnClient(_ -> FabricJoidClientGameTest.countTextures()) : 0;
		FabricJoidClientGameTest.reload(context);
		FabricJoidClientGameTest.reload(context);
		final int reloaded = openGl ? context.computeOnClient(_ -> FabricJoidClientGameTest.countTextures()) : 0;
		final List<String> failures = new ArrayList<>();
		if (!(decoded instanceof final Texture texture) || !texture.isDeleted()) {
			failures.add("the texture decoded before the reloads was not deleted: " + decoded);
		}

		if (reloaded != textures) {
			failures.add("the reloads changed the OpenGL texture count from " + textures + " to " + reloaded);
		}

		final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-reloaded"));
		FabricJoidClientGameTest.expectTexture(context, failures, "sprite item/diamond after the reloads", image, 560, 720, "textures/item/diamond.png");
		FabricJoidClientGameTest.expectTexture(context, failures, "decoded block/diamond_block after the reloads", image, 80, 820, "textures/block/diamond_block.png");
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " reload checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}

		System.out.println("[JOID] " + (openGl ? textures + " OpenGL textures before and after the reloads" : "Texture count skipped outside OpenGL"));
	}

	private static void reload(final ClientGameTestContext context) {
		final CompletableFuture<Void> future = context.computeOnClient(Minecraft::reloadResourcePacks);
		context.waitFor(_ -> future.isDone(), 2400);
		context.waitTicks(40);
	}

	private static int countTextures() {
		int count = 0;
		for (int name = 1; name < 65536; name++) {
			if (GL11.glIsTexture(name)) {
				count++;
			}
		}
		return count;
	}

	private static void expectChange(final List<String> failures, final String label, final List<BufferedImage> captures, final int x, final int y, final int size, final boolean same) {
		boolean changed = false;
		for (int capture = 1; capture < captures.size(); capture++) {
			for (int row = 0; row < size && !changed; row++) {
				for (int column = 0; column < size && !changed; column++) {
					changed = captures.get(capture - 1).getRGB(x + column, y + row) != captures.get(capture).getRGB(x + column, y + row);
				}
			}
		}

		if (changed == same) {
			failures.add(label + (same ? " changed" : " did not change") + " between the captures");
		}
	}

	private static void expectTexture(final ClientGameTestContext context, final List<String> failures, final String label, final BufferedImage image, final int x, final int y, final String path) {
		final BufferedImage texture = context.computeOnClient(minecraft -> FabricJoidClientGameTest.texture(minecraft, path));
		int different = 0;
		for (int row = 0; row < 64; row++) {
			for (int column = 0; column < 64; column++) {
				final int expected = texture.getRGB(column * texture.getWidth() / 64, row * texture.getHeight() / 64);
				if (expected >>> 24 == 255 && FabricJoidClientGameTest.delta(expected, image.getRGB(x + column, y + row)) > 1) {
					different++;
				}
			}
		}

		if (different > 0) {
			failures.add(label + ": " + different + " opaque pixels differ from " + path);
		}
	}

	private static int delta(final int expected, final int actual) {
		int delta = 0;
		for (int shift = 0; shift < 24; shift += 8) {
			delta = Math.max(delta, Math.abs((expected >> shift & 0xFF) - (actual >> shift & 0xFF)));
		}
		return delta;
	}

	private static BufferedImage texture(final Minecraft minecraft, final String path) {
		try (InputStream stream = minecraft.getResourceManager().getResource(Identifier.withDefaultNamespace(path)).orElseThrow().open()) {
			return ImageIO.read(stream);
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the texture " + path, exception);
		}
	}

	private static BufferedImage read(final Path path) {
		try {
			return ImageIO.read(path.toFile());
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + path, exception);
		}
	}

	private static void verifyWidths(final Minecraft minecraft) {
		final List<String> texts = List.of("The quick brown fox jumps over the lazy dog", "§lBold§r and regular", "§cRed §l§oBold italic§r back", "§kSecret §nunder", "Unicode éè ★ 日本", "Trailing§", "§x§1§2§3§4§5§6Hex", "  spaces  ", "§zUnknown");
		final List<String> failures = new ArrayList<>();
		for (final MinecraftFont font : List.of(MinecraftFont.DEFAULT, MinecraftFont.ALT, MinecraftFont.ILLAGER, MinecraftFont.UNIFORM)) {
			final TextInfo info = TextInfo.create(font, MinecraftFont.SIZE);
			for (final String text : texts) {
				final int expected = minecraft.font.width(Component.literal(text).withStyle(style -> style.withFont(new FontDescription.Resource(font.getIdentifier()))));
				final double actual = info.getWidth(text);
				if (Math.ceil(actual) != expected) {
					failures.add(font + " \"" + text + "\": " + actual + " instead of " + expected);
				}
			}
		}

		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " MinecraftFont widths differ from Font.width:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verify(final Result result) {
		if (!result.wasSuccessful()) {
			throw new AssertionError(result.getFailureCount() + " RenderBridge contract failures:" + System.lineSeparator() + result.getFailures().stream().map(failure -> failure.getTestHeader() + ": " + failure.getMessage()).collect(Collectors.joining(System.lineSeparator())));
		}
	}

	private static void verifySnapshots(final File output, final File references) {
		final List<String> failures = new ArrayList<>();
		final SnapshotRunner runner = SnapshotRunner.start(new SnapshotBackend());
		try {
			final File rendererReferences = new File(references, runner.getRenderer());
			for (final String scenario : SnapshotRunner.getScenarios()) {
				for (final Map.Entry<String, SnapshotImage> shot : runner.run(scenario).entrySet()) {
					final File reference = new File(rendererReferences, shot.getKey() + ".png");
					shot.getValue().write(new File(output, shot.getKey() + ".png"));
					if (!reference.exists()) {
						shot.getValue().write(reference);
						System.out.println("[JOID] Recorded the snapshot reference " + reference);
						continue;
					}

					final SnapshotDifference difference = shot.getValue().compare(SnapshotImage.read(reference), 1);
					if (difference.getPixels() > 0) {
						failures.add(shot.getKey() + ": " + difference.getPixels() + " pixels differ from the reference, maximum channel delta " + difference.getMaximum());
					}
				}
			}
		} finally {
			runner.stop();
		}

		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " snapshots differ from their reference:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

}