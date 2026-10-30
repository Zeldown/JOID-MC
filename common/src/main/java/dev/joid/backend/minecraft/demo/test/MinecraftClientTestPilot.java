package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import dev.joid.backend.minecraft.demo.snapshot.MinecraftBitmapFontContractTest;
import dev.joid.backend.minecraft.demo.snapshot.MinecraftBorrowedTextureContractTest;
import dev.joid.backend.minecraft.demo.snapshot.MinecraftRenderBridgeContractTest;
import dev.joid.backend.minecraft.demo.snapshot.MinecraftStateGuardContractTest;
import dev.joid.backend.minecraft.demo.test.object.ClientTestBlock;
import dev.joid.backend.minecraft.demo.test.object.ClientTestEntity;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.backend.minecraft.demo.test.object.ClientTestTexture;
import dev.joid.backend.minecraft.demo.test.object.ClientTestVolume;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.draw.raster.FeatureDrawableRaster;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.base.lwjgl3.openal.Lwjgl3AlBinding;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.draw.raster.DrawableRaster;
import dev.joid.lib.font.IFont;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.test.snapshot.ISnapshotBackend;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public final class MinecraftClientTestPilot implements IClientTestPilot {

	private static final AtomicInteger RELOADS = new AtomicInteger();

	private final Set<Key>    heldKeys = new HashSet<>();
	private final List<String> sounds  = new ArrayList<>();

	private String             graphicsBackend;
	private SoundEventListener soundListener;
	private DemoContainer      demoContainer;

	public static void countReload() {
		MinecraftClientTestPilot.RELOADS.incrementAndGet();
	}

	@Override
	public String getVersion() {
		return "26.2";
	}

	@Override
	public String getLoader() {
		return System.getProperty("joid.loader", "unknown");
	}

	@Override
	public String getGraphicsBackend() {
		if (this.graphicsBackend == null) {
			this.graphicsBackend = this.computeOnClient(() -> RenderSystem.getDevice().getDeviceInfo().backendName().toLowerCase(Locale.ROOT), 120);
		}
		return this.graphicsBackend;
	}

	@Override
	public Map<ClientTestScenario, String> getExclusions() {
		final Map<ClientTestScenario, String> exclusions = new EnumMap<>(ClientTestScenario.class);
		exclusions.put(ClientTestScenario.STENCIL, "Blaze3D gives no stencil buffer to the screen, JOID emulates it");
		return exclusions;
	}

	@Override
	public List<Class<?>> getContractTests() {
		return List.of(MinecraftRenderBridgeContractTest.class, MinecraftStateGuardContractTest.class, MinecraftBorrowedTextureContractTest.class, MinecraftBitmapFontContractTest.class);
	}

	@Override
	public void runOnClient(final Runnable runnable) {
		MinecraftClientTestPilot.await(Minecraft.getInstance().submit(runnable), "running on the client", 120);
	}

	@Override
	public <T> T computeOnClient(final Callable<T> callable, final int seconds) {
		return MinecraftClientTestPilot.await(Minecraft.getInstance().submit(() -> {
			try {
				return callable.call();
			} catch (final Exception exception) {
				throw new IllegalStateException(exception);
			}
		}), "computing on the client", seconds);
	}

	@Override
	public void stop(final boolean passed) {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(minecraft::stop);
	}

	@Override
	public void moveMouse(final double x, final double y) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWCursorPosCallback callback = GLFW.glfwSetCursorPosCallback(window, null);
			GLFW.glfwSetCursorPosCallback(window, callback);
			callback.invoke(window, x, y);
		});
	}

	@Override
	public void pressMouse(final boolean press) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWMouseButtonCallback callback = GLFW.glfwSetMouseButtonCallback(window, null);
			GLFW.glfwSetMouseButtonCallback(window, callback);
			callback.invoke(window, GLFW.GLFW_MOUSE_BUTTON_LEFT, press ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, this.getModifiers());
		});
	}

	@Override
	public void scroll(final double amount) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWScrollCallback callback = GLFW.glfwSetScrollCallback(window, null);
			GLFW.glfwSetScrollCallback(window, callback);
			callback.invoke(window, 0D, amount);
		});
	}

	@Override
	public void pressKey(final Key key, final boolean press) {
		if (press) {
			this.heldKeys.add(key);
		} else {
			this.heldKeys.remove(key);
		}

		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().handle();
			final GLFWKeyCallback callback = GLFW.glfwSetKeyCallback(window, null);
			GLFW.glfwSetKeyCallback(window, callback);
			final int code = GlfwKeys.getCode(key);
			callback.invoke(window, code, GLFW.glfwGetKeyScancode(code), press ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, this.getModifiers());
		});
	}

	@Override
	public BufferedImage capture(final String name) {
		final CompletableFuture<Path> future = new CompletableFuture<>();
		this.runOnClient(() -> Screenshot.takeScreenshot(Minecraft.getInstance().gameRenderer.mainRenderTarget(), image -> {
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
			return ImageIO.read(MinecraftClientTestPilot.await(future, "capturing " + name, 120).toFile());
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + name, exception);
		}
	}

	@Override
	public void reachTitleScreen() {
		this.waitFor("the end of the game loading", () -> MinecraftClientTestPilot.isLoaded() || Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		if (this.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof AccessibilityOnboardingScreen, 120)) {
			this.runOnClient(() -> Minecraft.getInstance().gui.screen().onClose());
			this.waitFor("the end of the accessibility onboarding", () -> MinecraftClientTestPilot.isLoaded() || Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 600);
		}

		if (this.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof BackupConfirmScreen, 120)) {
			this.runOnClient(() -> MinecraftClientTestPilot.pressButton(Minecraft.getInstance().gui.screen(), "selectWorld.backupJoinSkipButton"));
		}

		this.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getInstance();
			final long window = minecraft.getWindow().handle();
			final GLFWWindowFocusCallback callback = GLFW.glfwSetWindowFocusCallback(window, null);
			GLFW.glfwSetWindowFocusCallback(window, callback);
			callback.invoke(window, true);
			if (minecraft.level == null && !(minecraft.gui.screen() instanceof TitleScreen)) {
				minecraft.gui.setScreen(new TitleScreen());
			}
		});
		this.waitFor("the title screen", () -> Minecraft.getInstance().gui.screen() instanceof TitleScreen, 120);
		this.sleep(2000L);
	}

	@Override
	public void joinWorld() {
		if (this.computeOnClient(() -> Minecraft.getInstance().level == null && !Minecraft.getInstance().getLevelSource().levelExists("joid-test"), 120)) {
			this.runOnClient(() -> CreateWorldScreen.openFresh(Minecraft.getInstance(), () -> Minecraft.getInstance().gui.setScreen(new TitleScreen())));
			this.waitFor("the world creation screen", () -> Minecraft.getInstance().gui.screen() instanceof CreateWorldScreen, 120);
			this.runOnClient(() -> {
				final CreateWorldScreen screen = (CreateWorldScreen) Minecraft.getInstance().gui.screen();
				final WorldCreationUiState state = screen.getUiState();
				state.setName("joid-test");
				state.setWorldType(new WorldCreationUiState.WorldTypeEntry(state.getSettings().worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)));
				state.setSeed("1");
				state.setGenerateStructures(false);
				state.getGameRules().set(GameRules.ADVANCE_TIME, false, null);
				state.getGameRules().set(GameRules.SPAWN_MOBS, false, null);
				MinecraftClientTestPilot.pressButton(screen, "selectWorld.create");
			});
			this.waitFor("the world creation", () -> Minecraft.getInstance().level != null || Minecraft.getInstance().gui.screen() instanceof ConfirmScreen, 600);
			if (this.computeOnClient(() -> Minecraft.getInstance().gui.screen() instanceof ConfirmScreen, 120)) {
				this.runOnClient(() -> ((BooleanConsumer) MinecraftClientTestPilot.field(Minecraft.getInstance().gui.screen(), "callback")).accept(true));
			}
		}

		this.waitFor("the singleplayer world", () -> Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && Minecraft.getInstance().gui.screen() == null && Minecraft.getInstance().gui.overlay() == null, 600);
		this.sleep(5000L);
	}

	@Override
	public void clearInventory() {
		this.computeOnServer(player -> {
			player.getInventory().clearContent();
			player.containerMenu.setCarried(ItemStack.EMPTY);
			player.containerMenu.broadcastChanges();
			return null;
		});
	}

	@Override
	public void giveItem(final int slot, final ClientTestItem item, final int count) {
		this.computeOnServer(player -> {
			player.getInventory().setItem(slot, new ItemStack(MinecraftClientTestPilot.item(item), count));
			return null;
		});
	}

	@Override
	public void setContainerSlot(final int slot, final ClientTestItem item, final int count) {
		this.computeOnServer(player -> {
			player.containerMenu.getSlot(slot).set(new ItemStack(MinecraftClientTestPilot.item(item), count));
			return null;
		});
		this.sleep(500L);
	}

	@Override
	public int[] readContainer(final boolean server) {
		if (server) {
			return this.computeOnServer(player -> MinecraftClientTestPilot.describe(player.containerMenu));
		}
		return this.computeOnClient(() -> MinecraftClientTestPilot.describe(Minecraft.getInstance().player.containerMenu), 120);
	}

	@Override
	public boolean isDemoContainerOpen() {
		return this.computeOnServer(player -> player.containerMenu instanceof DemoContainer);
	}

	@Override
	public boolean canOpenDemoContainer(final int level) {
		return this.computeOnServer(player -> {
			final MinecraftServer server = player.level().getServer();
			final ServerPlayer stranger = new ServerPlayer(server, server.overworld(), new GameProfile(UUID.randomUUID(), "JOIDStranger"), ClientInformation.createDefault());
			if (level > 0) {
				server.getPlayerList().op(stranger.nameAndId(), Optional.of(level == 1 ? LevelBasedPermissionSet.MODERATOR : LevelBasedPermissionSet.GAMEMASTER), Optional.empty());
			}

			try {
				return OpenDemoContainerPayloadHandler.canOpen(stranger);
			} finally {
				server.getPlayerList().deop(stranger.nameAndId());
			}
		});
	}

	@Override
	public int openDemoContainer() {
		return this.computeOnServer(player -> {
			player.getInventory().clearContent();
			OpenDemoContainerPayloadHandler.handle(OpenDemoContainerPayload.INSTANCE, player);
			if (!(player.containerMenu instanceof final DemoContainer container)) {
				return -1;
			}

			container.getStorage().clearContent();
			this.demoContainer = container;
			return container.slots.size();
		});
	}

	@Override
	public void quickMoveDemoContainer(final int slot) {
		this.computeOnServer(player -> player.containerMenu.quickMoveStack(player, slot));
	}

	@Override
	public void clickDemoContainer(final int slot, final int button) {
		this.computeOnServer(player -> {
			player.containerMenu.clicked(slot, button, ContainerInput.PICKUP, player);
			return null;
		});
	}

	@Override
	public void closeDemoContainer() {
		this.computeOnServer(player -> {
			player.closeContainer();
			return null;
		});
	}

	@Override
	public int countInventory(final ClientTestItem item) {
		return this.computeOnServer(player -> player.getInventory().countItem(MinecraftClientTestPilot.item(item)));
	}

	@Override
	public int countDemoStorage(final int slot) {
		return this.computeOnServer(_ -> this.demoContainer.getStorage().getItem(slot).getCount());
	}

	@Override
	public File getGameDirectory() {
		return Minecraft.getInstance().gameDirectory;
	}

	@Override
	public boolean isBackendRegistered() {
		return MinecraftBackend.inst().isRegistered();
	}

	@Override
	public boolean isTitleScreen() {
		return Minecraft.getInstance().gui.screen() instanceof TitleScreen;
	}

	@Override
	public boolean isScreenOpen() {
		return Minecraft.getInstance().gui.screen() != null;
	}

	@Override
	public boolean isJoidScreen() {
		return Minecraft.getInstance().gui.screen() instanceof UIScreen;
	}

	@Override
	public boolean isContainerScreen() {
		return Minecraft.getInstance().gui.screen() instanceof ContainerUIScreen;
	}

	@Override
	public boolean isInventoryScreen() {
		return Minecraft.getInstance().gui.screen() instanceof InventoryScreen;
	}

	@Override
	public boolean isReloading() {
		return Minecraft.getInstance().gui.overlay() != null;
	}

	@Override
	public int countReloads() {
		return MinecraftClientTestPilot.RELOADS.get();
	}

	@Override
	public Object getScreen() {
		return Minecraft.getInstance().gui.screen();
	}

	@Override
	public void closeScreen() {
		Minecraft.getInstance().gui.setScreen(null);
	}

	@Override
	public boolean isPauseScreen() {
		return Minecraft.getInstance().gui.screen().isPauseScreen();
	}

	@Override
	public boolean isGamePaused() {
		return Minecraft.getInstance().isPaused();
	}

	@Override
	public String getScreenTitle() {
		return Minecraft.getInstance().gui.screen().getTitle().getString();
	}

	@Override
	public boolean isDebugScreenShown() {
		return Minecraft.getInstance().debugEntries.isOverlayVisible();
	}

	@Override
	public int getGuiScale() {
		return Minecraft.getInstance().getWindow().getGuiScale();
	}

	@Override
	public void applyGuiScale(final int guiScale) {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.guiScale().set(guiScale);
		minecraft.resizeGui();
	}

	@Override
	public void reloadResources() {
		Minecraft.getInstance().reloadResourcePacks();
	}

	@Override
	public void openControls() {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.gui.setScreen(new KeyBindsScreen(minecraft.gui.screen(), minecraft.options));
	}

	@Override
	public List<String> getControlsLabels() {
		final KeyBindsList list = (KeyBindsList) MinecraftClientTestPilot.field(Minecraft.getInstance().gui.screen(), "keyBindsList");
		list.setScrollAmount(list.maxScrollAmount());
		final List<String> labels = new ArrayList<>();
		for (final KeyBindsList.Entry entry : list.children()) {
			labels.add((entry instanceof KeyBindsList.CategoryEntry ? ((FocusableTextWidget) MinecraftClientTestPilot.field(entry, "categoryName")).getMessage() : (Component) MinecraftClientTestPilot.field(entry, "name")).getString());
		}
		return labels;
	}

	@Override
	public Object getKeyMapping(final String name) {
		for (final KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
			if (mapping.getName().equals(name)) {
				return mapping;
			}
		}
		throw new AssertionError("Minecraft has no key mapping " + name);
	}

	@Override
	public String translate(final String language, final String key) {
		return ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), List.of(language), false).getOrDefault(key);
	}

	@Override
	public Object createRedChat(final String text) {
		return Component.literal(text).withStyle(ChatFormatting.RED);
	}

	@Override
	public int measureText(final IFont font, final String text) {
		return Minecraft.getInstance().font.width(Component.literal(text).withStyle(style -> style.withFont(new FontDescription.Resource(((MinecraftFont) font).getId()))));
	}

	@Override
	public int getStencilBits() {
		throw new UnsupportedOperationException("Blaze3D gives no stencil buffer to the screen");
	}

	@Override
	public Object getAudioContext() {
		return Lwjgl3AlBinding.inst().getCurrentContext();
	}

	@Override
	public int getAudioError() {
		return AL10.alGetError();
	}

	@Override
	public float getAudioGain(final int source) {
		return AL10.alGetSourcef(source, AL10.AL_GAIN);
	}

	@Override
	public double getVolume(final ClientTestVolume volume) {
		return Minecraft.getInstance().options.getSoundSourceOptionInstance(MinecraftClientTestPilot.source(volume)).get();
	}

	@Override
	public void setVolume(final ClientTestVolume volume, final double value) {
		Minecraft.getInstance().options.getSoundSourceOptionInstance(MinecraftClientTestPilot.source(volume)).set(value);
	}

	@Override
	public void setSubtitles(final boolean subtitles) {
		Minecraft.getInstance().options.showSubtitles().set(subtitles);
	}

	@Override
	public void playInterfaceSound() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1F));
	}

	@Override
	public void startSoundCapture() {
		this.sounds.clear();
		this.soundListener = (sound, _, _) -> this.sounds.add(sound.getIdentifier().toString());
		Minecraft.getInstance().getSoundManager().addListener(this.soundListener);
	}

	@Override
	public List<String> stopSoundCapture() {
		Minecraft.getInstance().getSoundManager().removeListener(this.soundListener);
		return List.copyOf(this.sounds);
	}

	@Override
	public String getNoteBlockSound() {
		return SoundEvents.NOTE_BLOCK_PLING.value().location().toString();
	}

	@Override
	public Node createItemNode(final double x, final double y, final double size, final ClientTestItem item, final boolean glint) {
		final ItemNode node = ItemNode.create(x, y, size, size).stack(new ItemStack(MinecraftClientTestPilot.item(item)));
		return glint ? node.glint(true) : node;
	}

	@Override
	public Node createBlockNode(final double x, final double y, final double size, final ClientTestBlock block) {
		return BlockNode.create(x, y, size, size).block(MinecraftClientTestPilot.block(block));
	}

	@Override
	public Node createEntityNode(final double x, final double y, final double width, final double height, final ClientTestEntity entity, final double yaw, final double pitch) {
		final EntityNode node = EntityNode.create(x, y, width, height).rotationYaw(yaw).rotationPitch(pitch);
		return switch (entity) {
			case PIG -> node.type(EntityTypes.PIG);
			case ZOMBIE -> node.type(EntityTypes.ZOMBIE);
			case PLAYER_PROFILE -> node.profile(new GameProfile(new UUID(0L, 15L), "Steve"));
			case PLAYER_SKIN -> node.skin(PlayerSkin.insecure(new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("joid", "demo/skin"), Identifier.fromNamespaceAndPath("joid", "demo/textures/skin.png")), null, null, PlayerModelType.WIDE));
		};
	}

	@Override
	public boolean hasEntity(final Node node) {
		return ((EntityNode) node).getEntity() != null;
	}

	@Override
	public boolean isOnCooldown(final ClientTestItem item) {
		return Minecraft.getInstance().player.getCooldowns().isOnCooldown(new ItemStack(MinecraftClientTestPilot.item(item)));
	}

	@Override
	public Resource createSprite(final ClientTestItem item) {
		return Resource.of(Sheets.ITEMS_MAPPER.defaultNamespaceApply(BuiltInRegistries.ITEM.getKey(MinecraftClientTestPilot.item(item)).getPath()));
	}

	@Override
	public Node[] createNestedRasterNodes(final boolean feature) {
		final ItemStack apple = new ItemStack(Items.APPLE);
		final ItemStack stone = new ItemStack(Items.STONE);
		final double appleSize = feature ? 32D : 64D;
		final RectNode nested = RectNode.create(400, 300, 256, 256).color(Color.rgb(0x202020)).onDraw(event -> {
			final Node node = event.getNode();
			if (feature) {
				final ItemStackRenderState state = MinecraftClientTestPilot.itemState(stone);
				DrawUtils.RASTER.drawRaster(node.getX(), node.getY(), node.getWidth(), node.getHeight(), (width, height) -> FeatureDrawableRaster.of(Lighting.Entry.ITEMS_3D, (pose, collector) -> {
					state.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
					DrawItem.inst().drawItem(0D, 0D, appleSize * width / node.getWidth(), apple);
				}).draw(width, height));
			} else {
				final DrawableRaster raster = MinecraftClientTestPilot.itemRaster(stone);
				DrawUtils.RASTER.drawRaster(node.getX(), node.getY(), node.getWidth(), node.getHeight(), (width, height) -> {
					DrawItem.inst().drawItem(0D, 0D, appleSize * width / node.getWidth(), apple);
					raster.draw(width, height);
				});
			}
		});
		final RectNode flat = RectNode.create(1000, 300, 256, 256).color(Color.rgb(0x202020)).onDraw(event -> {
			final Node node = event.getNode();
			DrawItem.inst().drawItem(node.getX(), node.getY(), appleSize, apple);
			DrawUtils.RASTER.drawRaster(node.getX(), node.getY(), node.getWidth(), node.getHeight(), MinecraftClientTestPilot.itemRaster(stone));
		});
		return new Node[] {nested, flat};
	}

	@Override
	public String getTexturePath(final ClientTestTexture texture) {
		return "minecraft:" + MinecraftClientTestPilot.texture(texture);
	}

	@Override
	public BufferedImage readTexture(final ClientTestTexture texture) {
		try (final InputStream stream = Minecraft.getInstance().getResourceManager().getResource(Identifier.withDefaultNamespace(MinecraftClientTestPilot.texture(texture))).orElseThrow().open()) {
			return ImageIO.read(stream);
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the texture " + texture, exception);
		}
	}

	@Override
	public boolean isDeleted(final ITexture texture) {
		return texture instanceof final MinecraftTexture minecraft && minecraft.isDeleted();
	}

	@Override
	public int countTextures() {
		int count = 0;
		for (int name = 1; name < 65536; name++) {
			if (GL11.glIsTexture(name)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public UI createBackgroundUi(final boolean background) {
		return background ? new VanillaBackground() : new NoBackground();
	}

	@Override
	public UI createHiddenHotbarUi() {
		return new HiddenHotbar();
	}

	@Override
	public int[] getHotbarBounds() {
		final Minecraft minecraft = Minecraft.getInstance();
		final int guiScale = minecraft.getWindow().getGuiScale();
		return new int[] {(minecraft.getWindow().getGuiScaledWidth() / 2 - 91) * guiScale, (minecraft.getWindow().getGuiScaledHeight() - 22) * guiScale, 182 * guiScale, 22 * guiScale};
	}

	@Override
	public int[] getHotbarSlotBounds(final int slot) {
		final Minecraft minecraft = Minecraft.getInstance();
		final int guiScale = minecraft.getWindow().getGuiScale();
		return new int[] {(minecraft.getWindow().getGuiScaledWidth() / 2 - 88 + slot * 20) * guiScale, (minecraft.getWindow().getGuiScaledHeight() - 19) * guiScale, 16 * guiScale, 16 * guiScale};
	}

	@Override
	public int[] getInventorySlotBounds(final int slot) {
		final Screen screen = Minecraft.getInstance().gui.screen();
		final Slot hotbar = ((InventoryScreen) screen).getMenu().getSlot(slot);
		final int scale = Minecraft.getInstance().getWindow().getGuiScale();
		return new int[] {((int) MinecraftClientTestPilot.field(screen, "leftPos") + hotbar.x) * scale, ((int) MinecraftClientTestPilot.field(screen, "topPos") + hotbar.y) * scale, 16 * scale, 16 * scale};
	}

	@Override
	public int[] getContainerScreenBounds() {
		final Screen screen = Minecraft.getInstance().gui.screen();
		final int scale = Minecraft.getInstance().getWindow().getGuiScale();
		return new int[] {(int) MinecraftClientTestPilot.field(screen, "leftPos") * scale, (int) MinecraftClientTestPilot.field(screen, "topPos") * scale, (int) MinecraftClientTestPilot.field(screen, "imageWidth") * scale, (int) MinecraftClientTestPilot.field(screen, "imageHeight") * scale};
	}

	@Override
	public int getHoveredSlot() {
		return Minecraft.getInstance().gui.screen() instanceof final ContainerUIScreen<?> screen && screen.getHoveredSlot() != null ? screen.getHoveredSlot().index : -1;
	}

	@Override
	public Cursor getCursor() {
		final Object cursor = MinecraftClientTestPilot.field(Minecraft.getInstance().getWindow(), "currentCursor");
		if (cursor == CursorTypes.POINTING_HAND) {
			return Cursor.POINTER;
		}

		if (cursor == CursorTypes.IBEAM) {
			return Cursor.TEXT;
		}
		return cursor == CursorType.DEFAULT ? Cursor.DEFAULT : null;
	}

	@Override
	public void drawHudFrame() {
		final Minecraft minecraft = Minecraft.getInstance();
		BridgeHandler.UI.getBridge(OverlayUIBridge.class).drawHud(new GuiGraphicsExtractor(minecraft, (GuiRenderState) MinecraftClientTestPilot.field(minecraft.gui, "guiRenderState"), 0, 0));
	}

	@Override
	public int countRenderTargets() {
		return ((List<?>) MinecraftClientTestPilot.field(MinecraftClientTestPilot.field(MinecraftClientTestPilot.field(BridgeHandler.UI.getBridge(OverlayUIBridge.class), "compositor"), "targetPool"), "targetList")).size();
	}

	@Override
	public ISnapshotBackend createSnapshotBackend() {
		return new MinecraftSnapshotBackend();
	}

	private int getModifiers() {
		return this.heldKeys.contains(Key.LEFT_SHIFT) ? GLFW.GLFW_MOD_SHIFT : 0;
	}

	private <T> T computeOnServer(final Function<ServerPlayer, T> function) {
		final MinecraftServer server = this.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer(), 120);
		return MinecraftClientTestPilot.await(server.submit(() -> function.apply(server.getPlayerList().getPlayers().getFirst())), "computing on the server", 120);
	}

	private void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!this.computeOnClient(condition::getAsBoolean, 120)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + this.computeOnClient(this::getScreen, 120));
			}

			this.sleep(250L);
		}
	}

	private void sleep(final long millis) {
		ClientTestContext.sleep(millis);
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

	private static int[] describe(final AbstractContainerMenu container) {
		final int[] counts = new int[container.slots.size() + 1];
		counts[0] = container.getCarried().getCount();
		for (int index = 0; index < container.slots.size(); index++) {
			counts[index + 1] = container.slots.get(index).getItem().getCount();
		}
		return counts;
	}

	private static Item item(final ClientTestItem item) {
		return switch (item) {
			case APPLE -> Items.APPLE;
			case DIAMOND -> Items.DIAMOND;
			case DIAMOND_SWORD -> Items.DIAMOND_SWORD;
			case EMERALD -> Items.EMERALD;
			case ENDER_PEARL -> Items.ENDER_PEARL;
			case FURNACE -> Items.FURNACE;
			case GOLDEN_APPLE -> Items.GOLDEN_APPLE;
			case SNOW_BLOCK -> Items.SNOW_BLOCK;
			case STONE -> Items.STONE;
		};
	}

	private static BlockState block(final ClientTestBlock block) {
		return switch (block) {
			case GRASS -> Blocks.GRASS_BLOCK.defaultBlockState();
			case ICE -> Blocks.ICE.defaultBlockState();
			case SNOW_BLOCK -> Blocks.SNOW_BLOCK.defaultBlockState();
			case STONE -> Blocks.STONE.defaultBlockState();
		};
	}

	private static String texture(final ClientTestTexture texture) {
		return switch (texture) {
			case ANIMATED_BLOCK -> "textures/block/sea_lantern.png";
			case DIAMOND_BLOCK -> "textures/block/diamond_block.png";
			case DIAMOND_ITEM -> "textures/item/diamond.png";
		};
	}

	private static SoundSource source(final ClientTestVolume volume) {
		return volume == ClientTestVolume.MASTER ? SoundSource.MASTER : SoundSource.UI;
	}

	private static DrawableRaster itemRaster(final ItemStack stack) {
		final ItemStackRenderState state = MinecraftClientTestPilot.itemState(stack);
		return FeatureDrawableRaster.of(Lighting.Entry.ITEMS_3D, (pose, collector) -> state.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0));
	}

	private static ItemStackRenderState itemState(final ItemStack stack) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ItemStackRenderState state = new ItemStackRenderState();
		minecraft.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
		return state;
	}

	private static <T> T await(final CompletableFuture<T> future, final String label, final int seconds) {
		try {
			return future.get(seconds, TimeUnit.SECONDS);
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

	public static final class VanillaBackground extends UI {

		@Override
		public void init() {}

	}

	@UIDataMinecraft(background = false)
	public static final class NoBackground extends UI {

		@Override
		public void init() {}

	}

	@UIDataOverlay(active = true)
	@UIDataOverlayLayer(layer = OverlayLayer.HOTBAR, cancel = true)
	public static final class HiddenHotbar extends UI {

		@Override
		public void init() {}

	}

}