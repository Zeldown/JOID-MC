package dev.joid.impl.minecraft.fabric.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Vector2d;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.lwjgl.glfw.GLFW;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.font.UIDemoFont;
import dev.joid.demo.ui.shader.UIDemoEffect;
import dev.joid.demo.ui.shader.UIDemoShader;
import dev.joid.impl.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.impl.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.impl.minecraft.snapshot.SnapshotBackend;
import dev.joid.impl.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.impl.minecraft.ui.screen.UIScreen;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.ui.core.UI;
import dev.joid.test.snapshot.SnapshotDifference;
import dev.joid.test.snapshot.SnapshotImage;
import dev.joid.test.snapshot.SnapshotRunner;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;

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