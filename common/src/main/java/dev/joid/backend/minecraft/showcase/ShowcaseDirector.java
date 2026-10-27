package dev.joid.backend.minecraft.showcase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;

import dev.joid.lib.JOID;
import dev.joid.lib.ui.node.Node;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.gamerules.GameRules;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseDirector {

	private static String  clip;
	private static int     captures;
	private static int     clipFrame;
	private static double  mouseX;
	private static double  mouseY;
	private static boolean pressed;
	private static boolean pointer;

	public static void start() {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.pauseOnLostFocus = false;
		minecraft.options.onboardAccessibility = false;
		minecraft.options.tutorialStep = TutorialSteps.NONE;
		minecraft.options.framerateLimit().set(260);
		minecraft.options.renderDistance().set(16);
		minecraft.options.bobView().set(false);
		minecraft.options.guiScale().set(4);
		minecraft.options.fov().set(70);
		final Thread thread = new Thread(ShowcaseDirector::run, "JOID Showcase Director");
		thread.setDaemon(true);
		thread.start();
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
		if (List.of(System.getProperty("joid.showcase.video", "").split(",")).contains(name)) {
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
		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWCursorPosCallback callback = GLFW.glfwSetCursorPosCallback(window, null);
			GLFW.glfwSetCursorPosCallback(window, callback);
			callback.invoke(window, x, y);
		});
	}

	public static void hidePointer() {
		ShowcaseDirector.pointer = false;
	}

	public static void press() {
		ShowcaseDirector.pressed = true;
		ShowcaseDirector.mouse(GLFW.GLFW_PRESS);
	}

	public static void release() {
		ShowcaseDirector.pressed = false;
		ShowcaseDirector.mouse(GLFW.GLFW_RELEASE);
	}

	public static void key(final int key) {
		ShowcaseDirector.keyEvent(key, GLFW.GLFW_PRESS);
		ShowcaseDirector.keyEvent(key, GLFW.GLFW_RELEASE);
	}

	public static void type(final String text, final int count) {
		for (final char character : text.toCharArray()) {
			ShowcaseDirector.runOnClient(() -> {
				final long window = Minecraft.getInstance().getWindow().handle();
				final GLFWCharCallback callback = GLFW.glfwSetCharCallback(window, null);
				GLFW.glfwSetCharCallback(window, callback);
				callback.invoke(window, character);
			});
			ShowcaseDirector.frames(count);
		}
	}

	public static void scroll(final double notches) {
		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWScrollCallback callback = GLFW.glfwSetScrollCallback(window, null);
			GLFW.glfwSetScrollCallback(window, callback);
			callback.invoke(window, 0D, notches);
		});
	}

	public static void command(final String command) {
		final MinecraftServer server = ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer());
		ShowcaseDirector.await(server.submit(() -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), command)), "running /" + command);
	}

	public static void runOnServer(final Consumer<ServerPlayer> consumer) {
		ShowcaseDirector.computeOnServer(player -> {
			consumer.accept(player);
			return null;
		});
	}

	public static <T> T computeOnServer(final Function<ServerPlayer, T> function) {
		final MinecraftServer server = ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer());
		return ShowcaseDirector.await(server.submit(() -> function.apply(server.getPlayerList().getPlayers().getFirst())), "computing on the server");
	}

	public static void runOnClient(final Runnable runnable) {
		ShowcaseDirector.await(Minecraft.getInstance().submit(runnable), "running on the client");
	}

	public static <T> T computeOnClient(final Supplier<T> supplier) {
		return ShowcaseDirector.await(Minecraft.getInstance().submit(supplier), "computing on the client");
	}

	public static void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!ShowcaseDirector.computeOnClient(condition::getAsBoolean)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().gui.screen()));
			}

			ShowcaseDirector.sleep(250);
		}
	}

	public static void waitForWorld() {
		ShowcaseDirector.sleep(3000);
		ShowcaseDirector.waitFor("the chunks around the player", () -> Minecraft.getInstance().levelRenderer.hasRenderedAllSections(), 900);
		ShowcaseDirector.sleep(3000);
		ShowcaseDirector.waitFor("the chunks around the player", () -> Minecraft.getInstance().levelRenderer.hasRenderedAllSections(), 900);
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
		final Minecraft minecraft = Minecraft.getInstance();
		try {
			ShowcaseDirector.joinWorld();
			for (final String scene : System.getProperty("joid.showcase").split(",")) {
				ShowcaseScene.valueOf(scene.trim().toUpperCase(Locale.ROOT)).play();
			}
		} catch (final Throwable throwable) {
			ShowcaseClock.release();
			final AssertionError error = new AssertionError("The showcase stopped after " + ShowcaseDirector.captures + " captures", throwable);
			minecraft.execute(() -> minecraft.delayCrash(CrashReport.forThrowable(error, "JOID showcase")));
			return;
		}

		System.out.println("[JOID] Showcase done: " + ShowcaseDirector.captures + " captures");
		ShowcaseClock.release();
		minecraft.execute(minecraft::stop);
	}

	private static void joinWorld() {
		ShowcaseDirector.waitFor("the end of the game loading", () -> ShowcaseDirector.isLoaded() || Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		if (ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen)) {
			ShowcaseDirector.runOnClient(() -> Minecraft.getInstance().gui.screen().onClose());
			ShowcaseDirector.waitFor("the end of the accessibility onboarding", () -> ShowcaseDirector.isLoaded() || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		}

		if (ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen)) {
			ShowcaseDirector.runOnClient(() -> ShowcaseDirector.pressButton(Minecraft.getInstance().gui.screen(), "selectWorld.backupJoinSkipButton"));
		}

		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWWindowFocusCallback callback = GLFW.glfwSetWindowFocusCallback(window, null);
			GLFW.glfwSetWindowFocusCallback(window, callback);
			callback.invoke(window, true);
		});
		if (ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().level == null && !Minecraft.getInstance().getLevelSource().levelExists("joid-showcase"))) {
			ShowcaseDirector.runOnClient(() -> CreateWorldScreen.openFresh(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(new TitleScreen())));
			ShowcaseDirector.waitFor("the world creation screen", () -> Minecraft.getInstance().gui.screen() instanceof CreateWorldScreen, 120);
			ShowcaseDirector.runOnClient(() -> {
				final CreateWorldScreen screen = (CreateWorldScreen) Minecraft.getInstance().gui.screen();
				final WorldCreationUiState state = screen.getUiState();
				state.setName("joid-showcase");
				state.setSeed(System.getProperty("joid.showcase.seed", "1700"));
				state.setDifficulty(Difficulty.NORMAL);
				state.getGameRules().set(GameRules.ADVANCE_TIME, false, null);
				state.getGameRules().set(GameRules.ADVANCE_WEATHER, false, null);
				state.getGameRules().set(GameRules.SPAWN_MOBS, false, null);
				state.getGameRules().set(GameRules.LOCATOR_BAR, false, null);
				state.getGameRules().set(GameRules.RANDOM_TICK_SPEED, 0, null);
				state.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, null);
				state.getGameRules().set(GameRules.SHOW_ADVANCEMENT_MESSAGES, false, null);
				ShowcaseDirector.pressButton(screen, "selectWorld.create");
			});
			ShowcaseDirector.waitFor("the world creation", () -> Minecraft.getInstance().level != null || Minecraft.getInstance().gui.screen() instanceof ConfirmScreen, 900);
			if (ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof ConfirmScreen)) {
				ShowcaseDirector.runOnClient(() -> ((BooleanConsumer) ShowcaseDirector.field(Minecraft.getInstance().gui.screen(), "callback")).accept(true));
			}
		}

		ShowcaseDirector.waitFor("the singleplayer world", () -> Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && Minecraft.getInstance().gui.screen() == null && Minecraft.getInstance().gui.overlay() == null, 900);
		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final int[] left = new int[1];
			final int[] top = new int[1];
			GLFW.glfwGetWindowFrameSize(window, left, top, new int[1], new int[1]);
			GLFW.glfwSetWindowPos(window, left[0], top[0]);
			Minecraft.getInstance().getWindow().setTitle("Minecraft");
			JOID.open(new ShowcaseFrameUI());
		});
	}

	private static boolean isLoaded() {
		final Minecraft minecraft = Minecraft.getInstance();
		return minecraft.gui.overlay() == null && (minecraft.level != null || minecraft.gui.screen() instanceof TitleScreen || minecraft.gui.screen() instanceof DisconnectedScreen);
	}

	private static void pressButton(final Screen screen, final String translationKey) {
		final String message = Component.translatable(translationKey).getString();
		for (final GuiEventListener child : screen.children()) {
			if (child instanceof final Button button && button.getMessage().getString().equals(message)) {
				button.onPress(new MouseButtonInfo(GLFW.GLFW_KEY_UNKNOWN, 0));
				return;
			}
		}
		throw new AssertionError("The screen " + screen + " has no button " + translationKey);
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

			Minecraft.getInstance().getWindow().setTitle("Minecraft");
			ShowcaseClock.advance(1_000_000_000L / 30L);
		});
	}

	private static void write(final String name) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Path path = Path.of(System.getProperty("joid.showcase.output", minecraft.gameDirectory.toPath().resolve("showcase").toString())).resolve(name);
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path.resolveSibling(path.getFileName() + ".json"), String.format(Locale.ROOT, "{\"mouse\": [%.2f, %.2f], \"pressed\": %b, \"visible\": %b}", ShowcaseDirector.mouseX, ShowcaseDirector.mouseY, ShowcaseDirector.pressed, ShowcaseDirector.pointer), StandardCharsets.UTF_8);
			final String command = System.getProperty("joid.showcase.capture");
			if (command == null) {
				Screenshot.takeScreenshot(minecraft.gameRenderer.mainRenderTarget(), image -> {
					try (image) {
						image.writeToFile(path.resolveSibling(path.getFileName() + ".png"));
					} catch (final IOException exception) {
						throw new AssertionError("Unable to write the capture " + name, exception);
					}
				});
			} else {
				final int code = new ProcessBuilder(command, path.toString()).inheritIO().start().waitFor();
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

	private static void mouse(final int action) {
		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWMouseButtonCallback callback = GLFW.glfwSetMouseButtonCallback(window, null);
			GLFW.glfwSetMouseButtonCallback(window, callback);
			callback.invoke(window, GLFW.GLFW_MOUSE_BUTTON_LEFT, action, 0);
		});
	}

	private static void keyEvent(final int key, final int action) {
		ShowcaseDirector.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWKeyCallback callback = GLFW.glfwSetKeyCallback(window, null);
			GLFW.glfwSetKeyCallback(window, callback);
			callback.invoke(window, key, GLFW.glfwGetKeyScancode(key), action, 0);
		});
	}

	private static <T> T await(final CompletableFuture<T> future, final String label) {
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
		throw new AssertionError("No field " + name + " in " + target.getClass().getName());
	}

}