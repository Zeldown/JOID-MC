package dev.joid.backend.minecraft.demo.smoke;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.audio.MinecraftAudioGain;
import dev.joid.backend.minecraft.bridge.thread.MinecraftThreadBridge;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft;
import dev.joid.backend.minecraft.demo.ui.UIDemoMinecraftChoice;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer;
import dev.joid.base.lwjgl2.openal.Lwjgl2AlBinding;
import dev.joid.base.openal.AlAudioBridge;
import dev.joid.base.opengl.render.GlRenderBridge;
import dev.joid.demo.ui.DemoEntry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.simple.UIDemoSimple;
import dev.joid.lib.JOID;
import dev.joid.lib.JOIDEnv;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.audio.IAudioSource;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.MinecraftForgeClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientSmokeTest {

	private static final List<String>    WARNINGS     = new CopyOnWriteArrayList<>();
	private static final Queue<Runnable> SERVER_TASKS = new ConcurrentLinkedQueue<>();
	private static final AtomicLong      FRAMES       = new AtomicLong();

	private static String window;

	public static void start() {
		System.setErr(new WarningStream(System.err));
		FMLCommonHandler.instance().bus().register(new ClientSmokeTest());
		final Thread thread = new Thread(ClientSmokeTest::run, "JOID Client Smoke Test");
		thread.setDaemon(true);
		thread.start();
	}

	@SubscribeEvent
	public void render(final RenderTickEvent event) {
		if (event.phase == Phase.END) {
			ClientSmokeTest.FRAMES.incrementAndGet();
		}
	}

	@SubscribeEvent
	public void serverTick(final ServerTickEvent event) {
		if (event.phase != Phase.START) {
			return;
		}

		for (Runnable task = ClientSmokeTest.SERVER_TASKS.poll(); task != null; task = ClientSmokeTest.SERVER_TASKS.poll()) {
			task.run();
		}
	}

	private static void run() {
		final List<String> failures = new ArrayList<>();
		try {
			ClientSmokeTest.joinWorld();
			ClientSmokeTest.verifyBridges(failures);
			ClientSmokeTest.verifyAudio(failures, "in the world");
			ClientSmokeTest.verifyKeyBind(failures);
			ClientSmokeTest.verifyDebugKey(failures);
			ClientSmokeTest.verifyDemoMenu(failures);
			ClientSmokeTest.verifyMinecraftDemo(failures);
			ClientSmokeTest.verifyTooltip(failures);
			ClientSmokeTest.verifyOverlays(failures);
			ClientSmokeTest.verifyScreenInput(failures);
			ClientSmokeTest.verifyReload(failures);
			ClientSmokeTest.verifyWarnings(failures);
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
			failures.add("the smoke test stopped on " + throwable);
		}

		if (failures.isEmpty()) {
			System.out.println("[JOID] Client smoke test passed");
		} else {
			System.out.println("[JOID] " + failures.size() + " client smoke checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
		Minecraft.getMinecraft().func_152344_a(Minecraft.getMinecraft()::shutdown);
	}

	private static void joinWorld() {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(600);
		while (!MinecraftBackend.inst().isRegistered() || !(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after 600 s waiting for JOID on the main menu");
			}

			ClientSmokeTest.sleep(500);
		}

		ClientSmokeTest.window = ClientSmokeTest.xdotool("search", "--sync", "--limit", "1", "--name", "^Minecraft").trim();
		ClientSmokeTest.xdotool("windowfocus", "--sync", ClientSmokeTest.window);
		ClientSmokeTest.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.gameSettings.pauseOnLostFocus = false;
			minecraft.launchIntegratedServer("joid-smoke", "joid-smoke", new WorldSettings(0L, WorldSettings.GameType.SURVIVAL, false, false, WorldType.FLAT));
		});
		ClientSmokeTest.waitFor("the singleplayer world", () -> Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu), 600);
		ClientSmokeTest.runOnClient(() -> Minecraft.getMinecraft().displayGuiScreen(null));
		ClientSmokeTest.waitFor("the game without screen", () -> Minecraft.getMinecraft().currentScreen == null, 30);
		ClientSmokeTest.sleep(5000);
	}

	private static void verifyBridges(final List<String> failures) {
		final List<String> missing = ClientSmokeTest.computeOnClient(() -> {
			final List<String> names = new ArrayList<>();
			if (!(BridgeHandler.RENDER.get() instanceof GlRenderBridge)) {
				names.add("render bridge");
			}

			if (!(BridgeHandler.WINDOW.get() instanceof MinecraftWindowBridge)) {
				names.add("window bridge");
			}

			if (!(BridgeHandler.AUDIO.get() instanceof AlAudioBridge) || ((AlAudioBridge) BridgeHandler.AUDIO.get()).isOwnContext()) {
				names.add("audio bridge on the OpenAL context of the game");
			}

			if (!(BridgeHandler.THREAD.get() instanceof MinecraftThreadBridge)) {
				names.add("thread bridge");
			}

			final List<UIBridge> bridges = BridgeHandler.UI.getBridgeList();
			if (bridges.size() != 2 || !(bridges.get(0) instanceof ScreenUIBridge) || !(bridges.get(1) instanceof OverlayUIBridge)) {
				names.add("UI bridges [ScreenUIBridge, OverlayUIBridge]");
			}

			if (!JOIDEnv.isDevMode() || !JOIDEnv.isDemoMode()) {
				names.add("dev and demo modes");
			}
			return names;
		});
		if (!missing.isEmpty()) {
			failures.add("JOID started without " + String.join(", ", missing));
		}

		final int[] stencil = ClientSmokeTest.computeOnClient(() -> {
			final Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
			framebuffer.bindFramebuffer(false);
			try {
				return new int[] {MinecraftForgeClient.getStencilBits(), GL11.glGetInteger(GL11.GL_STENCIL_BITS)};
			} finally {
				framebuffer.unbindFramebuffer();
			}
		});
		if (stencil[0] != 8 || stencil[1] != 8) {
			failures.add("the screen of the game has " + stencil[0] + " stencil bits for Forge and " + stencil[1] + " in its framebuffer instead of 8");
		}
	}

	private static void verifyAudio(final List<String> failures, final String moment) {
		final String failure = ClientSmokeTest.computeOnClient(() -> {
			final GameSettings settings = Minecraft.getMinecraft().gameSettings;
			if (MinecraftAudioGain.inst().apply(0.5F, SoundCategory.MUSIC) != 0.5F * settings.getSoundLevel(SoundCategory.MASTER) * settings.getSoundLevel(SoundCategory.MUSIC)) {
				return "the gain of a music source does not follow the master and music volumes of the game";
			}

			final Object context = Lwjgl2AlBinding.inst().getCurrentContext();
			final IAudioSource source = BridgeHandler.AUDIO.get().createSource(44100, 1);
			try {
				source.write(new short[4410]);
				source.play();
				final int error = AL10.alGetError();
				if (error != AL10.AL_NO_ERROR || context == null || Lwjgl2AlBinding.inst().getCurrentContext() != context) {
					return "a JOID source plays with the OpenAL error " + error + " or out of the context of the game";
				}
			} finally {
				source.delete();
			}
			return null;
		});
		if (failure != null) {
			failures.add(failure + " " + moment);
		}
	}

	private static void verifyKeyBind(final List<String> failures) {
		if (!ClientSmokeTest.computeOnClient(() -> Arrays.stream(Minecraft.getMinecraft().gameSettings.keyBindings).anyMatch(binding -> "key.joid.demo".equals(binding.getKeyDescription()) && KeyResolver.resolve(binding) == Key.J))) {
			failures.add("the key binding key.joid.demo is not registered on J");
		}

		ClientSmokeTest.press("j");
		ClientSmokeTest.waitFor("UIDemoMinecraftChoice opened by the key J", () -> Minecraft.getMinecraft().currentScreen instanceof UIScreen && JOID.isOpen(UIDemoMinecraftChoice.class), 30);
		ClientSmokeTest.sleep(2000);
	}

	private static void verifyDebugKey(final List<String> failures) {
		ClientSmokeTest.press("F3");
		ClientSmokeTest.sleep(1000);
		if (!ClientSmokeTest.computeOnClient(() -> JOID.getUi(UIDemoMinecraftChoice.class).getNodeList().contains(JOID.getUi(UIDemoMinecraftChoice.class).getDevNode()))) {
			failures.add("F3 in UIDemoMinecraftChoice does not show the dev panel of JOID");
		}

		if (ClientSmokeTest.computeOnClient(() -> Minecraft.getMinecraft().gameSettings.showDebugInfo)) {
			failures.add("F3 consumed by UIDemoMinecraftChoice also shows the debug screen of Minecraft");
		}

		ClientSmokeTest.press("F3");
		ClientSmokeTest.press("Escape");
		ClientSmokeTest.waitFor("the game without screen", () -> Minecraft.getMinecraft().currentScreen == null, 30);
		ClientSmokeTest.press("F3");
		ClientSmokeTest.sleep(1000);
		if (!ClientSmokeTest.computeOnClient(() -> Minecraft.getMinecraft().gameSettings.showDebugInfo)) {
			failures.add("F3 without a JOID UI does not show the debug screen of Minecraft");
		}

		ClientSmokeTest.press("F3");
		ClientSmokeTest.press("j");
		ClientSmokeTest.waitFor("UIDemoMinecraftChoice opened again by the key J", () -> Minecraft.getMinecraft().currentScreen instanceof UIScreen && JOID.isOpen(UIDemoMinecraftChoice.class), 30);
		ClientSmokeTest.sleep(2000);
	}

	private static void verifyDemoMenu(final List<String> failures) {
		ClientSmokeTest.click(ClientSmokeTest.entry("UIDemoSimple"));
		ClientSmokeTest.waitFor("UIDemoSimple opened by a click on UIDemoMinecraftChoice", () -> JOID.isOpen(UIDemoSimple.class), 30);
		ClientSmokeTest.sleep(2000);
		ClientSmokeTest.press("Escape");
		ClientSmokeTest.waitFor("UIDemoMinecraftChoice opened again by Escape on UIDemoSimple", () -> !JOID.isOpen(UIDemoSimple.class) && JOID.isOpen(UIDemoMinecraftChoice.class), 30);
		ClientSmokeTest.sleep(2000);
		if (ClientSmokeTest.computeOnClient(() -> JOID.isOpen(UIDemoChoice.class))) {
			failures.add("Escape on UIDemoSimple opens UIDemoChoice instead of UIDemoMinecraftChoice");
		}
	}

	private static void verifyMinecraftDemo(final List<String> failures) {
		ClientSmokeTest.click(ClientSmokeTest.entry("UIDemoMinecraft"));
		ClientSmokeTest.waitFor("UIDemoMinecraft opened by a click on UIDemoMinecraftChoice", () -> JOID.isOpen(UIDemoMinecraft.class), 30);
		ClientSmokeTest.sleep(2000);
		final int[] card = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoMinecraft.class).getNodeList().get(0).getChild(0, RectNode.class)));
		ClientSmokeTest.expectColor(failures, "first card of UIDemoMinecraft", ClientSmokeTest.capture("joid-smoke-minecraft"), card, 0xDDDDDD, true);
		if (ClientSmokeTest.computeOnClient(() -> Minecraft.getMinecraft().isGamePaused())) {
			failures.add("UIDemoMinecraft, declared with pause = false, pauses the game");
		}

		ClientSmokeTest.press("Escape");
		ClientSmokeTest.waitFor("UIDemoMinecraftChoice opened again by Escape on UIDemoMinecraft", () -> !JOID.isOpen(UIDemoMinecraft.class) && JOID.isOpen(UIDemoMinecraftChoice.class), 30);
		ClientSmokeTest.sleep(2000);
	}

	private static void verifyTooltip(final List<String> failures) {
		final int[] entry = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoMinecraftChoice.class).getNodeList().get(0).getChild(0, RectNode.class)));
		final int scale = ClientSmokeTest.computeOnClient(() -> new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight).getScaleFactor());
		final int mouseX = (entry[0] / scale + 6) * scale;
		final int mouseY = ((entry[1] + entry[3]) / scale - 2) * scale;
		final int[] region = {mouseX + 10 * scale, mouseY - 12 * scale, 2 * scale, 8 * scale};

		ClientSmokeTest.move(4, 4);
		final int before = ClientSmokeTest.countTooltipPixels(ClientSmokeTest.capture("joid-smoke-no-tooltip"), region);
		ClientSmokeTest.move(mouseX, mouseY);
		ClientSmokeTest.sleep(1000);
		final int after = ClientSmokeTest.countTooltipPixels(ClientSmokeTest.capture("joid-smoke-tooltip"), region);
		if (before != 0 || after * 2 < region[2] * region[3]) {
			failures.add("the tooltip of a demo entry is not drawn by the game: " + before + " tooltip pixels before the hover and " + after + " of " + region[2] * region[3] + " after, at " + Arrays.toString(region));
		}
		ClientSmokeTest.move(4, 4);
	}

	private static void verifyOverlays(final List<String> failures) {
		ClientSmokeTest.click(ClientSmokeTest.entry("UIDemoOverlayLayer"));
		ClientSmokeTest.waitFor("the demo overlays opened by a click on UIDemoMinecraftChoice", () -> JOID.isOpen(UIDemoOverlayLayer.Hotbar.class) && JOID.isOpen(UIDemoOverlayLayer.Always.class), 30);
		ClientSmokeTest.press("Escape");
		ClientSmokeTest.waitFor("the game without screen", () -> Minecraft.getMinecraft().currentScreen == null, 30);
		ClientSmokeTest.sleep(2000);

		final int[] hotbar = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getNodeList().get(0)));
		final int[] always = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Always.class).getNodeList().get(0)));
		final int[] crosshair = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Crosshair.class).getNodeList().get(0)));
		final int[] experience = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Experience.class).getNodeList().get(0)));
		final BufferedImage hud = ClientSmokeTest.capture("joid-smoke-overlay");
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar", hud, hotbar, 0xDDDDDD, true);
		ClientSmokeTest.expectColor(failures, "overlay always shown", hud, always, 0xDDDDDD, true);
		ClientSmokeTest.expectColor(failures, "overlay below the crosshair", hud, crosshair, 0xDDDDDD, true);
		ClientSmokeTest.expectColor(failures, "overlay in place of the experience bar", hud, experience, 0xDDDDDD, true);
		ClientSmokeTest.press("F1");
		ClientSmokeTest.sleep(2000);
		final BufferedImage hidden = ClientSmokeTest.capture("joid-smoke-overlay-hidden-gui");
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar with the GUI hidden", hidden, hotbar, 0xDDDDDD, false);
		ClientSmokeTest.expectColor(failures, "overlay always shown with the GUI hidden", hidden, always, 0xDDDDDD, true);
		ClientSmokeTest.press("F1");
		ClientSmokeTest.sleep(1000);
	}

	private static void verifyScreenInput(final List<String> failures) {
		ClientSmokeTest.computeOnServer(player -> {
			player.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond, 3));
			return null;
		});
		ClientSmokeTest.sleep(1000);
		ClientSmokeTest.press("e");
		ClientSmokeTest.waitFor("the inventory", () -> Minecraft.getMinecraft().currentScreen instanceof GuiInventory, 30);
		ClientSmokeTest.sleep(2000);

		final int[] slot = ClientSmokeTest.computeOnClient(() -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			final GuiInventory screen = (GuiInventory) minecraft.currentScreen;
			final Slot hotbar = screen.inventorySlots.getSlot(36);
			final int scale = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor();
			return new int[] {((int) ClientSmokeTest.field(screen, "guiLeft") + hotbar.xDisplayPosition) * scale, ((int) ClientSmokeTest.field(screen, "guiTop") + hotbar.yDisplayPosition) * scale, 16 * scale, 16 * scale};
		});
		final int[] button = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Interactive.class).getNodeList().get(0).getChild(0, RectNode.class)));
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, "a click on 3 diamonds of the inventory", 3);
		ClientSmokeTest.click(button);
		final int clicks = ClientSmokeTest.computeOnClient(() -> (int) ClientSmokeTest.field(JOID.getUi(UIDemoOverlayLayer.Interactive.class), "clicks"));
		if (clicks != 1) {
			failures.add("a click on the interactive overlay over the inventory gives " + clicks + " overlay clicks instead of 1");
		}

		ClientSmokeTest.expectCarried(failures, "a click on the interactive overlay carrying 3 diamonds", 3);
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, "a click back on the slot of the inventory", 0);
		ClientSmokeTest.capture("joid-smoke-inventory");
		ClientSmokeTest.press("Escape");
		ClientSmokeTest.waitFor("the inventory to close", () -> Minecraft.getMinecraft().currentScreen == null, 30);
		ClientSmokeTest.sleep(1000);
	}

	private static void verifyReload(final List<String> failures) {
		ClientSmokeTest.xdotool("keydown", "F3");
		ClientSmokeTest.waitFrames(5);
		ClientSmokeTest.press("t");
		ClientSmokeTest.xdotool("keyup", "F3");
		ClientSmokeTest.waitFrames(5);
		ClientSmokeTest.sleep(3000);
		ClientSmokeTest.press("F3");
		ClientSmokeTest.sleep(1000);

		final int[] hotbar = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getNodeList().get(0)));
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar after F3+T", ClientSmokeTest.capture("joid-smoke-reloaded"), hotbar, 0xDDDDDD, true);
		ClientSmokeTest.verifyAudio(failures, "after F3+T, which recreates the OpenAL context of the game");
	}

	private static void verifyWarnings(final List<String> failures) {
		final List<String> frames = ClientSmokeTest.WARNINGS.stream().filter(warning -> warning.contains("ms to draw a frame")).collect(Collectors.toList());
		final List<String> others = ClientSmokeTest.WARNINGS.stream().filter(warning -> !frames.contains(warning)).collect(Collectors.toList());
		System.out.println("[JOID] " + frames.size() + " frame time warnings of the dev mode, llvmpipe draws on the CPU");
		if (!others.isEmpty()) {
			failures.add("JOID warned during the smoke test:" + System.lineSeparator() + String.join(System.lineSeparator(), others));
		}
	}

	private static void expectCarried(final List<String> failures, final String label, final int count) {
		final int client = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.count(Minecraft.getMinecraft().thePlayer.inventory.getItemStack()));
		final int server = ClientSmokeTest.computeOnServer(player -> ClientSmokeTest.count(player.inventory.getItemStack()));
		if (client != count || server != count) {
			failures.add(label + " carries " + server + " on the server and " + client + " on the client instead of " + count);
		}
	}

	private static void expectColor(final List<String> failures, final String label, final BufferedImage image, final int[] bounds, final int color, final boolean same) {
		final int actual = image.getRGB(bounds[0] + 4, bounds[1] + 4) & 0xFFFFFF;
		if (actual == color != same) {
			failures.add(label + " at " + Arrays.toString(bounds) + " is " + Integer.toHexString(actual) + (same ? " instead of " : " like ") + Integer.toHexString(color));
		}
	}

	private static int countTooltipPixels(final BufferedImage image, final int[] region) {
		int count = 0;
		for (int y = region[1]; y < region[1] + region[3]; y++) {
			for (int x = region[0]; x < region[0] + region[2]; x++) {
				final int rgb = image.getRGB(x, y);
				final int red = rgb >> 16 & 0xFF;
				final int green = rgb >> 8 & 0xFF;
				final int blue = rgb & 0xFF;
				if (red < 0x40 && blue < 0x40 && green < red && green < blue) {
					count++;
				}
			}
		}
		return count;
	}

	private static BufferedImage capture(final String name) {
		ClientSmokeTest.waitFrames(2);
		ClientSmokeTest.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			ScreenShotHelper.saveScreenshot(minecraft.mcDataDir, name + ".png", minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
		});
		try {
			return ImageIO.read(new File(new File(Minecraft.getMinecraft().mcDataDir, "screenshots"), name + ".png"));
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + name, exception);
		}
	}

	private static int[] entry(final String label) {
		return ClientSmokeTest.computeOnClient(() -> {
			final int index = ClientSmokeTest.entries().stream().map(DemoEntry::getLabel).collect(Collectors.toList()).indexOf(label);
			return ClientSmokeTest.bounds(JOID.getUi(UIDemoMinecraftChoice.class).getNodeList().get(0).getChild(index, RectNode.class));
		});
	}

	private static List<DemoEntry> entries() {
		return (List<DemoEntry>) ClientSmokeTest.field(null, UIDemoMinecraftChoice.class, "LIST");
	}

	private static void click(final int[] bounds) {
		ClientSmokeTest.xdotool("mousemove", "--window", ClientSmokeTest.window, String.valueOf(bounds[0] + bounds[2] / 2), String.valueOf(bounds[1] + bounds[3] / 2), "click", "1");
		ClientSmokeTest.waitFrames(5);
		ClientSmokeTest.sleep(500);
	}

	private static void move(final int x, final int y) {
		ClientSmokeTest.xdotool("mousemove", "--window", ClientSmokeTest.window, String.valueOf(x), String.valueOf(y));
		ClientSmokeTest.waitFrames(5);
	}

	private static void press(final String key) {
		ClientSmokeTest.xdotool("key", key);
		ClientSmokeTest.waitFrames(5);
		ClientSmokeTest.sleep(300);
	}

	private static String xdotool(final String... arguments) {
		final List<String> command = new ArrayList<>();
		command.add("xdotool");
		command.addAll(Arrays.asList(arguments));
		try {
			final Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
			try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
				final String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));
				if (!process.waitFor(30, TimeUnit.SECONDS) || process.exitValue() != 0) {
					throw new AssertionError("xdotool " + String.join(" ", arguments) + " failed: " + output);
				}
				return output;
			}
		} catch (final IOException exception) {
			throw new AssertionError("xdotool cannot run", exception);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while running xdotool", exception);
		}
	}

	private static <T> T computeOnServer(final Function<EntityPlayerMP, T> function) {
		final CompletableFuture<T> future = new CompletableFuture<>();
		ClientSmokeTest.SERVER_TASKS.add(() -> {
			try {
				future.complete(function.apply((EntityPlayerMP) MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0)));
			} catch (final RuntimeException exception) {
				future.completeExceptionally(exception);
			}
		});
		return ClientSmokeTest.await(future, "computing on the server");
	}

	private static void runOnClient(final Runnable runnable) {
		ClientSmokeTest.await(Minecraft.getMinecraft().func_152344_a(runnable), "running on the client");
	}

	private static <T> T computeOnClient(final Callable<T> callable) {
		return (T) ClientSmokeTest.await(Minecraft.getMinecraft().func_152343_a(callable), "computing on the client");
	}

	private static void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!ClientSmokeTest.computeOnClient(condition::getAsBoolean)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + ClientSmokeTest.computeOnClient(() -> Minecraft.getMinecraft().currentScreen));
			}

			ClientSmokeTest.sleep(250);
		}
	}

	private static void waitFrames(final int frames) {
		final long target = ClientSmokeTest.FRAMES.get() + frames;
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
		while (ClientSmokeTest.FRAMES.get() < target) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after 60 s waiting for " + frames + " frames");
			}

			ClientSmokeTest.sleep(20);
		}
	}

	private static <T> T await(final Future<T> future, final String label) {
		try {
			return future.get(120, TimeUnit.SECONDS);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while " + label, exception);
		} catch (final ExecutionException exception) {
			throw new AssertionError("Failed while " + label + ": " + exception.getCause(), exception.getCause());
		} catch (final TimeoutException exception) {
			throw new AssertionError("Timed out while " + label, exception);
		}
	}

	private static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while sleeping", exception);
		}
	}

	private static int count(final ItemStack stack) {
		return stack == null ? 0 : stack.stackSize;
	}

	private static int[] bounds(final Node node) {
		final UI ui = node.getUi();
		return new int[] {(int) Math.round(ui.getView().toScreenX(node.getAbsoluteX())), (int) Math.round(ui.getView().toScreenY(node.getAbsoluteY())), (int) Math.round(ui.getView().toScreenWidth(node.getWidth())), (int) Math.round(ui.getView().toScreenHeight(node.getHeight()))};
	}

	private static Object field(final Object target, final String name) {
		return ClientSmokeTest.field(target, target.getClass(), name);
	}

	private static Object field(final Object target, final Class<?> type, final String name) {
		for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
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
		throw new AssertionError("No field " + name + " in " + type.getName());
	}

	private static final class WarningStream extends PrintStream {

		private final PrintStream target;

		private WarningStream(final PrintStream target) {
			super(target, true);
			this.target = target;
		}

		@Override
		public void println(final String line) {
			if (line != null && line.startsWith("[JOID]")) {
				ClientSmokeTest.WARNINGS.add(line);
			}
			this.target.println(line);
		}

	}

}