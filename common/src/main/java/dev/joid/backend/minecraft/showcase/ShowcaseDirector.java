package dev.joid.backend.minecraft.showcase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.lib.JOID;
import dev.joid.lib.ui.node.Node;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseDirector {

	private static final Queue<Runnable> SERVER_TASKS = new ConcurrentLinkedQueue<>();

	private static String  clip;
	private static String  window;
	private static int     captures;
	private static int     clipFrame;
	private static double  mouseX;
	private static double  mouseY;
	private static boolean pressed;
	private static boolean pointer;

	public static void start() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.gameSettings.pauseOnLostFocus = false;
		minecraft.gameSettings.showInventoryAchievementHint = false;
		minecraft.gameSettings.limitFramerate = 260;
		minecraft.gameSettings.renderDistanceChunks = 16;
		minecraft.gameSettings.viewBobbing = false;
		minecraft.gameSettings.guiScale = 4;
		minecraft.gameSettings.fovSetting = 70F;
		minecraft.gameSettings.difficulty = EnumDifficulty.NORMAL;
		FMLCommonHandler.instance().bus().register(new ShowcaseDirector());
		final Thread thread = new Thread(ShowcaseDirector::run, "JOID Showcase Director");
		thread.setDaemon(true);
		thread.start();
	}

	@SubscribeEvent
	public void serverTick(final ServerTickEvent event) {
		if (event.phase != Phase.START) {
			return;
		}

		for (Runnable task = ShowcaseDirector.SERVER_TASKS.poll(); task != null; task = ShowcaseDirector.SERVER_TASKS.poll()) {
			task.run();
		}
	}

	public static void frames(final int count) {
		for (int frame = 0; frame < count; frame++) {
			ShowcaseDirector.step(null);
		}
	}

	public static void capture(final String name) {
		ShowcaseDirector.step(name);
	}

	public static void begin(final String name) {
		if (Arrays.asList(System.getProperty("joid.showcase.video", "").split(",")).contains(name)) {
			ShowcaseDirector.clip      = name;
			ShowcaseDirector.clipFrame = 0;
		}
	}

	public static void end() {
		ShowcaseDirector.clip = null;
	}

	public static void glide(final Node node, final int count) {
		final double[] bounds = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(node));
		ShowcaseDirector.glide(bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D, count);
	}

	public static void glide(final double x, final double y, final int count) {
		final double fromX = ShowcaseDirector.pointer ? ShowcaseDirector.mouseX : x;
		final double fromY = ShowcaseDirector.pointer ? ShowcaseDirector.mouseY : y;
		for (int frame = 1; frame <= count; frame++) {
			final double progress = (double) frame / count;
			final double eased = progress * progress * (3D - 2D * progress);
			ShowcaseDirector.move(fromX + (x - fromX) * eased, fromY + (y - fromY) * eased);
			ShowcaseDirector.step(null);
		}
	}

	public static void animate(final int count, final DoubleConsumer action) {
		for (int frame = 1; frame <= count; frame++) {
			final double progress = (double) frame / count;
			final double eased = progress * progress * (3D - 2D * progress);
			ShowcaseDirector.runOnClient(() -> action.accept(eased));
			ShowcaseDirector.step(null);
		}
	}

	public static void click() {
		ShowcaseDirector.press();
		ShowcaseDirector.frames(4);
		ShowcaseDirector.release();
	}

	public static void move(final Node node) {
		final double[] bounds = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(node));
		ShowcaseDirector.move(bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D);
	}

	public static void move(final double x, final double y) {
		ShowcaseDirector.mouseX  = x;
		ShowcaseDirector.mouseY  = y;
		ShowcaseDirector.pointer = true;
		ShowcaseDirector.xdotool("mousemove", "--window", ShowcaseDirector.window, String.valueOf((int) Math.round(x)), String.valueOf((int) Math.round(y)));
	}

	public static void hidePointer() {
		ShowcaseDirector.pointer = false;
	}

	public static void press() {
		ShowcaseDirector.pressed = true;
		ShowcaseDirector.xdotool("mousedown", "1");
	}

	public static void release() {
		ShowcaseDirector.pressed = false;
		ShowcaseDirector.xdotool("mouseup", "1");
	}

	public static void key(final String key) {
		ShowcaseDirector.xdotool("key", key);
	}

	public static void type(final String text, final int count) {
		for (final char character : text.toCharArray()) {
			ShowcaseDirector.xdotool("type", "--delay", "0", String.valueOf(character));
			ShowcaseDirector.frames(count);
		}
	}

	public static void scroll(final double notches) {
		ShowcaseDirector.xdotool("click", notches < 0D ? "5" : "4");
	}

	public static void command(final String command) {
		ShowcaseDirector.computeOnServer(player -> {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
			return null;
		});
	}

	public static void runOnServer(final Consumer<EntityPlayerMP> consumer) {
		ShowcaseDirector.computeOnServer(player -> {
			consumer.accept(player);
			return null;
		});
	}

	public static <T> T computeOnServer(final Function<EntityPlayerMP, T> function) {
		final CompletableFuture<T> future = new CompletableFuture<>();
		ShowcaseDirector.SERVER_TASKS.add(() -> {
			try {
				future.complete(function.apply((EntityPlayerMP) MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0)));
			} catch (final RuntimeException exception) {
				future.completeExceptionally(exception);
			}
		});
		return ShowcaseDirector.await(future, "computing on the server");
	}

	public static void runOnClient(final Runnable runnable) {
		ShowcaseDirector.await(Minecraft.getMinecraft().func_152344_a(runnable), "running on the client");
	}

	public static <T> T computeOnClient(final Callable<T> callable) {
		return (T) ShowcaseDirector.await(Minecraft.getMinecraft().func_152343_a(callable), "computing on the client");
	}

	public static void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!ShowcaseDirector.computeOnClient(condition::getAsBoolean)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + ShowcaseDirector.computeOnClient(() -> Minecraft.getMinecraft().currentScreen));
			}

			ShowcaseDirector.sleep(250);
		}
	}

	public static void waitForWorld() {
		ShowcaseDirector.sleep(20000);
	}

	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while sleeping", exception);
		}
	}

	public static double[] bounds(final Node node) {
		return new double[] {node.getUi().getView().toScreenX(node.getAbsoluteX()), node.getUi().getView().toScreenY(node.getAbsoluteY()), node.getUi().getView().toScreenWidth(node.getWidth()), node.getUi().getView().toScreenHeight(node.getHeight())};
	}

	private static void run() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		try {
			ShowcaseDirector.joinWorld();
			for (final String scene : System.getProperty("joid.showcase").split(",")) {
				ShowcaseScene.valueOf(scene.trim().toUpperCase(Locale.ROOT)).play();
			}
		} catch (final Throwable throwable) {
			ShowcaseClock.release();
			throwable.printStackTrace();
			System.out.println("[JOID] The showcase stopped after " + ShowcaseDirector.captures + " captures");
			minecraft.func_152344_a(minecraft::shutdown);
			return;
		}

		System.out.println("[JOID] Showcase done: " + ShowcaseDirector.captures + " captures");
		ShowcaseClock.release();
		minecraft.func_152344_a(minecraft::shutdown);
	}

	private static void joinWorld() {
		ShowcaseDirector.waitFor("JOID on the main menu", () -> MinecraftBackend.inst().isRegistered() && Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu, 600);
		ShowcaseDirector.window = ShowcaseDirector.xdotool("search", "--sync", "--limit", "1", "--name", "^Minecraft").trim();
		ShowcaseDirector.xdotool("windowfocus", "--sync", ShowcaseDirector.window);
		ShowcaseDirector.xdotool("windowmove", "--sync", ShowcaseDirector.window, "0", "0");
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().launchIntegratedServer("joid-showcase", "joid-showcase", new WorldSettings(Long.parseLong(System.getProperty("joid.showcase.seed", "1700")), WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT)));
		ShowcaseDirector.waitFor("the singleplayer world", () -> Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null, 900);
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().displayGuiScreen(null));
		ShowcaseDirector.waitFor("the game without screen", () -> Minecraft.getMinecraft().currentScreen == null, 60);
		ShowcaseDirector.runOnClient(() -> {
			Display.setTitle("Minecraft");
			JOID.open(new ShowcaseFrameUI());
		});
	}

	private static void step(final String capture) {
		final long frame = ShowcaseFrameUI.getFrame();
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(120);
		while (ShowcaseFrameUI.getFrame() <= frame) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after 120 s waiting for a frame");
			}

			ShowcaseDirector.sleep(2);
		}

		ShowcaseDirector.runOnClient(() -> {
			if (capture != null) {
				ShowcaseDirector.write(capture);
			}

			if (ShowcaseDirector.clip != null) {
				ShowcaseDirector.write(ShowcaseDirector.clip + "/" + String.format(Locale.ROOT, "f%04d", ShowcaseDirector.clipFrame++));
			}

			ShowcaseClock.advance(1_000_000_000L / 30L);
		});
	}

	private static void write(final String name) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final File path = new File(System.getProperty("joid.showcase.output", new File(minecraft.mcDataDir, "showcase").getPath()), name);
		try {
			Files.createDirectories(path.getParentFile().toPath());
			Files.write(new File(path.getPath() + ".json").toPath(), String.format(Locale.ROOT, "{\"mouse\": [%.2f, %.2f], \"pressed\": %b, \"visible\": %b}", ShowcaseDirector.mouseX, ShowcaseDirector.mouseY, ShowcaseDirector.pressed, ShowcaseDirector.pointer).getBytes(StandardCharsets.UTF_8));
			final String command = System.getProperty("joid.showcase.capture");
			if (command == null) {
				ScreenShotHelper.saveScreenshot(path.getParentFile(), path.getName() + ".png", minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
			} else {
				final int code = new ProcessBuilder(command, path.getPath()).inheritIO().start().waitFor();
				if (code != 0) {
					throw new AssertionError("The capture command of " + name + " ended with the code " + code);
				}
			}
		} catch (final IOException exception) {
			throw new AssertionError("Unable to capture " + name, exception);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while capturing " + name, exception);
		}

		ShowcaseDirector.captures++;
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

	private static <T> T await(final Future<T> future, final String label) {
		try {
			return future.get(300, TimeUnit.SECONDS);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while " + label, exception);
		} catch (final ExecutionException exception) {
			throw new AssertionError("Failed while " + label + ": " + exception.getCause(), exception.getCause());
		} catch (final TimeoutException exception) {
			throw new AssertionError("Timed out while " + label, exception);
		}
	}

}