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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.joml.Vector2d;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.backend.minecraft.bridge.thread.MinecraftThreadBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.demo.ui.DemoEntry;
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
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.font.converter.TextConverter;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.resource.decoder.impl.VideoResourceDecoder;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.core.data.popup.UIDataPopup;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.CircleNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.impl.design.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.design.resource.ResourcePlayerNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.ui.node.impl.structure.container.ContainerNode;
import dev.joid.test.snapshot.SnapshotDifference;
import dev.joid.test.snapshot.SnapshotImage;
import dev.joid.test.snapshot.SnapshotRunner;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class FabricJoidClientGameTest implements FabricClientGameTest {

	@Override
	public void runTest(final ClientGameTestContext context) {
		context.waitFor(_ -> BridgeHandler.UI.getBridge(ScreenUIBridge.class) != null);
		context.getInput().resizeWindow(1920, 1080);
		FabricJoidClientGameTest.verifyKeyBindLabels(context);
		FabricJoidClientGameTest.verifyEntitiesWithoutWorld(context);
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			singleplayer.getConnection().waitForChunksRender();
			context.getInput().pressKey(GLFW.GLFW_KEY_J);
			context.waitForScreen(UIScreen.class);
			context.runOnClient(_ -> {
				if (JOID.getUi(UIDemoChoice.class) == null) {
					throw new AssertionError("The key J did not open UIDemoChoice");
				}
			});
			context.waitTicks(40);
			context.takeScreenshot("joid-demo-choice");
			FabricJoidClientGameTest.guiScale(context, 2, 0.5D, "joid-demo-choice-gui-scale-2");
			FabricJoidClientGameTest.guiScale(context, 0, 1D, "joid-demo-choice-gui-scale-auto");
			FabricJoidClientGameTest.clickDemo(context, "UIDemoMinecraft", "joid-demo-choice-minecraft");
			context.runOnClient(_ -> {
				if (JOID.getUi(UIDemoMinecraft.class) == null) {
					throw new AssertionError("Clicking the entry UIDemoMinecraft of UIDemoChoice did not open it");
				}
			});
			FabricJoidClientGameTest.verifyHostBindings(context);

			FabricJoidClientGameTest.screenshot(context, new UIDemoFont(), "joid-demo-font");
			FabricJoidClientGameTest.screenshot(context, new UIDemoShader(), "joid-demo-shader");
			FabricJoidClientGameTest.screenshot(context, new UIDemoEffect(), "joid-demo-effect");
			FabricJoidClientGameTest.screenshot(context, new UIDemoMinecraft(), "joid-demo-minecraft");
			FabricJoidClientGameTest.verifyMinecraftData(context);
			FabricJoidClientGameTest.verifyResources(context);
			FabricJoidClientGameTest.verifyReload(context);
			FabricJoidClientGameTest.verifyItems(context);
			FabricJoidClientGameTest.verifyEntities(context);
			FabricJoidClientGameTest.verifySound(context);
			FabricJoidClientGameTest.verifyScale(context);
			FabricJoidClientGameTest.verifyAudio(context);
			context.runOnClient(FabricJoidClientGameTest::verifyWidths);
			FabricJoidClientGameTest.verifyBitmapFont(context);
			FabricJoidClientGameTest.verifyDepth(context);
			context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
			context.waitTicks(20);
			context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
			context.waitForScreen(null);
			FabricJoidClientGameTest.verifyBackground(context);
			FabricJoidClientGameTest.verifyOverlays(context);
			FabricJoidClientGameTest.verifyContainer(context, singleplayer.getServer());
		}

		context.runOnClient(_ -> FabricJoidClientGameTest.verify(JUnitCore.runClasses(FabricRenderBridgeContractTest.class)));
		context.runOnClient(_ -> FabricJoidClientGameTest.verifySnapshots(new File(System.getProperty("joid.snapshot.output", "snapshots/renders")), new File(System.getProperty("joid.snapshot.references", "snapshots/references"))));
	}

	private static void verifyHostBindings(final ClientGameTestContext context) {
		context.getInput().pressKey(GLFW.GLFW_KEY_E);
		context.waitTicks(40);
		context.runOnClient(minecraft -> {
			if (JOID.isOpen(UIDemoMinecraft.class) || !JOID.isOpen(UIDemoChoice.class)) {
				throw new AssertionError("The inventory key bound by UIDemoMinecraft did not close it back to UIDemoChoice");
			}

			if (KeyResolver.resolve(minecraft.options.keyInventory) != Key.E) {
				throw new AssertionError("The inventory key mapping resolves to " + KeyResolver.resolve(minecraft.options.keyInventory) + " instead of E");
			}

			final String converted = TextConverter.convert(Component.literal("A").withStyle(ChatFormatting.RED));
			if (!converted.equals("\u00A7r\u00A7cA")) {
				throw new AssertionError("A red chat component converts to " + converted);
			}

			if (!(BridgeHandler.THREAD.get() instanceof MinecraftThreadBridge) || !BridgeHandler.THREAD.get().isRenderThread()) {
				throw new AssertionError("The thread bridge is " + BridgeHandler.THREAD.get());
			}
		});
	}

	private static void verifyMinecraftData(final ClientGameTestContext context) {
		context.runOnClient(minecraft -> {
			final Screen screen = minecraft.gui.screen();
			if (screen.isPauseScreen() || screen.isInGameUi() || !screen.getTitle().getString().equals("Minecraft demo")) {
				throw new AssertionError("The screen of UIDemoMinecraft does not follow its UIDataMinecraft: pause " + screen.isPauseScreen() + ", in game " + screen.isInGameUi() + ", title " + screen.getTitle().getString());
			}
		});
	}

	private static void verifyBackground(final ClientGameTestContext context) {
		final List<String> failures = new ArrayList<>();
		context.runOnClient(minecraft -> minecraft.options.showSubtitles().set(true));
		context.waitTicks(5);
		final BufferedImage world = FabricJoidClientGameTest.read(context.takeScreenshot("joid-background-world"));
		final BufferedImage vanilla = FabricJoidClientGameTest.verifyBackground(context, failures, new VanillaBackground(), "joid-background-vanilla");
		final BufferedImage none = FabricJoidClientGameTest.verifyBackground(context, failures, new NoBackground(), "joid-background-none");
		context.runOnClient(minecraft -> minecraft.options.showSubtitles().set(false));
		final double darkened = FabricJoidClientGameTest.difference(world, vanilla);
		final double unchanged = FabricJoidClientGameTest.difference(world, none);
		if (darkened < 20D) {
			failures.add("the default background differs from the world by " + darkened + " levels on average, the vanilla menu background is missing");
		}

		if (unchanged > 4D) {
			failures.add("the background disabled by UIDataMinecraft differs from the world by " + unchanged + " levels on average");
		}

		System.out.println("[JOID] Background: vanilla " + darkened + ", none " + unchanged + " levels from the world");
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " background checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static BufferedImage verifyBackground(final ClientGameTestContext context, final List<String> failures, final UI ui, final String name) {
		final int[] subtitles = {1400, 640, 520, 440};
		context.runOnClient(_ -> JOID.open(ui));
		context.waitForScreen(UIScreen.class);
		context.waitTicks(20);
		if (context.computeOnClient(minecraft -> !minecraft.gui.screen().isPauseScreen() || !minecraft.gui.screen().getTitle().getString().isEmpty())) {
			failures.add(name + " does not pause the game with an empty title like an absent UIDataMinecraft");
		}

		final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot(name));
		context.runOnClient(minecraft -> minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1F)));
		context.waitTicks(5);
		final BufferedImage subtitled = FabricJoidClientGameTest.read(context.takeScreenshot(name + "-subtitles"));
		FabricJoidClientGameTest.expectChange(failures, "subtitles of " + name, List.of(image, subtitled), subtitles, false);
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.waitTicks(40);
		return image;
	}

	private static void verifyKeyBindLabels(final ClientGameTestContext context) {
		context.runOnClient(minecraft -> minecraft.gui.setScreen(new KeyBindsScreen(minecraft.gui.screen(), minecraft.options)));
		context.waitForScreen(KeyBindsScreen.class);
		context.runOnClient(minecraft -> {
			final KeyBindsList list = (KeyBindsList) FabricJoidClientGameTest.field(minecraft.gui.screen(), "keyBindsList");
			list.setScrollAmount(list.maxScrollAmount());
		});
		context.waitTicks(5);
		context.takeScreenshot("joid-controls");
		final List<String> labels = context.computeOnClient(minecraft -> {
			final List<String> entries = new ArrayList<>();
			for (final KeyBindsList.Entry entry : ((KeyBindsList) FabricJoidClientGameTest.field(minecraft.gui.screen(), "keyBindsList")).children()) {
				entries.add((entry instanceof KeyBindsList.CategoryEntry ? ((FocusableTextWidget) FabricJoidClientGameTest.field(entry, "categoryName")).getMessage() : (Component) FabricJoidClientGameTest.field(entry, "name")).getString());
			}
			return entries;
		});
		final List<String> french = context.computeOnClient(minecraft -> {
			final ClientLanguage language = ClientLanguage.loadFrom(minecraft.getResourceManager(), List.of("fr_fr"), false);
			return List.of(language.getOrDefault("key.category.joid.main"), language.getOrDefault("key.joid.demo"));
		});
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(TitleScreen.class);

		final int category = labels.indexOf("JOID");
		if (category < 0 || labels.indexOf("Open JOID demos") != category + 1) {
			throw new AssertionError("The controls screen does not list the category JOID with Open JOID demos: " + labels.subList(Math.max(0, labels.size() - 4), labels.size()));
		}

		if (!french.equals(List.of("JOID", "Ouvrir les démos JOID"))) {
			throw new AssertionError("The French labels of the demo key are " + french);
		}
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

	private static void clickDemo(final ClientGameTestContext context, final String label, final String name) {
		final Vector2d center = context.computeOnClient(_ -> FabricJoidClientGameTest.center(label));
		context.getInput().setCursorPos(center.x, center.y);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.getInput().setCursorPos(960D, 540D);
		context.waitTicks(40);
		context.takeScreenshot(name);
	}

	private static Vector2d center(final String label) {
		final int index = UIDemoChoice.LIST.stream().map(DemoEntry::getLabel).toList().indexOf(label);

		if (index < 0) {
			throw new AssertionError("UIDemoChoice does not list " + label);
		}

		final UI ui = JOID.getUi(UIDemoChoice.class);
		final Node entry = ui.getNodeList().ordered().getFirst().getChild(index, RectNode.class);
		return new Vector2d(ui.getView().toScreenX(entry.getAbsoluteX() + entry.getWidth() / 2D), ui.getView().toScreenY(entry.getAbsoluteY() + entry.getHeight() / 2D));
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
			final double actual = JOID.getUi(UIDemoChoice.class).getView().getInterfaceScale();
			if (actual != interfaceScale) {
				throw new AssertionError("The GUI scale " + guiScale + " gives the interface scale " + actual + " instead of " + interfaceScale);
			}
		});
	}

	private static void verifyBitmapFont(final ClientGameTestContext context) {
		final RectNode background = RectNode.create(0, 0, 1920, 1080).color(Color.BLACK);
		final TextNode text = TextNode.create(960, 540).text(Text.create("HH", TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 4, Color.WHITE)));
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
		for (final int[] scale : new int[][] {{1, 1}, {2, 2}, {0, 4}}) {
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

	private static void verifyDepth(final ClientGameTestContext context) {
		final List<String> failures = new ArrayList<>();
		FabricJoidClientGameTest.verifyDepth(context, failures, "joid-depth", null, 2);
		FabricJoidClientGameTest.verifyDepth(context, failures, "joid-depth-rounded", () -> RoundedNodeEffect.create(24F), 2);
		FabricJoidClientGameTest.verifyDepth(context, failures, "joid-depth-circle", CircleNodeEffect::create, 2);
		FabricJoidClientGameTest.verifyDepth(context, failures, "joid-depth-blur", () -> BlurNodeEffect.create(2F), 12);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " depth checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyDepth(final ClientGameTestContext context, final List<String> failures, final String name, final Supplier<NodeEffect<Node>> effect, final int inset) {
		final List<RectNode> covers = new ArrayList<>();
		final Node grid = FabricJoidClientGameTest.depthGrid(covers, effect);
		final UI ui = new UI() {

			@Override
			public void init() {
				grid.attach(this);
			}

		};
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		context.runOnClient(_ -> JOID.open(ui));
		context.waitTicks(40);
		final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot(name));
		final List<int[]> bounds = context.computeOnClient(_ -> covers.stream().map(FabricJoidClientGameTest::bounds).toList());
		FabricJoidClientGameTest.expectDepth(failures, name, image, bounds, inset);
		context.runOnClient(_ -> JOID.close(ui));
		context.waitTicks(10);
	}

	private static void verifyContainerDepth(final ClientGameTestContext context, final List<String> failures) {
		final UI ui = context.computeOnClient(_ -> JOID.getUi(UIDemoContainer.class));
		final Node root = ContainerNode.create(0, 0, 1920, 1080).zindex(20);
		context.runOnClient(_ -> root.attach(ui));
		context.getInput().setCursorPos(4D, 4D);
		for (final String name : List.of("joid-depth-container", "joid-depth-container-circle")) {
			final Supplier<NodeEffect<Node>> effect = name.endsWith("circle") ? CircleNodeEffect::create : null;
			final List<RectNode> covers = new ArrayList<>();
			final Node grid = FabricJoidClientGameTest.depthGrid(covers, effect);
			context.runOnClient(_ -> grid.attach(root));
			context.waitTicks(40);
			final BufferedImage image = FabricJoidClientGameTest.read(context.takeScreenshot(name));
			final List<int[]> bounds = context.computeOnClient(_ -> covers.stream().map(FabricJoidClientGameTest::bounds).toList());
			FabricJoidClientGameTest.expectDepth(failures, name, image, bounds, 2);
			context.runOnClient(_ -> root.remove(grid));
			context.waitTicks(10);
		}
	}

	private static Node depthGrid(final List<RectNode> covers, final Supplier<NodeEffect<Node>> effect) {
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

				FabricJoidClientGameTest.depthNode(row, left, top, size).attach(parent);
				if (column == 0) {
					rect.attach(parent);
				}
				covers.add(rect);
			}
		}
		return grid;
	}

	private static Node depthNode(final int row, final double x, final double y, final double size) {
		return switch (row) {
			case 0 -> ItemNode.create(x, y, size, size).stack(new ItemStack(Items.DIAMOND));
			case 1 -> ItemNode.create(x, y, size, size).stack(new ItemStack(Items.DIAMOND_SWORD)).glint(true);
			case 2 -> BlockNode.create(x, y, size, size).block(Blocks.STONE.defaultBlockState());
			case 3 -> BlockNode.create(x, y, size, size).block(Blocks.ICE.defaultBlockState());
			case 4 -> EntityNode.create(x, y, size, size).type(EntityTypes.ZOMBIE);
			default -> EntityNode.create(x, y, size, size).profile(new GameProfile(new UUID(0L, 15L), "Steve"));
		};
	}

	private static void expectDepth(final List<String> failures, final String name, final BufferedImage image, final List<int[]> bounds, final int inset) {
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
				failures.add(label + " shows " + drawn + " pixels through the node");
			} else if (index % 4 >= 2 && drawn < 100) {
				failures.add(label + " is hidden: " + drawn + " pixels drawn");
			}
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
		FabricJoidClientGameTest.clickDemo(context, "UIDemoOverlayLayer", "joid-demo-choice-overlay-on");
		FabricJoidClientGameTest.expectOverlays(context, true);
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.waitTicks(10);

		final List<String> failures = new ArrayList<>();
		final int[] hotbar = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getNodeList().ordered().getFirst()));
		final int[] experience = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUi(UIDemoOverlayLayer.Experience.class).getNodeList().ordered().getFirst()));
		final int[] interactive = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUi(UIDemoOverlayLayer.Interactive.class).getNodeList().ordered().getFirst()));
		final int[] button = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUi(UIDemoOverlayLayer.Interactive.class).getNodeList().ordered().getFirst().getChild(0, RectNode.class)));
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
		context.runOnClient(_ -> JOID.close(JOID.getUi(HiddenHotbar.class)));
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
		final int clicks = context.computeOnClient(_ -> (int) FabricJoidClientGameTest.field(JOID.getUi(UIDemoOverlayLayer.Interactive.class), "clicks"));
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
		final double interfaceScale = context.computeOnClient(_ -> JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getView().getInterfaceScale());
		if (interfaceScale != 0.5D) {
			failures.add("the GUI scale 2 gives the overlays the interface scale " + interfaceScale + " instead of 0.5");
		}

		context.runOnClient(minecraft -> {
			minecraft.options.guiScale().set(0);
			minecraft.resizeGui();
		});
		context.waitTicks(5);
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitForScreen(UIScreen.class);
		context.waitTicks(20);
		FabricJoidClientGameTest.clickDemo(context, "UIDemoOverlayLayer", "joid-demo-choice-overlay-off");
		FabricJoidClientGameTest.expectOverlays(context, false);
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.waitTicks(10);
		final BufferedImage off = FabricJoidClientGameTest.read(context.takeScreenshot("joid-overlay-off"));
		FabricJoidClientGameTest.expectColor(failures, "overlay above the hotbar toggled off", off, hotbar, 0xDDDDDD, false);
		FabricJoidClientGameTest.expectColor(failures, "interactive overlay toggled off", off, interactive, 0xDDDDDD, false);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " overlay checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyContainer(final ClientGameTestContext context, final TestServerContext server) {
		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitForScreen(UIScreen.class);
		context.waitTicks(20);
		final Vector2d entry = context.computeOnClient(_ -> FabricJoidClientGameTest.center("UIDemoContainer"));
		context.getInput().setCursorPos(entry.x, entry.y);
		context.waitTicks(2);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitForScreen(ContainerUIScreen.class);
		context.waitTicks(20);
		server.runOnServer(minecraftServer -> {
			final DemoContainer container = (DemoContainer) FabricJoidClientGameTest.player(minecraftServer).containerMenu;
			container.getSlot(0).set(new ItemStack(Items.DIAMOND, 10));
			container.getSlot(1).set(new ItemStack(Items.GOLDEN_APPLE));
		});
		context.waitTicks(10);

		final List<String> failures = new ArrayList<>();
		final List<int[]> slots = context.computeOnClient(_ -> FabricJoidClientGameTest.slotBounds());
		final BufferedImage opened = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-container"));
		FabricJoidClientGameTest.expectDrawn(failures, "diamonds in the first storage slot", opened, slots.get(0));
		FabricJoidClientGameTest.moveTo(context, slots.get(0));
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		FabricJoidClientGameTest.moveTo(context, slots.get(9));
		final BufferedImage carried = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-container-carried"));
		FabricJoidClientGameTest.expectChange(failures, "carried diamonds drawn under the mouse", List.of(opened, carried), slots.get(9), false);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a left click on 10 diamonds", 10, new int[] {0, 0});

		context.getInput().holdMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.getInput().setCursorPos(slots.get(9)[0] + slots.get(9)[2] / 2D + 4D, slots.get(9)[1] + slots.get(9)[3] / 2D);
		context.waitTicks(2);
		FabricJoidClientGameTest.moveTo(context, slots.get(10));
		FabricJoidClientGameTest.moveTo(context, slots.get(11));
		context.takeScreenshot("joid-demo-container-drag");
		context.getInput().releaseMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a left drag of 10 diamonds over three slots", 1, new int[] {9, 3}, new int[] {10, 3}, new int[] {11, 3});

		FabricJoidClientGameTest.moveTo(context, slots.get(12));
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a left click with 1 diamond on an empty slot", 0, new int[] {12, 1});

		FabricJoidClientGameTest.moveTo(context, slots.get(9));
		context.runOnClient(minecraft -> {
			final double x = minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
			final double y = minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
			final MouseButtonEvent event = new MouseButtonEvent(x, y, new MouseButtonInfo(GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_MOD_SHIFT));
			minecraft.gui.screen().mouseClicked(event, false);
			minecraft.gui.screen().mouseReleased(event);
		});
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a shift-click on 3 diamonds", 0, new int[] {9, 0}, new int[] {62, 3});

		FabricJoidClientGameTest.moveTo(context, slots.get(10));
		context.getInput().pressKey(GLFW.GLFW_KEY_1);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "the key 1 over 3 diamonds", 0, new int[] {10, 0}, new int[] {54, 3});

		FabricJoidClientGameTest.moveTo(context, slots.get(13));
		final BufferedImage unhovered = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-container-unhovered"));
		FabricJoidClientGameTest.moveTo(context, slots.get(1));
		context.waitTicks(5);
		final BufferedImage tooltip = FabricJoidClientGameTest.read(context.takeScreenshot("joid-demo-container-tooltip"));
		final int[] apple = slots.get(1);
		FabricJoidClientGameTest.expectChange(failures, "vanilla tooltip of the hovered golden apple", List.of(unhovered, tooltip), new int[] {apple[0] + apple[2], apple[1] - apple[3], apple[2] * 3, apple[3]}, false);
		if (!context.computeOnClient(minecraft -> minecraft.gui.screen() instanceof final ContainerUIScreen<?> screen && screen.getHoveredSlot() != null && screen.getHoveredSlot().index == 1)) {
			failures.add("the hovered slot of the container screen is not the golden apple");
		}

		context.getInput().pressKey(GLFW.GLFW_KEY_Q);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "the key Q over the golden apple", 0, new int[] {1, 0});

		FabricJoidClientGameTest.verifyContainerPopup(context, server, failures);
		FabricJoidClientGameTest.verifyContainerBounds(context, server, failures);
		FabricJoidClientGameTest.verifyContainerDepth(context, failures);

		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(null);
		context.waitTicks(5);
		if (!server.computeOnServer(minecraftServer -> FabricJoidClientGameTest.player(minecraftServer).containerMenu == FabricJoidClientGameTest.player(minecraftServer).inventoryMenu)) {
			failures.add("Escape does not close the container on the server");
		}

		context.getInput().setCursorPos(960D, 540D);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " container checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void verifyContainerBounds(final ClientGameTestContext context, final TestServerContext server, final List<String> failures) {
		if (context.computeOnClient(minecraft -> minecraft.gui.screen().isPauseScreen() || !minecraft.gui.screen().isInGameUi())) {
			failures.add("the container screen pauses the game or is not an in-game screen");
		}

		final int[] panel = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(JOID.getUi(UIDemoContainer.class).getNodeList().ordered().getFirst()));
		final int[] image = context.computeOnClient(minecraft -> {
			final int scale = minecraft.getWindow().getGuiScale();
			final Screen screen = minecraft.gui.screen();
			return new int[] {(int) FabricJoidClientGameTest.field(screen, "leftPos") * scale, (int) FabricJoidClientGameTest.field(screen, "topPos") * scale, (int) FabricJoidClientGameTest.field(screen, "imageWidth") * scale, (int) FabricJoidClientGameTest.field(screen, "imageHeight") * scale, scale};
		});
		for (int i = 0; i < 4; i++) {
			if (Math.abs(image[i] - panel[i]) >= image[4] * 2) {
				failures.add("the container screen bounds " + Arrays.toString(image) + " do not follow the panel " + Arrays.toString(panel));
				break;
			}
		}

		server.runOnServer(minecraftServer -> FabricJoidClientGameTest.player(minecraftServer).containerMenu.getSlot(3).set(new ItemStack(Items.DIAMOND, 7)));
		context.waitTicks(10);
		final List<int[]> slots = context.computeOnClient(_ -> FabricJoidClientGameTest.slotBounds());
		FabricJoidClientGameTest.moveTo(context, slots.get(3));
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.moveTo(context, new int[] {panel[0] + 10, panel[1] + 10, 2, 2});
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a click in a hole of the panel with 7 diamonds", 7, new int[] {3, 0});
		FabricJoidClientGameTest.moveTo(context, new int[] {panel[0] - 60, panel[1] + panel[3] / 2, 2, 2});
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a click outside the panel with 7 diamonds", 0, new int[] {3, 0});
	}

	private static void verifyContainerPopup(final ClientGameTestContext context, final TestServerContext server, final List<String> failures) {
		server.runOnServer(minecraftServer -> FabricJoidClientGameTest.player(minecraftServer).containerMenu.getSlot(2).set(new ItemStack(Items.DIAMOND, 5)));
		context.waitTicks(10);
		final Screen screen = context.computeOnClient(minecraft -> minecraft.gui.screen());
		final ContainerPopup popup = new ContainerPopup();
		context.runOnClient(_ -> JOID.open(popup));
		context.waitTicks(20);
		final int[] button = context.computeOnClient(_ -> FabricJoidClientGameTest.bounds(popup.getNodeList().ordered().getFirst()));
		FabricJoidClientGameTest.moveTo(context, button);
		context.takeScreenshot("joid-demo-container-popup");
		if (!context.computeOnClient(minecraft -> minecraft.gui.screen() == screen && JOID.isOpen(popup) && BridgeHandler.UI.getBridge(ContainerUIBridge.class).isOpen(popup))) {
			failures.add("the popup replaced the container screen instead of opening over it");
		}

		if (context.computeOnClient(_ -> ((ContainerUIScreen<?>) screen).getHoveredSlot() != null)) {
			failures.add("the slot under the popup is hovered");
		}

		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		context.getInput().pressKey(GLFW.GLFW_KEY_E);
		context.waitTicks(5);
		if (popup.clicks != 1) {
			failures.add("a click on the popup gives " + popup.clicks + " popup clicks instead of 1");
		}

		FabricJoidClientGameTest.expectContainer(context, server, failures, "a click on the popup over 5 diamonds", 0, new int[] {2, 5});
		if (!context.computeOnClient(minecraft -> minecraft.gui.screen() == screen)) {
			failures.add("the key E closed the container under the popup");
		}

		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitTicks(20);
		if (context.computeOnClient(minecraft -> JOID.isOpen(popup) || minecraft.gui.screen() != screen)) {
			failures.add("Escape does not close the popup alone");
		}

		if (!server.computeOnServer(minecraftServer -> FabricJoidClientGameTest.player(minecraftServer).containerMenu instanceof DemoContainer)) {
			failures.add("the container is closed on the server after the popup");
		}

		FabricJoidClientGameTest.moveTo(context, button);
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a left click on 5 diamonds after the popup", 5, new int[] {2, 0});
		context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(5);
		FabricJoidClientGameTest.expectContainer(context, server, failures, "a left click with 5 diamonds after the popup", 0, new int[] {2, 5});

		context.runOnClient(_ -> JOID.open(new UIDemoChoice()));
		context.waitTicks(20);
		if (!context.computeOnClient(minecraft -> minecraft.gui.screen() == screen && JOID.isOpen(UIDemoChoice.class))) {
			failures.add("UIDemoChoice replaced the container screen instead of opening over it");
		}

		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitTicks(20);
		if (context.computeOnClient(minecraft -> JOID.isOpen(UIDemoChoice.class) || minecraft.gui.screen() != screen)) {
			failures.add("Escape does not close UIDemoChoice alone over the container");
		}
	}

	private static void expectContainer(final ClientGameTestContext context, final TestServerContext server, final List<String> failures, final String label, final int carried, final int[]... slots) {
		final String expected = FabricJoidClientGameTest.describe(carried, null, slots);
		final String client = context.computeOnClient(minecraft -> FabricJoidClientGameTest.describe(minecraft.player.containerMenu, slots));
		final String actual = server.computeOnServer(minecraftServer -> FabricJoidClientGameTest.describe(FabricJoidClientGameTest.player(minecraftServer).containerMenu, slots));
		if (!actual.equals(expected) || !client.equals(expected)) {
			failures.add(label + " gives " + actual + " on the server and " + client + " on the client instead of " + expected);
		}
	}

	private static String describe(final AbstractContainerMenu container, final int[]... slots) {
		return FabricJoidClientGameTest.describe(container.getCarried().getCount(), container.slots.stream().map(slot -> slot.getItem().getCount()).toList(), slots);
	}

	private static String describe(final int carried, final List<Integer> counts, final int[]... slots) {
		final StringBuilder description = new StringBuilder("carried " + carried);
		for (final int[] slot : slots) {
			description.append(", slot ").append(slot[0]).append(' ').append(counts == null ? slot[1] : counts.get(slot[0]));
		}
		return description.toString();
	}

	private static void moveTo(final ClientGameTestContext context, final int[] bounds) {
		context.getInput().setCursorPos(bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D);
		context.waitTicks(2);
	}

	private static List<int[]> slotBounds() {
		final UI ui = JOID.getUi(UIDemoContainer.class);
		final List<int[]> bounds = new ArrayList<>();
		for (final Node node : ui.getNodeList().ordered()) {
			FabricJoidClientGameTest.bounds(ui, node, SlotNode.class, bounds);
		}
		return bounds;
	}

	private static ServerPlayer player(final MinecraftServer server) {
		return server.getPlayerList().getPlayers().getFirst();
	}

	private static void expectOverlays(final ClientGameTestContext context, final boolean open) {
		context.runOnClient(_ -> {
			if (JOID.isOpen(UIDemoOverlayLayer.Hotbar.class) != open || JOID.isOpen(UIDemoOverlayLayer.Experience.class) != open || JOID.isOpen(UIDemoOverlayLayer.Interactive.class) != open) {
				throw new AssertionError("The overlay entry of UIDemoChoice did not toggle the demo overlays " + (open ? "on" : "off"));
			}
		});
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
		if (!(decoded instanceof final MinecraftTexture texture) || !texture.isDeleted()) {
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
					JOID.getUi(UIDemoMinecraft.class).getScale().setActive(scaled);
					minecraft.options.guiScale().set(guiScale);
					minecraft.resizeGui();
				});
				context.waitTicks(20);
				context.takeScreenshot("joid-demo-minecraft-scale-gui-" + guiScale + "-" + (scaled ? "active" : "inactive"));
				FabricJoidClientGameTest.expectScale(context, failures, "the GUI scale " + guiScale, scaled, false, scaled && guiScale == 2 ? 0.5D : 1D);
			}
		}

		context.runOnClient(_ -> JOID.getUi(UIDemoMinecraft.class).getScale().setActive(true));
		context.waitTicks(5);
		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " interface scale checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	private static void expectScale(final ClientGameTestContext context, final List<String> failures, final String label, final boolean active, final boolean limited, final double interfaceScale) {
		final String actual = context.computeOnClient(_ -> {
			final UI ui = JOID.getUi(UIDemoMinecraft.class);
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
		final UI ui = JOID.getUi(UIDemoMinecraft.class);
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

	private static double difference(final BufferedImage first, final BufferedImage second) {
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
				final int expected = minecraft.font.width(Component.literal(text).withStyle(style -> style.withFont(new FontDescription.Resource(font.getId()))));
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
		final SnapshotRunner runner = SnapshotRunner.start(new MinecraftSnapshotBackend());
		final List<DemoEntry> entries = new ArrayList<>(UIDemoChoice.LIST);
		UIDemoChoice.LIST.removeIf(entry -> entry.getHover() != null && entry.getHover().startsWith("dev.joid.backend.minecraft."));
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
			UIDemoChoice.LIST.clear();
			UIDemoChoice.LIST.addAll(entries);
			runner.stop();
		}

		if (!failures.isEmpty()) {
			throw new AssertionError(failures.size() + " snapshots differ from their reference:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
	}

	@UIDataPopup(active = true)
	public static final class ContainerPopup extends UI {

		private int clicks;

		@Override
		public void init() {
			RectNode.create(760, 244, 108, 108).color(Color.WHITE).onClick((_, _, _, _) -> this.clicks++).attach(this);
		}

	}

	@UIData(background = false)
	@UIDataOverlay(active = true)
	@UIDataOverlayLayer(layer = OverlayLayer.HOTBAR, cancel = true)
	public static final class HiddenHotbar extends UI {

		@Override
		public void init() {}

	}

	@UIData(background = false)
	public static final class VanillaBackground extends UI {

		@Override
		public void init() {}

	}

	@UIData(background = false)
	@UIDataMinecraft(background = false)
	public static final class NoBackground extends UI {

		@Override
		public void init() {}

	}

}