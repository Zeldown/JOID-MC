package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.backend.minecraft.bridge.ui.container.ContainerUIScreen;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayUIBridge;
import dev.joid.backend.minecraft.bridge.ui.screen.UIScreen;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
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
import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
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
import dev.joid.lib.bridge.render.texture.Texture;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public final class MinecraftClientTestPilot implements IClientTestPilot {

	private static final AtomicInteger RELOADS = new AtomicInteger();

	private final Set<Key>     heldKeys = new HashSet<>();
	private final List<String> sounds   = new ArrayList<>();

	private String             window;
	private DemoContainer      demoContainer;
	private SoundEventListener soundListener;

	public static void countReload() {
		MinecraftClientTestPilot.RELOADS.incrementAndGet();
	}

	@Override
	public String getLoader() {
		return System.getProperty("joid.loader", "unknown");
	}

	@Override
	public String getVersion() {
		return "1.18.2";
	}

	@Override
	public String getGraphicsBackend() {
		return "opengl";
	}

	@Override
	public List<Class<?>> getContractTests() {
		return List.of(MinecraftRenderBridgeContractTest.class, MinecraftStateGuardContractTest.class, MinecraftBorrowedTextureContractTest.class, MinecraftBitmapFontContractTest.class);
	}

	@Override
	public Map<ClientTestScenario, String> getExclusions() {
		final Map<ClientTestScenario, String> exclusions = new EnumMap<>(ClientTestScenario.class);
		exclusions.put(ClientTestScenario.AUDIO_INTERFACE, "Minecraft 1.18.2 has no interface sound category, JOID follows the master volume");
		exclusions.put(ClientTestScenario.NESTED_FEATURE_RASTER, "Minecraft 1.18.2 draws at once, it has no feature render pipeline");
		return exclusions;
	}

	@Override
	public void reachTitleScreen() {
		this.waitFor("the end of the game loading", () -> MinecraftClientTestPilot.isLoaded() || Minecraft.getInstance().screen instanceof BackupConfirmScreen, 600);

		if (this.computeOnClient(() -> Minecraft.getInstance().screen instanceof BackupConfirmScreen, 120)) {
			this.runOnClient(() -> MinecraftClientTestPilot.pressButton(Minecraft.getInstance().screen, "selectWorld.backupJoinSkipButton"));
		}

		this.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getInstance();
			final long window = minecraft.getWindow().getWindow();
			final GLFWWindowFocusCallback callback = GLFW.glfwSetWindowFocusCallback(window, null);
			GLFW.glfwSetWindowFocusCallback(window, callback);
			callback.invoke(window, true);
			if (minecraft.level == null && !(minecraft.screen instanceof TitleScreen)) {
				minecraft.setScreen(new TitleScreen());
			}
		});
		this.waitFor("the title screen", () -> Minecraft.getInstance().screen instanceof TitleScreen, 120);
		this.sleep(2000L);
	}

	@Override
	public void joinWorld() {
		if (this.computeOnClient(() -> Minecraft.getInstance().level == null && !Minecraft.getInstance().getLevelSource().levelExists("joid-test"), 120)) {
			this.runOnClient(() -> {
				final RegistryAccess.Frozen registries = RegistryAccess.builtinCopy().freeze();
				final Registry<StructureSet> structures = registries.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
				final GameRules rules = new GameRules();
				rules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
				rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
				final LevelSettings settings = new LevelSettings("joid-test", GameType.SURVIVAL, false, Difficulty.NORMAL, false, rules, DataPackConfig.DEFAULT);
				final FlatLevelSource generator = new FlatLevelSource(structures, FlatLevelGeneratorSettings.getDefault(registries.registryOrThrow(Registry.BIOME_REGISTRY), structures));
				Minecraft.getInstance().createLevel("joid-test", settings, registries, new WorldGenSettings(1L, false, false, WorldGenSettings.withOverworld(registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), DimensionType.defaultDimensions(registries, 1L), generator)));
			});
			this.waitFor("the world creation", () -> Minecraft.getInstance().level != null || Minecraft.getInstance().screen instanceof ConfirmScreen, 600);
			if (this.computeOnClient(() -> Minecraft.getInstance().screen instanceof ConfirmScreen, 120)) {
				this.runOnClient(() -> ((BooleanConsumer) MinecraftClientTestPilot.field(Minecraft.getInstance().screen, "callback")).accept(true));
			}
		}

		this.waitFor("the singleplayer world", () -> Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null && Minecraft.getInstance().getOverlay() == null, 600);
		this.runOnClient(() -> {
			Minecraft.getInstance().getTutorial().setStep(TutorialSteps.NONE);
			Minecraft.getInstance().getToasts().clear();
		});
		this.sleep(5000L);
	}

	@Override
	public void stop(final boolean passed) {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(minecraft::stop);
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
	public void repeatKey(final Key key) {
		this.sendKey(key, GLFW.GLFW_REPEAT);
	}

	@Override
	public void scroll(final double amount) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().getWindow();
			final GLFWScrollCallback callback = GLFW.glfwSetScrollCallback(window, null);
			GLFW.glfwSetScrollCallback(window, callback);
			callback.invoke(window, 0D, amount);
		});
	}

	@Override
	public void pressMouse(final boolean press) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().getWindow();
			final GLFWMouseButtonCallback callback = GLFW.glfwSetMouseButtonCallback(window, null);
			GLFW.glfwSetMouseButtonCallback(window, callback);
			callback.invoke(window, GLFW.GLFW_MOUSE_BUTTON_LEFT, press ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, this.getModifiers());
		});
	}

	@Override
	public BufferedImage capture(final String name) {
		final Path path = this.computeOnClient(() -> {
			final Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots").resolve(name + ".png");
			Files.createDirectories(file.getParent());
			try (final NativeImage image = Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget())) {
				image.writeToFile(file);
			}
			return file;
		}, 120);
		try {
			return ImageIO.read(path.toFile());
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + name, exception);
		}
	}

	@Override
	public void moveMouse(final double x, final double y) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().getWindow();
			final GLFWCursorPosCallback callback = GLFW.glfwSetCursorPosCallback(window, null);
			GLFW.glfwSetCursorPosCallback(window, callback);
			callback.invoke(window, x, y);
		});
	}

	@Override
	public void pressKey(final Key key, final boolean press) {
		if (press) {
			this.heldKeys.add(key);
		} else {
			this.heldKeys.remove(key);
		}

		if (key == Key.LEFT_SHIFT || key == Key.F3 || key == Key.BACKSPACE) {
			this.pressHeldKey(key, press);
			return;
		}

		this.sendKey(key, press ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE);
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
	public void closeDemoContainer() {
		this.computeOnServer(player -> {
			player.closeContainer();
			return null;
		});
	}

	@Override
	public boolean isDemoContainerOpen() {
		return this.computeOnServer(player -> player.containerMenu instanceof DemoContainer);
	}

	@Override
	public int countDemoStorage(final int slot) {
		return this.computeOnServer(player -> this.demoContainer.getStorage().getItem(slot).getCount());
	}

	@Override
	public int[] readContainer(final boolean server) {
		if (server) {
			return this.computeOnServer(player -> MinecraftClientTestPilot.describe(player.containerMenu));
		}
		return this.computeOnClient(() -> MinecraftClientTestPilot.describe(Minecraft.getInstance().player.containerMenu), 120);
	}

	@Override
	public void quickMoveDemoContainer(final int slot) {
		this.computeOnServer(player -> player.containerMenu.quickMoveStack(player, slot));
	}

	@Override
	public boolean canOpenDemoContainer(final int level) {
		return this.computeOnServer(player -> {
			final MinecraftServer server = player.getServer();
			final ServerPlayer stranger = new ServerPlayer(server, server.overworld(), new GameProfile(UUID.randomUUID(), "JOIDStranger"));
			if (level > 0) {
				server.getPlayerList().getOps().add(new ServerOpListEntry(stranger.getGameProfile(), level == 1 ? 1 : 2, false));
			}

			try {
				return OpenDemoContainerPayloadHandler.canOpen(stranger);
			} finally {
				server.getPlayerList().deop(stranger.getGameProfile());
			}
		});
	}

	@Override
	public int countInventory(final ClientTestItem item) {
		return this.computeOnServer(player -> player.getInventory().countItem(MinecraftClientTestPilot.item(item)));
	}

	@Override
	public void clickDemoContainer(final int slot, final int button) {
		this.computeOnServer(player -> {
			player.containerMenu.clicked(slot, button, ClickType.PICKUP, player);
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
	public Object getScreen() {
		return Minecraft.getInstance().screen;
	}

	@Override
	public void closeScreen() {
		Minecraft.getInstance().setScreen(null);
	}

	@Override
	public boolean isScreenOpen() {
		return Minecraft.getInstance().screen != null;
	}

	@Override
	public boolean isJoidScreen() {
		return Minecraft.getInstance().screen instanceof UIScreen;
	}

	@Override
	public boolean isGamePaused() {
		return Minecraft.getInstance().isPaused();
	}

	@Override
	public boolean isTitleScreen() {
		return Minecraft.getInstance().screen instanceof TitleScreen;
	}

	@Override
	public boolean isPauseScreen() {
		return Minecraft.getInstance().screen.isPauseScreen();
	}

	@Override
	public String getScreenTitle() {
		return Minecraft.getInstance().screen.getTitle().getString();
	}

	@Override
	public boolean isContainerScreen() {
		return Minecraft.getInstance().screen instanceof ContainerUIScreen;
	}

	@Override
	public boolean isInventoryScreen() {
		return Minecraft.getInstance().screen instanceof InventoryScreen;
	}

	@Override
	public boolean isDebugScreenShown() {
		return Minecraft.getInstance().options.renderDebug;
	}

	@Override
	public int getGuiScale() {
		return (int) Minecraft.getInstance().getWindow().getGuiScale();
	}

	@Override
	public void applyGuiScale(final int guiScale) {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.guiScale = guiScale;
		minecraft.resizeDisplay();
	}

	@Override
	public int countReloads() {
		return MinecraftClientTestPilot.RELOADS.get();
	}

	@Override
	public void openControls() {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.setScreen(new KeyBindsScreen(minecraft.screen, minecraft.options));
	}

	@Override
	public List<String> getControlsLabels() {
		final KeyBindsList list = (KeyBindsList) MinecraftClientTestPilot.field(Minecraft.getInstance().screen, "keyBindsList");
		list.setScrollAmount(list.getMaxScroll());
		final List<String> labels = new ArrayList<>();
		for (final KeyBindsList.Entry entry : list.children()) {
			labels.add(((Component) MinecraftClientTestPilot.field(entry, "name")).getString());
		}
		return labels;
	}

	@Override
	public boolean isReloading() {
		return Minecraft.getInstance().getOverlay() != null;
	}

	@Override
	public void reloadResources() {
		Minecraft.getInstance().reloadResourcePacks();
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
	public Object createRedChat(final String text) {
		return new TextComponent(text).withStyle(ChatFormatting.RED);
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
	public int measureText(final IFont font, final String text) {
		return Minecraft.getInstance().font.width(new TextComponent(text).withStyle(style -> style.withFont(((MinecraftFont) font).getId())));
	}

	@Override
	public String translate(final String language, final String key) {
		return ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), List.of(Minecraft.getInstance().getLanguageManager().getLanguage(language))).getOrDefault(key);
	}

	@Override
	public int getAudioError() {
		return AL10.alGetError();
	}

	@Override
	public int getStencilBits() {
		final MinecraftRenderTarget target = new MinecraftRenderTarget(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
		final int frameBuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		target.bind();
		try {
			return GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE);
		} finally {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
			target.delete();
		}
	}

	@Override
	public Object getAudioContext() {
		return Lwjgl3AlBinding.inst().getCurrentContext();
	}

	@Override
	public void startSoundCapture() {
		this.sounds.clear();
		this.soundListener = (sound, accessor) -> this.sounds.add(sound.getLocation().toString());
		Minecraft.getInstance().getSoundManager().addListener(this.soundListener);
	}

	@Override
	public List<String> stopSoundCapture() {
		Minecraft.getInstance().getSoundManager().removeListener(this.soundListener);
		return List.copyOf(this.sounds);
	}

	@Override
	public void playInterfaceSound() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1F));
	}

	@Override
	public String getNoteBlockSound() {
		return SoundEvents.NOTE_BLOCK_PLING.getLocation().toString();
	}

	@Override
	public float getAudioGain(final int source) {
		return AL10.alGetSourcef(source, AL10.AL_GAIN);
	}

	@Override
	public void setSubtitles(final boolean subtitles) {
		Minecraft.getInstance().options.showSubtitles = subtitles;
	}

	@Override
	public double getVolume(final ClientTestVolume volume) {
		return volume == ClientTestVolume.MASTER ? Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) : 1D;
	}

	@Override
	public void setVolume(final ClientTestVolume volume, final double value) {
		if (volume == ClientTestVolume.MASTER) {
			Minecraft.getInstance().options.setSoundCategoryVolume(SoundSource.MASTER, (float) value);
		}
	}

	@Override
	public boolean hasEntity(final Node node) {
		return ((EntityNode) node).getEntity() != null;
	}

	@Override
	public boolean isOnCooldown(final ClientTestItem item) {
		return Minecraft.getInstance().player.getCooldowns().isOnCooldown(MinecraftClientTestPilot.item(item));
	}

	@Override
	public Resource createSprite(final ClientTestItem item) {
		return Resource.of(new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.tryParse("item/" + item.name().toLowerCase(Locale.ROOT))));
	}

	@Override
	public Node[] createNestedRasterNodes(final boolean feature) {
		final ItemStack apple = new ItemStack(Items.APPLE);
		final ItemStack stone = new ItemStack(Items.STONE);
		final RectNode nested = RectNode.create(400, 300, 256, 256).color(Color.rgb(0x202020)).onDraw(event -> {
			final Node node = event.getNode();
			final DrawableRaster raster = MinecraftClientTestPilot.itemRaster(stone);
			DrawUtils.RASTER.drawRaster(node.getX(), node.getY(), node.getWidth(), node.getHeight(), (width, height) -> {
				DrawItem.inst().drawItem(0D, 0D, 64D * width / node.getWidth(), apple);
				raster.draw(width, height);
			});
		});
		final RectNode flat = RectNode.create(1000, 300, 256, 256).color(Color.rgb(0x202020)).onDraw(event -> {
			final Node node = event.getNode();
			DrawItem.inst().drawItem(node.getX(), node.getY(), 64D, apple);
			DrawUtils.RASTER.drawRaster(node.getX(), node.getY(), node.getWidth(), node.getHeight(), MinecraftClientTestPilot.itemRaster(stone));
		});
		return new Node[] {nested, flat};
	}

	@Override
	public Node createBlockNode(final double x, final double y, final double size, final ClientTestBlock block) {
		return BlockNode.create(x, y, size, size).block(MinecraftClientTestPilot.block(block));
	}

	@Override
	public Node createItemNode(final double x, final double y, final double size, final ClientTestItem item, final boolean glint) {
		final ItemNode node = ItemNode.create(x, y, size, size).stack(new ItemStack(MinecraftClientTestPilot.item(item)));
		return glint ? node.glint(true) : node;
	}

	@Override
	public Node createEntityNode(final double x, final double y, final double width, final double height, final ClientTestEntity entity, final double yaw, final double pitch) {
		final EntityNode node = EntityNode.create(x, y, width, height).rotationYaw(yaw).rotationPitch(pitch);
		return switch (entity) {
			case PIG -> node.type(EntityType.PIG);
			case ZOMBIE -> node.type(EntityType.ZOMBIE);
			case PLAYER_PROFILE -> node.profile(new GameProfile(new UUID(0L, 15L), "Steve"));
			case PLAYER_SKIN -> node.skin(new ResourceLocation("joid", "demo/textures/skin.png"));
		};
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
	public boolean isDeleted(final ITexture texture) {
		return texture instanceof final Texture created && created.isDeleted();
	}

	@Override
	public String getTexturePath(final ClientTestTexture texture) {
		return "minecraft:" + MinecraftClientTestPilot.texture(texture);
	}

	@Override
	public BufferedImage readTexture(final ClientTestTexture texture) {
		try (final InputStream stream = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.tryParse(MinecraftClientTestPilot.texture(texture))).getInputStream()) {
			return ImageIO.read(stream);
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the texture " + texture, exception);
		}
	}

	@Override
	public Cursor getCursor() {
		return (Cursor) MinecraftClientTestPilot.field(BridgeHandler.WINDOW.getBridge(MinecraftWindowBridge.class), "applied");
	}

	@Override
	public void drawHudFrame() {
		BridgeHandler.UI.getBridge(OverlayUIBridge.class).drawHud(new PoseStack());
	}

	@Override
	public int getHoveredSlot() {
		return Minecraft.getInstance().screen instanceof final ContainerUIScreen<?> screen && screen.getHoveredSlot() != null ? screen.getHoveredSlot().index : -1;
	}

	@Override
	public int[] getHotbarBounds() {
		final Minecraft minecraft = Minecraft.getInstance();
		final int guiScale = this.getGuiScale();
		return new int[] {(minecraft.getWindow().getGuiScaledWidth() / 2 - 91) * guiScale, (minecraft.getWindow().getGuiScaledHeight() - 22) * guiScale, 182 * guiScale, 22 * guiScale};
	}

	@Override
	public int countRenderTargets() {
		return 0;
	}

	@Override
	public int[] getContainerScreenBounds() {
		final Screen screen = Minecraft.getInstance().screen;
		final int scale = this.getGuiScale();
		return new int[] {(int) MinecraftClientTestPilot.field(screen, "leftPos") * scale, (int) MinecraftClientTestPilot.field(screen, "topPos") * scale, (int) MinecraftClientTestPilot.field(screen, "imageWidth") * scale, (int) MinecraftClientTestPilot.field(screen, "imageHeight") * scale};
	}

	@Override
	public int[] getHotbarSlotBounds(final int slot) {
		final Minecraft minecraft = Minecraft.getInstance();
		final int guiScale = this.getGuiScale();
		return new int[] {(minecraft.getWindow().getGuiScaledWidth() / 2 - 88 + slot * 20) * guiScale, (minecraft.getWindow().getGuiScaledHeight() - 19) * guiScale, 16 * guiScale, 16 * guiScale};
	}

	@Override
	public int[] getInventorySlotBounds(final int slot) {
		final Screen screen = Minecraft.getInstance().screen;
		final Slot hotbar = ((InventoryScreen) screen).getMenu().getSlot(slot);
		final int scale = this.getGuiScale();
		return new int[] {((int) MinecraftClientTestPilot.field(screen, "leftPos") + hotbar.x) * scale, ((int) MinecraftClientTestPilot.field(screen, "topPos") + hotbar.y) * scale, 16 * scale, 16 * scale};
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
	public ISnapshotBackend createSnapshotBackend() {
		return new MinecraftSnapshotBackend();
	}

	private int getModifiers() {
		return this.heldKeys.contains(Key.LEFT_SHIFT) ? GLFW.GLFW_MOD_SHIFT : 0;
	}

	private void sendKey(final Key key, final int action) {
		this.runOnClient(() -> {
			final long window = Minecraft.getInstance().getWindow().getWindow();
			final GLFWKeyCallback callback = GLFW.glfwSetKeyCallback(window, null);
			GLFW.glfwSetKeyCallback(window, callback);
			final int code = GlfwKeys.getCode(key);
			callback.invoke(window, code, GLFW.glfwGetKeyScancode(code), action, this.getModifiers());
		});
	}

	private void pressHeldKey(final Key key, final boolean press) {
		if (this.window == null) {
			this.window = MinecraftClientTestPilot.xdotool("search", "--sync", "--limit", "1", "--name", "^Minecraft").trim();
			MinecraftClientTestPilot.xdotool("windowfocus", "--sync", this.window);
		}

		MinecraftClientTestPilot.xdotool(press ? "keydown" : "keyup", key == Key.F3 ? "F3" : key == Key.BACKSPACE ? "BackSpace" : "shift");
		final int code = GlfwKeys.getCode(key);
		this.waitFor("the key " + key + (press ? " down" : " up"), () -> (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), code) == GLFW.GLFW_PRESS) == press, 30);
	}

	private <T> T computeOnServer(final Function<ServerPlayer, T> function) {
		final MinecraftServer server = this.computeOnClient(() -> Minecraft.getInstance().getSingleplayerServer(), 120);
		return MinecraftClientTestPilot.await(server.submit(() -> function.apply(server.getPlayerList().getPlayers().get(0))), "computing on the server", 120);
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
		return minecraft.getOverlay() == null && (minecraft.level != null || minecraft.screen instanceof TitleScreen || minecraft.screen instanceof DisconnectedScreen);
	}

	private static void pressButton(final Screen screen, final String translationKey) {
		final String message = new TranslatableComponent(translationKey).getString();
		for (final GuiEventListener child : screen.children()) {
			if (child instanceof final Button button && button.getMessage().getString().equals(message)) {
				button.onPress();
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

	private static DrawableRaster itemRaster(final ItemStack stack) {
		final Minecraft minecraft = Minecraft.getInstance();
		final BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
		return GuiDrawableRaster.of(Lighting::setupFor3DItems, (pose, buffer) -> minecraft.getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, pose, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model));
	}

	private static String xdotool(final String... arguments) {
		try {
			final Process process = new ProcessBuilder(Stream.concat(Stream.of("timeout", "30", "xdotool"), Arrays.stream(arguments)).collect(Collectors.toList())).redirectErrorStream(true).start();
			final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			if (process.waitFor() != 0) {
				throw new AssertionError("xdotool " + String.join(" ", arguments) + " failed or ran past 30 s: " + output);
			}
			return output;
		} catch (final IOException exception) {
			throw new AssertionError("xdotool cannot run", exception);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while running xdotool", exception);
		}
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