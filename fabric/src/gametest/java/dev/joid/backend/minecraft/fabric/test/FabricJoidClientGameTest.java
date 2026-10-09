package dev.joid.backend.minecraft.fabric.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.joml.Vector2d;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlay;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.backend.minecraft.render.texture.Texture;
import dev.joid.backend.minecraft.snapshot.SnapshotBackend;
import dev.joid.backend.minecraft.ui.bridge.OverlayUIBridge;
import dev.joid.backend.minecraft.ui.bridge.ScreenUIBridge;
import dev.joid.backend.minecraft.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.ui.screen.UIScreen;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.font.UIDemoFont;
import dev.joid.demo.ui.resource.UIDemoPlayer;
import dev.joid.demo.ui.shader.UIDemoEffect;
import dev.joid.demo.ui.shader.UIDemoShader;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.dto.decoder.impl.VideoResourceDecoder;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.design.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.design.resource.ResourcePlayerNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.test.snapshot.SnapshotDifference;
import dev.joid.test.snapshot.SnapshotImage;
import dev.joid.test.snapshot.SnapshotRunner;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class FabricJoidClientGameTest implements FabricClientGameTest {

	@Override
	public void runTest(final ClientGameTestContext context) {
		context.waitFor(_ -> BridgeHandler.UI.getBridge(ScreenUIBridge.class) != null);
		context.getInput().resizeWindow(1920, 1080);
		FabricJoidClientGameTest.verifyEntitiesWithoutWorld(context);
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
			FabricJoidClientGameTest.clickDemo(context, UIDemoMinecraft.class, "joid-demo-choice-minecraft");

			FabricJoidClientGameTest.screenshot(context, new UIDemoFont(), "joid-demo-font");
			FabricJoidClientGameTest.screenshot(context, new UIDemoShader(), "joid-demo-shader");
			FabricJoidClientGameTest.screenshot(context, new UIDemoEffect(), "joid-demo-effect");
			FabricJoidClientGameTest.screenshot(context, new UIDemoMinecraft(), "joid-demo-minecraft");
			FabricJoidClientGameTest.verifyResources(context);
			FabricJoidClientGameTest.verifyReload(context);
			FabricJoidClientGameTest.verifyItems(context);
			FabricJoidClientGameTest.verifyEntities(context);
			FabricJoidClientGameTest.verifySound(context);
			FabricJoidClientGameTest.verifyScale(context);
			FabricJoidClientGameTest.verifyAudio(context);
			context.runOnClient(FabricJoidClientGameTest::verifyWidths);
			FabricJoidClientGameTest.verifyBitmapFont(context);
			context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
			context.waitTicks(20);
			context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
			context.waitForScreen(null);
			FabricJoidClientGameTest.verifyOverlays(context);
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

	private static void click(final ClientGameTestContext context, final int[] bounds) {
		context.getInput().setCursorPos(bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		context.getInput().setCursorPos(960D, 540D);
	}

	private static void clickToggle(final ClientGameTestContext context, final int fromEnd) {
		final List<int[]> rects = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(RectNode.class));
		FabricJoidClientGameTest.click(context, rects.get(rects.size() - fromEnd));
	}

	private static void clickDemo(final ClientGameTestContext context, final Class<? extends UI> clazz, final String name) {
		final Vector2d center = context.computeOnClient(_ -> FabricJoidClientGameTest.center(clazz));
		context.getInput().setCursorPos(center.x, center.y);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.getInput().setCursorPos(960D, 540D);
		context.waitTicks(40);
		context.takeScreenshot(name);
		context.runOnClient(_ -> {
			if (JOID.getUI(clazz) == null) {
				throw new AssertionError("Clicking the entry " + clazz.getSimpleName() + " of UIDemoChoice did not open it");
			}
		});
	}

	private static Vector2d center(final Class<? extends UI> clazz) {
		final int index = new ArrayList<>(UIDemoChoice.LIST).indexOf(clazz);
		if (index < 0) {
			throw new AssertionError("UIDemoChoice does not list " + clazz.getName());
		}

		final UI ui = JOID.getUI(UIDemoChoice.class);
		final Node entry = ui.getNodeList().ordered().getFirst().getChild(index, RectNode.class);
		return new Vector2d(ui.getView().toScreenX(entry.getAbsoluteX() + entry.getWidth() / 2D), ui.getView().toScreenY(entry.getAbsoluteY() + entry.getHeight() / 2D));
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

	private static void verifyBitmapFont(final ClientGameTestContext context) {
		final RectNode background = RectNode.create(0, 0, 1920, 1080).color(Color.BLACK);
		final TextNode text = TextNode.create(960, 540).text(Text.create("HH", TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3, Color.WHITE)));
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		context.runOnClient(_ -> JOID.open(new UI() {

			@Override
			public void init() {
				background.attach(this);
				text.attach(this);
			}

		}));
		context.waitTicks(20);
		final List<String> failures = new ArrayList<>();
		for (final int[] scale : new int[][] {{1, 1}, {2, 2}, {0, 3}}) {
			context.runOnClient(minecraft -> {
				minecraft.options.guiScale().set(scale[0]);
				minecraft.resizeGui();
			});
			context.waitTicks(5);
			final int[] bounds = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(background));
			final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot("joid-bitmap-font-gui-scale-" + scale[0]));
			FabricJoidClientGameTest.expectCrisp(failures, "the GUI scale " + scale[0], image, bounds, scale[1]);
		}

		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		context.runOnClient(_ -> JOID.open(new UIDemoMinecraft()));
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(1);
			minecraft.resizeGui();
		});
		context.waitTicks(40);
		context.takeScreenshot("joid-demo-minecraft-gui-scale-1");
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(0);
			minecraft.resizeGui();
		});
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " bitmap font checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void expectCrisp(final List<String> failures, final String label, final BufferedImage image, final int[] bounds, final int texel) {
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
		if (blended > 0 || width != 11 * texel || height != 7 * texel) {
			failures.add(label + " draws \"HH\" " + width + "x" + height + " with " + blended + " blended pixels instead of " + 11 * texel + "x" + 7 * texel + " at " + texel + " pixels per texel");
		}
	}

	private static void verifyOverlays(final ClientGameTestContext context) {
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitForScreen(UIScreen.class);
		context.waitTicks(20);
		FabricJoidClientGameTest.clickDemo(context, UIDemoOverlay.class, "joid-demo-choice-overlay");
		for (int index = 0; index < 3; index++) {
			final int card = index;
			final int[] toggle = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUI(UIDemoOverlay.class).getNodeList().ordered().get(card).getChild(1, RectNode.class)));
			FabricJoidClientGameTest.click(context, toggle);
		}

		context.takeScreenshot("joid-demo-overlay");
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitFor(_ -> JOID.getUI(UIDemoChoice.class) != null && JOID.getUI(UIDemoOverlay.class) == null);
		context.waitTicks(20);
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.waitTicks(10);

		final List<String> failures = new ArrayList<>();
		final int[] hotbar = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUI(UIDemoOverlay.Hotbar.class).getNodeList().ordered().getFirst()));
		final int[] experience = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUI(UIDemoOverlay.Experience.class).getNodeList().ordered().getFirst()));
		final int[] interactive = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUI(UIDemoOverlay.Interactive.class).getNodeList().ordered().getFirst()));
		final int[] button = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUI(UIDemoOverlay.Interactive.class).getNodeList().ordered().getFirst().getChild(0, RectNode.class)));
		final int[] vanillaHotbar = context.computeOnClient(minecraft -> {
			final int guiScale = minecraft.getWindow().getGuiScale();
			return new int[] {(minecraft.getWindow().getGuiScaledWidth() / 2 - 91) * guiScale, (minecraft.getWindow().getGuiScaledHeight() - 22) * guiScale, 182 * guiScale, 22 * guiScale};
		});
		final BufferedImage hud = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-hud"));
		FabricJoidClientGameTest.expectColor(failures, "overlay above the hotbar", hud, hotbar, 0xDDDDDD, true);
		FabricJoidClientGameTest.expectColor(failures, "overlay replacing the experience bar", hud, new int[] {experience[0] + experience[2] - 8, experience[1], 8, experience[3]}, 0xDDDDDD, true);
		FabricJoidClientGameTest.expectColor(failures, "interactive overlay in game", hud, interactive, 0xDDDDDD, true);
		if (!context.computeOnClient(_ -> OverlayLayerRenderer.isCancelled(OverlayLayer.CONTEXTUAL_BAR))) {
			failures.add("the overlay replacing the experience bar does not cancel the contextual bar");
		}

		context.runOnClient(_ -> JOID.open(new HiddenHotbar()));
		context.waitTicks(5);
		final BufferedImage hidden = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-hotbar-hidden"));
		FabricJoidClientGameTest.expectChange(failures, "vanilla hotbar cancelled by an overlay", List.of(hud, hidden), vanillaHotbar, false);
		context.runOnClient(_ -> JOID.close(JOID.getUI(HiddenHotbar.class)));
		context.waitTicks(5);
		final BufferedImage restored = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-hotbar-restored"));
		FabricJoidClientGameTest.expectChange(failures, "vanilla hotbar after closing the cancelling overlay", List.of(hidden, restored), vanillaHotbar, false);

		context.getInput().pressKey(GLFW.GLFW_KEY_F1);
		context.waitTicks(5);
		final BufferedImage hiddenGui = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-hidden-gui"));
		FabricJoidClientGameTest.expectColor(failures, "overlay above the hotbar with the GUI hidden", hiddenGui, hotbar, 0xDDDDDD, false);
		FabricJoidClientGameTest.expectColor(failures, "interactive overlay with the GUI hidden", hiddenGui, interactive, 0xDDDDDD, false);
		if (!context.computeOnClient(_ -> BridgeHandler.UI.getBridge(OverlayUIBridge.class).isOverlayHidden())) {
			failures.add("F1 does not hide the overlays");
		}

		context.getInput().pressKey(GLFW.GLFW_KEY_F1);
		context.waitTicks(5);
		context.getInput().pressKey(GLFW.GLFW_KEY_E);
		context.waitFor(minecraft -> minecraft.gui.screen() instanceof AbstractContainerScreen);
		context.waitTicks(10);
		final int[] vanillaClicks = new int[1];
		context.runOnClient(minecraft -> ScreenMouseEvents.beforeMouseClick(minecraft.gui.screen()).register((_, _) -> vanillaClicks[0]++));
		final BufferedImage inventory = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-inventory"));
		FabricJoidClientGameTest.expectColor(failures, "interactive overlay over the inventory", inventory, interactive, 0xDDDDDD, true);
		FabricJoidClientGameTest.expectColor(failures, "overlay above the hotbar over the inventory", inventory, hotbar, 0xDDDDDD, false);
		context.getInput().setCursorPos(button[0] + button[2] / 2D, button[1] + button[3] / 2D);
		context.waitTicks(5);
		context.takeScreenshot("joid-overlay-inventory-hover");
		if (context.computeOnClient(minecraft -> FabricJoidClientGameTest.field(minecraft.getWindow(), "currentCursor")) != CursorTypes.POINTING_HAND) {
			failures.add("hovering the interactive overlay does not show the pointing hand cursor");
		}

		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		context.getInput().setCursorPos(960D, 40D);
		context.waitTicks(5);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		final int clicks = context.computeOnClient(_ -> (int) FabricJoidClientGameTest.field(JOID.getUI(UIDemoOverlay.Interactive.class), "clicks"));
		if (clicks != 1 || vanillaClicks[0] != 1) {
			failures.add("a click on the interactive overlay then one beside it give " + clicks + " overlay clicks and " + vanillaClicks[0] + " inventory clicks instead of 1 and 1");
		}

		context.takeScreenshot("joid-overlay-inventory-clicked");
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(2);
			minecraft.resizeGui();
		});
		context.waitTicks(10);
		context.takeScreenshot("joid-overlay-gui-scale-2");
		final double interfaceScale = context.computeOnClient(_ -> JOID.getUI(UIDemoOverlay.Hotbar.class).getView().getInterfaceScale());
		if (interfaceScale != 0.5D) {
			failures.add("the GUI scale 2 gives the overlays the interface scale " + interfaceScale + " instead of 0.5");
		}

		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(0);
			minecraft.resizeGui();
			JOID.close(JOID.getUI(UIDemoOverlay.Hotbar.class));
			JOID.close(JOID.getUI(UIDemoOverlay.Experience.class));
			JOID.close(JOID.getUI(UIDemoOverlay.Interactive.class));
		});
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " overlay checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void expectColor(final List<String> failures, final String label, final BufferedImage image, final int[] bounds, final int color, final boolean same) {
		final int actual = image.getRGB(bounds[0] + 4, bounds[1] + 4) & 0xFFFFFF;
		if (actual == color != same) {
			failures.add(label + " at " + Arrays.toString(bounds) + " is " + Integer.toHexString(actual) + (same ? " instead of " : " like ") + Integer.toHexString(color));
		}
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

	private static void verifyItems(final ClientGameTestContext context) {
		context.getInput().setCursorPos(960D, 540D);
		for (int notch = 0; notch < 20; notch++) {
			context.getInput().scroll(-1D);
		}

		context.waitTicks(40);
		final List<int[]> items = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(ItemNode.class));
		final List<int[]> blocks = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(BlockNode.class));
		final BufferedImage first = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-items-a"));
		context.waitTicks(7);
		final BufferedImage second = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-items-b"));
		final List<String> failures = new ArrayList<>();
		for (int index = 0; index < items.size(); index++) {
			FabricJoidClientGameTest.expectDrawn(failures, "item " + index, first, items.get(index));
		}

		for (int index = 0; index < blocks.size(); index++) {
			FabricJoidClientGameTest.expectDrawn(failures, "block " + index, first, blocks.get(index));
		}

		FabricJoidClientGameTest.expectChange(failures, "plain apple", List.of(first, second), items.get(0), true);
		FabricJoidClientGameTest.expectChange(failures, "glint of the enchanted sword", List.of(first, second), items.get(1), false);
		FabricJoidClientGameTest.expectChange(failures, "rotating furnace", List.of(first, second), blocks.get(3), false);

		final int[] pearl = items.get(4);
		context.getInput().setCursorPos(pearl[0] + pearl[2] / 2D, pearl[1] + pearl[3] / 2D);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(10);
		final BufferedImage cooldown = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-cooldown"));
		if (!context.computeOnClient(minecraft -> minecraft.player.getCooldowns().isOnCooldown(new ItemStack(Items.ENDER_PEARL)))) {
			failures.add("clicking the ender pearl did not start its cooldown");
		}

		FabricJoidClientGameTest.expectChange(failures, "cooldown overlay of the ender pearl", List.of(first, cooldown), pearl, false);

		final int[] apple = items.get(5);
		final int[] tooltip = {apple[0] + apple[2] / 2 + 12, apple[1] + apple[3] / 2 - 30, 160, 40};
		context.getInput().setCursorPos(apple[0] + apple[2] / 2D, apple[1] + apple[3] / 2D);
		context.waitTicks(5);
		final BufferedImage hovered = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-tooltip"));
		FabricJoidClientGameTest.expectChange(failures, "vanilla tooltip of the golden apple", List.of(cooldown, hovered), tooltip, false);
		context.getInput().setCursorPos(960D, 540D);

		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(2);
			minecraft.resizeGui();
		});
		context.waitTicks(40);
		context.takeScreenshot("joid-demo-minecraft-items-gui-scale-2");
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(0);
			minecraft.resizeGui();
		});
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " item checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyEntitiesWithoutWorld(final ClientGameTestContext context) {
		final PlayerSkin skin = PlayerSkin.insecure(new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("joid", "demo/skin"), Identifier.fromNamespaceAndPath("joid", "demo/textures/skin.png")), null, null, PlayerModelType.WIDE);
		final EntityNode profile = EntityNode.create(200, 200, 300, 600).profile(new GameProfile(new UUID(0L, 15L), "Steve")).rotationYaw(-30D);
		final EntityNode custom = EntityNode.create(700, 200, 300, 600).skin(skin).rotationPitch(20D);
		final EntityNode mob = EntityNode.create(1200, 200, 300, 600).type(EntityTypes.ZOMBIE);
		context.waitForScreen(TitleScreen.class);
		context.runOnClient(_ -> JOID.open(new UI() {

			@Override
			public void init() {
				profile.attach(this);
				custom.attach(this);
				mob.attach(this);
			}

		}));
		context.waitForScreen(UIScreen.class);
		context.waitTicks(40);
		final List<int[]> entities = context.computeOnClient(_ -> List.of(FabricJoidClientGameTest.bounds(profile), FabricJoidClientGameTest.bounds(custom), FabricJoidClientGameTest.bounds(mob)));
		final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot("joid-entities-title"));
		final List<String> failures = new ArrayList<>();
		FabricJoidClientGameTest.expectDrawn(failures, "player with a default skin without a world", image, entities.get(0));
		FabricJoidClientGameTest.expectDrawn(failures, "player with a custom skin without a world", image, entities.get(1));
		if (context.computeOnClient(_ -> mob.getEntity() != null)) {
			failures.add("an entity type was created without a world");
		}

		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(TitleScreen.class);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " entity checks without a world failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyEntities(final ClientGameTestContext context) {
		FabricJoidClientGameTest.scroll(context, 20);
		final List<int[]> entities = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(EntityNode.class));
		final int[] player = entities.get(9);
		final int[] villager = entities.get(10);
		context.getInput().setCursorPos(player[0] - 200D, player[1] + 20D);
		context.waitTicks(5);
		final BufferedImage left = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-entities-a"));
		context.getInput().setCursorPos(villager[0] + villager[2] + 80D, villager[1] + 20D);
		context.waitTicks(5);
		final BufferedImage right = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-minecraft-entities-b"));
		context.getInput().setCursorPos(960D, 540D);
		final List<String> failures = new ArrayList<>();
		final List<String> labels = List.of("zombie", "creeper", "pig", "chicken", "default skin", "custom skin", "local player", "masked local player", "rotated local player", "local player following the mouse", "villager following the mouse");
		for (int index = 0; index < entities.size(); index++) {
			FabricJoidClientGameTest.expectDrawn(failures, labels.get(index), left, entities.get(index));
		}

		FabricJoidClientGameTest.expectChange(failures, "zombie", List.of(left, right), entities.get(0), true);
		FabricJoidClientGameTest.expectChange(failures, "rotating creeper", List.of(left, right), entities.get(1), false);
		FabricJoidClientGameTest.expectChange(failures, "player with a default skin", List.of(left, right), entities.get(4), true);
		FabricJoidClientGameTest.expectChange(failures, "head of the player following the mouse", List.of(left, right), new int[] {player[0], player[1], player[2], player[3] / 3}, false);
		FabricJoidClientGameTest.expectChange(failures, "head of the villager following the mouse", List.of(left, right), new int[] {villager[0], villager[1], villager[2], villager[3] / 3}, false);
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(2);
			minecraft.resizeGui();
		});
		context.waitTicks(40);
		context.takeScreenshot("joid-demo-minecraft-entities-gui-scale-2");
		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(0);
			minecraft.resizeGui();
		});
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " entity checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifySound(final ClientGameTestContext context) {
		FabricJoidClientGameTest.scroll(context, 20);
		final List<int[]> resources = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(ResourceNode.class));
		final int[] noteBlock = resources.get(resources.size() - 1);
		final List<Identifier> played = new ArrayList<>();
		final SoundEventListener listener = (sound, _, _) -> played.add(sound.getIdentifier());
		context.runOnClient(minecraft -> minecraft.getSoundManager().addListener(listener));
		context.getInput().setCursorPos(noteBlock[0] + noteBlock[2] / 2D, noteBlock[1] + noteBlock[3] / 2D);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		context.takeScreenshot("joid-demo-minecraft-sound");
		context.getInput().setCursorPos(960D, 540D);
		final List<Identifier> sounds = context.computeOnClient(minecraft -> {
			minecraft.getSoundManager().removeListener(listener);
			return List.copyOf(played);
		});
		if (!sounds.equals(List.of(SoundEvents.NOTE_BLOCK_PLING.value().location()))) {
			throw new AssertionError("Clicking the note block played " + sounds + " instead of the note block pling alone");
		}
	}

	private static void verifyScale(final ClientGameTestContext context) {
		final List<String> failures = new ArrayList<>();
		FabricJoidClientGameTest.clickToggle(context, 2);
		FabricJoidClientGameTest.expectScale(context, failures, "clicking the active toggle", false, false, 1D);
		FabricJoidClientGameTest.clickToggle(context, 2);
		FabricJoidClientGameTest.clickToggle(context, 1);
		FabricJoidClientGameTest.expectScale(context, failures, "clicking the limit toggle", true, true, 0.75D);
		context.takeScreenshot("joid-demo-minecraft-scale-limited");
		FabricJoidClientGameTest.clickToggle(context, 1);
		FabricJoidClientGameTest.expectScale(context, failures, "clicking the limit toggle again", true, false, 1D);
		for (final int guiScale : new int[] {2, 0}) {
			for (final boolean scaled : new boolean[] {true, false}) {
				context.runOnClient(minecraft -> {
					JOID.getUI(UIDemoMinecraft.class).getScale().setActive(scaled);
					minecraft.options.guiScale().set(guiScale);
					minecraft.resizeGui();
				});
				context.waitTicks(20);
				context.takeScreenshot("joid-demo-minecraft-scale-gui-" + guiScale + "-" + (scaled ? "active" : "inactive"));
				FabricJoidClientGameTest.expectScale(context, failures, "the GUI scale " + guiScale, scaled, false, scaled && guiScale == 2 ? 0.5D : 1D);
			}
		}

		context.runOnClient(_ -> JOID.getUI(UIDemoMinecraft.class).getScale().setActive(true));
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " interface scale checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void expectScale(final ClientGameTestContext context, final List<String> failures, final String label, final boolean active, final boolean limited, final double interfaceScale) {
		final String actual = context.computeOnClient(_ -> {
			final UI ui = JOID.getUI(UIDemoMinecraft.class);
			return ui.getScale().active() + " " + ui.getScale().limited() + " " + ui.getView().getInterfaceScale();
		});
		final String expected = active + " " + limited + " " + interfaceScale;
		if (!actual.equals(expected)) {
			failures.add(label + " gives active, limited and interface scale " + actual + " instead of " + expected);
		}
	}

	private static void verifyAudio(final ClientGameTestContext context) {
		final UIDemoPlayer player = new UIDemoPlayer();
		final double master = context.computeOnClient(minecraft -> minecraft.options.getSoundSourceOptionInstance(SoundSource.MASTER).get());
		final double interfaceVolume = context.computeOnClient(minecraft -> minecraft.options.getSoundSourceOptionInstance(SoundSource.UI).get());
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		context.runOnClient(_ -> JOID.open(player));
		context.waitFor(_ -> FabricJoidClientGameTest.audioSource(player) != 0, 1200);
		final List<String> failures = new ArrayList<>();
		for (final double[] volumes : new double[][] {{1D, 1D}, {1D, 0.25D}, {0.5D, 0.25D}, {0.8D, 0.5D}}) {
			context.runOnClient(minecraft -> {
				minecraft.options.getSoundSourceOptionInstance(SoundSource.MASTER).set(volumes[0]);
				minecraft.options.getSoundSourceOptionInstance(SoundSource.UI).set(volumes[1]);
			});
			context.waitTicks(10);
			final float gain = context.computeOnClient(_ -> AL10.alGetSourcef(FabricJoidClientGameTest.audioSource(player), AL10.AL_GAIN));
			final float expected = 0.3F * (float) (volumes[0] * volumes[1]);
			if (Math.abs(gain - expected) > 1E-4F) {
				failures.add("master " + volumes[0] + " and interface " + volumes[1] + " give the video gain " + gain + " instead of " + expected);
			}
		}

		context.runOnClient(minecraft -> {
			minecraft.options.getSoundSourceOptionInstance(SoundSource.MASTER).set(master);
			minecraft.options.getSoundSourceOptionInstance(SoundSource.UI).set(interfaceVolume);
		});
		context.takeScreenshot("joid-demo-player");
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " audio checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static int audioSource(final UI ui) {
		for (final Node node : ui.getNodeList().ordered()) {
			final int source = FabricJoidClientGameTest.audioSource(node);
			if (source != 0) {
				return source;
			}
		}
		return 0;
	}

	private static int audioSource(final Node node) {
		if (node instanceof final ResourcePlayerNode player) {
			final Resource resource = (Resource) FabricJoidClientGameTest.field(player, "resource");
			final Object decoder = resource == null ? null : resource.getDecoder();
			final Object audioPlayer = decoder instanceof VideoResourceDecoder ? FabricJoidClientGameTest.field(decoder, "audioPlayer") : null;
			final Object source = audioPlayer == null ? null : FabricJoidClientGameTest.field(audioPlayer, "source");
			return source == null ? 0 : (int) FabricJoidClientGameTest.field(source, "source");
		}

		for (final Node child : node.getChildren(Node.class)) {
			final int source = FabricJoidClientGameTest.audioSource(child);
			if (source != 0) {
				return source;
			}
		}
		return 0;
	}

	private static Object field(final Object target, final String name) {
		for (Class<?> clazz = target.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			try {
				final Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field.get(target);
			} catch (final NoSuchFieldException exception) {
				continue;
			} catch (final IllegalAccessException exception) {
				throw new AssertionError("Unable to read the field " + name + " of " + clazz.getName(), exception);
			}
		}
		throw new AssertionError(target.getClass().getName() + " has no field " + name);
	}

	private static void scroll(final ClientGameTestContext context, final int notches) {
		context.getInput().setCursorPos(960D, 540D);
		for (int notch = 0; notch < notches; notch++) {
			context.getInput().scroll(-1D);
		}

		context.waitTicks(40);
	}

	private static List<int[]> bounds(final Class<? extends Node> clazz) {
		final UI ui = JOID.getUI(UIDemoMinecraft.class);
		final List<int[]> bounds = new ArrayList<>();
		for (final Node node : ui.getNodeList().ordered()) {
			FabricJoidClientGameTest.bounds(ui, node, clazz, bounds);
		}
		return bounds;
	}

	private static int[] bounds(final Node node) {
		final UI ui = node.getUi();
		return new int[] {(int) Math.round(ui.getView().toScreenX(node.getAbsoluteX())), (int) Math.round(ui.getView().toScreenY(node.getAbsoluteY())), (int) Math.round(ui.getView().toScreenWidth(node.getWidth())), (int) Math.round(ui.getView().toScreenHeight(node.getHeight()))};
	}

	private static void bounds(final UI ui, final Node node, final Class<? extends Node> clazz, final List<int[]> bounds) {
		if (clazz.isInstance(node)) {
			final Node parent = node.getParent() != null ? node.getParent() : node;
			final double left = Math.max(node.getAbsoluteX(), parent.getAbsoluteX());
			final double top = Math.max(node.getAbsoluteY(), parent.getAbsoluteY());
			final double right = Math.min(node.getAbsoluteX() + node.getWidth(), parent.getAbsoluteX() + parent.getWidth());
			final double bottom = Math.min(node.getAbsoluteY() + node.getHeight(), parent.getAbsoluteY() + parent.getHeight());
			bounds.add(new int[] {(int) Math.round(ui.getView().toScreenX(left)), (int) Math.round(ui.getView().toScreenY(top)), (int) Math.round(ui.getView().toScreenWidth(right - left)), (int) Math.round(ui.getView().toScreenHeight(bottom - top))});
		}

		for (final Node child : node.getChildren(Node.class)) {
			FabricJoidClientGameTest.bounds(ui, child, clazz, bounds);
		}
	}

	private static void expectDrawn(final List<String> failures, final String label, final BufferedImage image, final int[] bounds) {
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
			failures.add(label + " at " + Arrays.toString(bounds) + " is almost empty: " + drawn + " drawn pixels");
		}
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
		FabricJoidClientGameTest.expectChange(failures, label, captures, new int[] {x, y, size, size}, same);
	}

	private static void expectChange(final List<String> failures, final String label, final List<BufferedImage> captures, final int[] bounds, final boolean same) {
		final int x = bounds[0];
		final int y = bounds[1];
		boolean changed = false;
		for (int capture = 1; capture < captures.size(); capture++) {
			for (int row = 0; row < bounds[3] && !changed; row++) {
				for (int column = 0; column < bounds[2] && !changed; column++) {
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
		UIDemoChoice.LIST.remove(UIDemoMinecraft.class);
		UIDemoChoice.LIST.remove(UIDemoOverlay.class);
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
			UIDemoChoice.LIST.add(UIDemoMinecraft.class);
			UIDemoChoice.LIST.add(UIDemoOverlay.class);
			runner.stop();
		}

		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " snapshots differ from their reference:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	@UIData(background = false)
	@UIDataOverlay(active = true)
	@UIDataOverlayLayer(layer = OverlayLayer.HOTBAR, cancel = true)
	public static final class HiddenHotbar extends UI {

		@Override
		public void init() {}

	}

}