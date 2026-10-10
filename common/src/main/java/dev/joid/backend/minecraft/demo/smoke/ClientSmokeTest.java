package dev.joid.backend.minecraft.demo.smoke;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIBridge;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.ScreenUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoContainer;
import dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.demo.ui.DemoEntry;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

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
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientSmokeTest {

	public static void start() {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.pauseOnLostFocus = false;
		minecraft.options.onboardAccessibility = false;
		final Thread thread = new Thread(ClientSmokeTest::run, "JOID Client Smoke Test");
		thread.setDaemon(true);
		thread.start();
	}

	private static void run() {
		final List<String> failures = new ArrayList<>();
		try {
			ClientSmokeTest.joinWorld();
			ClientSmokeTest.verifyBridges(failures);
			ClientSmokeTest.verifyKeyBind(failures);
			ClientSmokeTest.verifyOverlays(failures);
			ClientSmokeTest.verifyScreenInput(failures);
			ClientSmokeTest.verifyReload(failures);
			ClientSmokeTest.verifyContainer(failures);
		} catch (final Throwable throwable) {
			failures.add("the smoke test stopped on " + throwable);
		}

		final Minecraft minecraft = Minecraft.getInstance();
		if (failures.isEmpty()) {
			System.out.println("[JOID] Client smoke test passed");
			minecraft.execute(minecraft::stop);
			return;
		}

		final AssertionError error = new AssertionError(failures.size() + " client smoke checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		minecraft.execute(() -> minecraft.delayCrash(CrashReport.forThrowable(error, "JOID client smoke test")));
	}

	private static void joinWorld() {
		ClientSmokeTest.waitFor("the end of the game loading", () -> ClientSmokeTest.isLoaded() || Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		if (ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen)) {
			ClientSmokeTest.runOnClient(() -> Minecraft.getInstance().gui.screen().onClose());
			ClientSmokeTest.waitFor("the end of the accessibility onboarding", () -> ClientSmokeTest.isLoaded() || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		}

		if (ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen)) {
			ClientSmokeTest.runOnClient(() -> ClientSmokeTest.pressButton(Minecraft.getInstance().gui.screen(), "selectWorld.backupJoinSkipButton"));
		}

		ClientSmokeTest.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWWindowFocusCallback callback = GLFW.glfwSetWindowFocusCallback(window, null);
			GLFW.glfwSetWindowFocusCallback(window, callback);
			callback.invoke(window, true);
		});
		if (ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().level == null && !Minecraft.getInstance().getLevelSource().levelExists("joid-smoke"))) {
			ClientSmokeTest.runOnClient(() -> CreateWorldScreen.openFresh(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(new TitleScreen())));
			ClientSmokeTest.waitFor("the world creation screen", () -> Minecraft.getInstance().gui.screen() instanceof CreateWorldScreen, 120);
			ClientSmokeTest.runOnClient(() -> {
				final CreateWorldScreen screen = (CreateWorldScreen) Minecraft.getInstance().gui.screen();
				final WorldCreationUiState state = screen.getUiState();
				state.setName("joid-smoke");
				state.setWorldType(new WorldCreationUiState.WorldTypeEntry(state.getSettings().worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)));
				state.setSeed("1");
				state.setGenerateStructures(false);
				state.getGameRules().set(GameRules.ADVANCE_TIME, false, null);
				state.getGameRules().set(GameRules.SPAWN_MOBS, false, null);
				ClientSmokeTest.pressButton(screen, "selectWorld.create");
			});
			ClientSmokeTest.waitFor("the world creation", () -> Minecraft.getInstance().level != null || Minecraft.getInstance().gui.screen() instanceof ConfirmScreen, 600);
			if (ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof ConfirmScreen)) {
				ClientSmokeTest.runOnClient(() -> ((BooleanConsumer) ClientSmokeTest.field(Minecraft.getInstance().gui.screen(), "callback")).accept(true));
			}
		}

		ClientSmokeTest.waitFor("the singleplayer world", () -> Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && Minecraft.getInstance().gui.screen() == null && Minecraft.getInstance().gui.overlay() == null, 600);
		ClientSmokeTest.sleep(5000);
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

	private static void verifyBridges(final List<String> failures) {
		final List<String> missing = ClientSmokeTest.computeOnClient(() -> {
			final List<String> names = new ArrayList<>();
			if (!(BridgeHandler.RENDER.get() instanceof MinecraftRenderBridge)) {
				names.add("render bridge");
			}

			if (!(BridgeHandler.WINDOW.get() instanceof MinecraftWindowBridge)) {
				names.add("window bridge");
			}

			if (BridgeHandler.AUDIO.get() == null || BridgeHandler.THREAD.get() == null) {
				names.add("audio or thread bridge");
			}

			if (BridgeHandler.UI.getBridge(ScreenUIBridge.class) == null || BridgeHandler.UI.getBridge(ContainerUIBridge.class) == null || BridgeHandler.UI.getBridge(OverlayUIBridge.class) == null) {
				names.add("UI bridges");
			}

			if (!JOID.inst().isDemoMode()) {
				names.add("demo mode");
			}
			return names;
		});
		if (!missing.isEmpty()) {
			failures.add("JOID started without " + String.join(", ", missing));
		}
	}

	private static void verifyKeyBind(final List<String> failures) {
		if (!ClientSmokeTest.computeOnClient(() -> Arrays.stream(Minecraft.getInstance().options.keyMappings).anyMatch(mapping -> mapping.getName().equals("key.joid.demo") && KeyResolver.resolve(mapping) == Key.J))) {
			failures.add("the key mapping key.joid.demo is not registered on J");
		}

		ClientSmokeTest.press(GLFW.GLFW_KEY_J);
		ClientSmokeTest.waitFor("UIDemoChoice opened by the key J", () -> Minecraft.getInstance().gui.screen() instanceof UIScreen && JOID.isOpen(UIDemoChoice.class), 30);
		ClientSmokeTest.sleep(2000);
	}

	private static void verifyOverlays(final List<String> failures) {
		ClientSmokeTest.click(ClientSmokeTest.entry("UIDemoOverlayLayer"));
		ClientSmokeTest.waitFor("the demo overlays opened by a click on UIDemoChoice", () -> JOID.isOpen(UIDemoOverlayLayer.Hotbar.class) && JOID.isOpen(UIDemoOverlayLayer.Always.class), 30);
		ClientSmokeTest.press(GLFW.GLFW_KEY_ESCAPE);
		ClientSmokeTest.waitFor("the game without screen", () -> Minecraft.getInstance().gui.screen() == null, 30);
		ClientSmokeTest.sleep(2000);

		final int[] hotbar = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getNodeList().ordered().getFirst()));
		final int[] always = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Always.class).getNodeList().ordered().getFirst()));
		final BufferedImage hud = ClientSmokeTest.capture("joid-smoke-overlay");
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar", hud, hotbar, 0xDDDDDD, true);
		ClientSmokeTest.expectColor(failures, "overlay always shown", hud, always, 0xDDDDDD, true);
		ClientSmokeTest.press(GLFW.GLFW_KEY_F1);
		ClientSmokeTest.sleep(2000);
		final BufferedImage hidden = ClientSmokeTest.capture("joid-smoke-overlay-hidden-gui");
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar with the GUI hidden", hidden, hotbar, 0xDDDDDD, false);
		ClientSmokeTest.expectColor(failures, "overlay always shown with the GUI hidden", hidden, always, 0xDDDDDD, true);
		ClientSmokeTest.press(GLFW.GLFW_KEY_F1);
		ClientSmokeTest.sleep(1000);
	}

	private static void verifyScreenInput(final List<String> failures) {
		final MinecraftServer server = ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer());
		ClientSmokeTest.computeOnServer(server, player -> {
			player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
			return null;
		});
		ClientSmokeTest.sleep(1000);
		ClientSmokeTest.press(GLFW.GLFW_KEY_E);
		ClientSmokeTest.waitFor("the inventory", () -> Minecraft.getInstance().gui.screen() instanceof InventoryScreen, 30);
		ClientSmokeTest.sleep(2000);

		final int[] slot = ClientSmokeTest.computeOnClient(() -> {
			final Screen screen = Minecraft.getInstance().gui.screen();
			final Slot hotbar = ((InventoryScreen) screen).getMenu().getSlot(36);
			final int scale = Minecraft.getInstance().getWindow().getGuiScale();
			return new int[] {((int) ClientSmokeTest.field(screen, "leftPos") + hotbar.x) * scale, ((int) ClientSmokeTest.field(screen, "topPos") + hotbar.y) * scale, 16 * scale, 16 * scale};
		});
		final int[] button = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Interactive.class).getNodeList().ordered().getFirst().getChild(0, RectNode.class)));
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, server, "a click on 3 diamonds of the inventory", 3);
		ClientSmokeTest.click(button);
		final int clicks = ClientSmokeTest.computeOnClient(() -> (int) ClientSmokeTest.field(JOID.getUi(UIDemoOverlayLayer.Interactive.class), "clicks"));
		if (clicks != 1) {
			failures.add("a click on the interactive overlay over the inventory gives " + clicks + " overlay clicks instead of 1");
		}

		ClientSmokeTest.expectCarried(failures, server, "a click on the interactive overlay carrying 3 diamonds", 3);
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, server, "a click back on the slot of the inventory", 0);
		ClientSmokeTest.capture("joid-smoke-inventory");
		ClientSmokeTest.press(GLFW.GLFW_KEY_ESCAPE);
		ClientSmokeTest.waitFor("the inventory to close", () -> Minecraft.getInstance().gui.screen() == null, 30);
		ClientSmokeTest.sleep(1000);
	}

	private static void verifyReload(final List<String> failures) {
		final PrintStream err = System.err;
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setErr(new PrintStream(output, true));
		try {
			ClientSmokeTest.key(GLFW.GLFW_KEY_F3, GLFW.GLFW_PRESS);
			ClientSmokeTest.press(GLFW.GLFW_KEY_T);
			ClientSmokeTest.key(GLFW.GLFW_KEY_F3, GLFW.GLFW_RELEASE);
			ClientSmokeTest.waitFor("the reload started by F3+T", () -> Minecraft.getInstance().gui.overlay() != null, 30);
			ClientSmokeTest.waitFor("the end of the reload started by F3+T", () -> Minecraft.getInstance().gui.overlay() == null, 600);
			ClientSmokeTest.sleep(3000);
		} finally {
			System.setErr(err);
		}

		err.print(output);
		final List<String> errors = output.toString().lines().filter(line -> line.contains("Exception") || line.contains("Error") || line.startsWith("	at ")).toList();
		if (!errors.isEmpty()) {
			failures.add("F3+T printed errors: " + String.join(System.lineSeparator(), errors));
		}

		final int[] hotbar = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoOverlayLayer.Hotbar.class).getNodeList().ordered().getFirst()));
		ClientSmokeTest.expectColor(failures, "overlay above the hotbar after F3+T", ClientSmokeTest.capture("joid-smoke-reloaded"), hotbar, 0xDDDDDD, true);
	}

	private static void verifyContainer(final List<String> failures) {
		final MinecraftServer server = ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer());
		ClientSmokeTest.press(GLFW.GLFW_KEY_J);
		ClientSmokeTest.waitFor("UIDemoChoice opened by the key J", () -> JOID.isOpen(UIDemoChoice.class), 30);
		ClientSmokeTest.sleep(2000);
		ClientSmokeTest.click(ClientSmokeTest.entry("UIDemoContainer"));
		ClientSmokeTest.waitFor("the demo container opened by its payload", () -> Minecraft.getInstance().gui.screen() instanceof ContainerUIScreen && JOID.isOpen(UIDemoContainer.class), 60);
		ClientSmokeTest.sleep(2000);
		if (!ClientSmokeTest.computeOnServer(server, player -> player.containerMenu instanceof DemoContainer)) {
			failures.add("the payload of UIDemoContainer did not open the demo container on the server");
			return;
		}

		ClientSmokeTest.computeOnServer(server, player -> {
			player.containerMenu.getSlot(0).set(new ItemStack(Items.EMERALD, 10));
			return null;
		});
		ClientSmokeTest.sleep(1000);
		final int[] slot = ClientSmokeTest.computeOnClient(() -> ClientSmokeTest.bounds(JOID.getUi(UIDemoContainer.class).getNodeList().ordered().getFirst().getChild(0, SlotNode.class)));
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, server, "a click on 10 emeralds of the demo container", 10);
		ClientSmokeTest.click(slot);
		ClientSmokeTest.expectCarried(failures, server, "a click back on the slot of the demo container", 0);
		final int count = ClientSmokeTest.computeOnServer(server, player -> player.containerMenu.getSlot(0).getItem().getCount());
		if (count != 10) {
			failures.add("the first slot of the demo container holds " + count + " emeralds on the server instead of 10");
		}

		ClientSmokeTest.capture("joid-smoke-container");
		ClientSmokeTest.press(GLFW.GLFW_KEY_ESCAPE);
		ClientSmokeTest.waitFor("the demo container to close", () -> Minecraft.getInstance().gui.screen() == null, 30);
		ClientSmokeTest.sleep(1000);
		if (!ClientSmokeTest.computeOnServer(server, player -> player.containerMenu == player.inventoryMenu)) {
			failures.add("Escape does not close the demo container on the server");
		}
	}

	private static void expectCarried(final List<String> failures, final MinecraftServer server, final String label, final int count) {
		final int client = ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().player.containerMenu.getCarried().getCount());
		final int actual = ClientSmokeTest.computeOnServer(server, player -> player.containerMenu.getCarried().getCount());
		if (client != count || actual != count) {
			failures.add(label + " carries " + actual + " on the server and " + client + " on the client instead of " + count);
		}
	}

	private static void expectColor(final List<String> failures, final String label, final BufferedImage image, final int[] bounds, final int color, final boolean same) {
		final int actual = image.getRGB(bounds[0] + 4, bounds[1] + 4) & 0xFFFFFF;
		if (actual == color != same) {
			failures.add(label + " at " + Arrays.toString(bounds) + " is " + Integer.toHexString(actual) + (same ? " instead of " : " like ") + Integer.toHexString(color));
		}
	}

	private static BufferedImage capture(final String name) {
		final CompletableFuture<Path> future = new CompletableFuture<>();
		ClientSmokeTest.runOnClient(() -> Screenshot.takeScreenshot(Minecraft.getInstance().gameRenderer.mainRenderTarget(), image -> {
			try (image) {
				final Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots").resolve(name + ".png");
				Files.createDirectories(path.getParent());
				image.writeToFile(path);
				future.complete(path);
			} catch (final IOException exception) {
				future.completeExceptionally(exception);
			}
		}));
		try {
			return ImageIO.read(ClientSmokeTest.await(future, "capturing " + name).toFile());
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + name, exception);
		}
	}

	private static int[] entry(final String label) {
		return ClientSmokeTest.computeOnClient(() -> {
			final int index = UIDemoChoice.LIST.stream().map(DemoEntry::getLabel).toList().indexOf(label);
			return ClientSmokeTest.bounds(JOID.getUi(UIDemoChoice.class).getNodeList().ordered().getFirst().getChild(index, RectNode.class));
		});
	}

	private static int[] bounds(final Node node) {
		return new int[] {(int) Math.round(node.getUi().getView().toScreenX(node.getAbsoluteX())), (int) Math.round(node.getUi().getView().toScreenY(node.getAbsoluteY())), (int) Math.round(node.getUi().getView().toScreenWidth(node.getWidth())), (int) Math.round(node.getUi().getView().toScreenHeight(node.getHeight()))};
	}

	private static void click(final int[] bounds) {
		ClientSmokeTest.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWCursorPosCallback callback = GLFW.glfwSetCursorPosCallback(window, null);
			GLFW.glfwSetCursorPosCallback(window, callback);
			callback.invoke(window, bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D);
		});
		ClientSmokeTest.sleep(1000);
		ClientSmokeTest.mouse(GLFW.GLFW_PRESS);
		ClientSmokeTest.mouse(GLFW.GLFW_RELEASE);
		ClientSmokeTest.sleep(1000);
	}

	private static void mouse(final int action) {
		ClientSmokeTest.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWMouseButtonCallback callback = GLFW.glfwSetMouseButtonCallback(window, null);
			GLFW.glfwSetMouseButtonCallback(window, callback);
			callback.invoke(window, GLFW.GLFW_MOUSE_BUTTON_LEFT, action, 0);
		});
		ClientSmokeTest.sleep(200);
	}

	private static void press(final int key) {
		ClientSmokeTest.key(key, GLFW.GLFW_PRESS);
		ClientSmokeTest.key(key, GLFW.GLFW_RELEASE);
	}

	private static void key(final int key, final int action) {
		ClientSmokeTest.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWKeyCallback callback = GLFW.glfwSetKeyCallback(window, null);
			GLFW.glfwSetKeyCallback(window, callback);
			callback.invoke(window, key, GLFW.glfwGetKeyScancode(key), action, 0);
		});
		ClientSmokeTest.sleep(200);
	}

	private static void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!ClientSmokeTest.computeOnClient(condition::getAsBoolean)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + ClientSmokeTest.computeOnClient(() -> Minecraft.getInstance().gui.screen()));
			}

			ClientSmokeTest.sleep(250);
		}
	}

	private static void runOnClient(final Runnable runnable) {
		ClientSmokeTest.await(Minecraft.getInstance().submit(runnable), "running on the client");
	}

	private static <T> T computeOnClient(final Supplier<T> supplier) {
		return ClientSmokeTest.await(Minecraft.getInstance().submit(supplier), "computing on the client");
	}

	private static <T> T computeOnServer(final MinecraftServer server, final Function<ServerPlayer, T> function) {
		return ClientSmokeTest.await(server.submit(() -> function.apply(server.getPlayerList().getPlayers().getFirst())), "computing on the server");
	}

	private static <T> T await(final CompletableFuture<T> future, final String label) {
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