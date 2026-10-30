package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
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
import dev.joid.backend.minecraft.demo.snapshot.MinecraftSnapshotBackend;
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
import dev.joid.base.lwjgl2.openal.Lwjgl2AlBinding;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;

public final class MinecraftClientTestPilot implements IClientTestPilot {

	private static final AtomicInteger RELOADS = new AtomicInteger();

	private static final RenderItem RENDER = new RenderItem();

	private final Queue<Runnable> serverTasks = new ConcurrentLinkedQueue<>();
	private final List<String>    sounds      = Collections.synchronizedList(new ArrayList<>());

	private String  window;
	private boolean soundCapture;

	public MinecraftClientTestPilot() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void countReload() {
		MinecraftClientTestPilot.RELOADS.incrementAndGet();
	}

	@SubscribeEvent
	public void serverTick(final ServerTickEvent event) {
		if (event.phase != Phase.START) {
			return;
		}

		for (Runnable task = this.serverTasks.poll(); task != null; task = this.serverTasks.poll()) {
			task.run();
		}
	}

	@SubscribeEvent
	public void playSound(final PlaySoundEvent17 event) {
		if (this.soundCapture) {
			this.sounds.add(event.sound.getPositionedSoundLocation().toString());
		}
	}

	@Override
	public String getLoader() {
		return System.getProperty("joid.loader", "unknown");
	}

	@Override
	public String getVersion() {
		return "1.7.10";
	}

	@Override
	public String getGraphicsBackend() {
		return "opengl";
	}

	@Override
	public List<Class<?>> getContractTests() {
		return Arrays.asList(MinecraftRenderBridgeContractTest.class, MinecraftStateGuardContractTest.class, MinecraftBorrowedTextureContractTest.class, MinecraftBitmapFontContractTest.class);
	}

	@Override
	public Map<ClientTestScenario, String> getExclusions() {
		final Map<ClientTestScenario, String> exclusions = new EnumMap<>(ClientTestScenario.class);
		exclusions.put(ClientTestScenario.AUDIO_INTERFACE, "Minecraft 1.7.10 has no interface sound category, JOID follows the master volume");
		exclusions.put(ClientTestScenario.ITEM_COOLDOWN, "Minecraft 1.7.10 has no item cooldowns, added in 1.9");
		exclusions.put(ClientTestScenario.SCREEN_TITLE, "a screen of Minecraft 1.7.10 has no title, so UIDataMinecraft has none");
		exclusions.put(ClientTestScenario.SUBTITLES, "Minecraft 1.7.10 has no subtitles, added in 1.9");
		exclusions.put(ClientTestScenario.NESTED_FEATURE_RASTER, "Minecraft 1.7.10 draws at once, it has no feature render pipeline");
		exclusions.put(ClientTestScenario.FONT_TRAILING_CODE, "the FontRenderer of Minecraft 1.7.10 measures a trailing \u00A7 as -1 pixel, a bug of the game that JOID does not copy");
		exclusions.put(ClientTestScenario.CONTAINER_CLOSE, "Minecraft 1.7.10 drops the carried stack when a container closes, newer versions give it back to the inventory");
		return exclusions;
	}

	@Override
	public void reachTitleScreen() {
		this.waitFor("JOID on the main menu", () -> MinecraftBackend.inst().isRegistered() && Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu, 600);
		this.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.gameSettings.pauseOnLostFocus = false;
			minecraft.gameSettings.difficulty = EnumDifficulty.PEACEFUL;
			minecraft.gameSettings.showInventoryAchievementHint = false;
		});
		this.sleep(2000L);
	}

	@Override
	public void joinWorld() {
		this.runOnClient(() -> Minecraft.getMinecraft().launchIntegratedServer("joid-test", "joid-test", new WorldSettings(1L, WorldSettings.GameType.SURVIVAL, false, false, WorldType.FLAT)));
		this.waitFor("the singleplayer world", () -> Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null, 600);
		this.runOnClient(() -> Minecraft.getMinecraft().displayGuiScreen(null));
		this.waitFor("the game without screen", () -> Minecraft.getMinecraft().currentScreen == null, 30);
		this.computeOnServer(player -> {
			player.worldObj.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
			player.worldObj.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
			return null;
		});
		this.sleep(5000L);
	}

	@Override
	public void stop(final boolean passed) {
		Minecraft.getMinecraft().func_152344_a(Minecraft.getMinecraft()::shutdown);
	}

	@Override
	public void runOnClient(final Runnable runnable) {
		MinecraftClientTestPilot.await(Minecraft.getMinecraft().func_152344_a(runnable), "running on the client", 120);
	}

	@Override
	public <T> T computeOnClient(final Callable<T> callable, final int seconds) {
		return (T) MinecraftClientTestPilot.await(Minecraft.getMinecraft().func_152343_a(callable), "computing on the client", seconds);
	}

	@Override
	public void scroll(final double amount) {
		this.focusWindow();
		MinecraftClientTestPilot.xdotool("click", amount < 0D ? "5" : "4");
	}

	@Override
	public void pressMouse(final boolean press) {
		this.focusWindow();
		MinecraftClientTestPilot.xdotool(press ? "mousedown" : "mouseup", "1");
	}

	@Override
	public BufferedImage capture(final String name) {
		this.runOnClient(() -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			ScreenShotHelper.saveScreenshot(minecraft.mcDataDir, name + ".png", minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
		});
		try {
			return ImageIO.read(new File(new File(Minecraft.getMinecraft().mcDataDir, "screenshots"), name + ".png"));
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the capture " + name, exception);
		}
	}

	@Override
	public void moveMouse(final double x, final double y) {
		MinecraftClientTestPilot.xdotool("mousemove", "--window", this.focusWindow(), String.valueOf((int) Math.round(x)), String.valueOf((int) Math.round(y)));
	}

	@Override
	public void pressKey(final Key key, final boolean press) {
		this.focusWindow();
		MinecraftClientTestPilot.xdotool(press ? "keydown" : "keyup", MinecraftClientTestPilot.keysym(key));
	}

	@Override
	public void clearInventory() {
		this.computeOnServer(player -> {
			for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
				player.inventory.setInventorySlotContents(slot, null);
			}
			player.inventory.setItemStack(null);
			player.updateHeldItem();
			player.inventoryContainer.detectAndSendChanges();
			return null;
		});
	}

	@Override
	public int openDemoContainer() {
		this.clearInventory();
		return this.computeOnServer(player -> {
			OpenDemoContainerPayloadHandler.handle(OpenDemoContainerPayload.INSTANCE, player);
			if (!(player.openContainer instanceof DemoContainer)) {
				return -1;
			}

			for (int slot = 0; slot < 27; slot++) {
				OpenDemoContainerPayloadHandler.getStorage(player).setInventorySlotContents(slot, null);
			}
			return player.openContainer.inventorySlots.size();
		});
	}

	@Override
	public void closeDemoContainer() {
		this.computeOnServer(player -> {
			player.closeScreen();
			return null;
		});
	}

	@Override
	public boolean isDemoContainerOpen() {
		return this.computeOnServer(player -> player.openContainer instanceof DemoContainer);
	}

	@Override
	public int countDemoStorage(final int slot) {
		return this.computeOnServer(player -> MinecraftClientTestPilot.count(OpenDemoContainerPayloadHandler.getStorage(player).getStackInSlot(slot)));
	}

	@Override
	public int[] readContainer(final boolean server) {
		if (server) {
			return this.computeOnServer(player -> MinecraftClientTestPilot.describe(player.inventory.getItemStack(), player.openContainer));
		}
		return this.computeOnClient(() -> {
			final EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
			return MinecraftClientTestPilot.describe(player.inventory.getItemStack(), player.openContainer);
		}, 120);
	}

	@Override
	public void quickMoveDemoContainer(final int slot) {
		this.computeOnServer(player -> player.openContainer.transferStackInSlot(player, slot));
	}

	@Override
	public boolean canOpenDemoContainer(final int level) {
		return this.computeOnServer(player -> {
			final MinecraftServer server = MinecraftServer.getServer();
			final WorldServer world = server.worldServerForDimension(0);
			final GameProfile profile = new GameProfile(UUID.randomUUID(), "JOIDStranger");
			final EntityPlayerMP stranger = new EntityPlayerMP(server, world, profile, new ItemInWorldManager(world));
			if (level > 0) {
				server.getConfigurationManager().func_152603_m().func_152687_a(new UserListOpsEntry(profile, level));
			}

			try {
				return OpenDemoContainerPayloadHandler.canOpen(stranger);
			} finally {
				server.getConfigurationManager().func_152610_b(profile);
			}
		});
	}

	@Override
	public int countInventory(final ClientTestItem item) {
		return this.computeOnServer(player -> {
			int count = 0;
			for (final ItemStack stack : player.inventory.mainInventory) {
				if (stack != null && stack.getItem() == MinecraftClientTestPilot.stack(item, 1).getItem()) {
					count += stack.stackSize;
				}
			}
			return count;
		});
	}

	@Override
	public void clickDemoContainer(final int slot, final int button) {
		this.computeOnServer(player -> player.openContainer.slotClick(slot, button, 0, player));
	}

	@Override
	public void giveItem(final int slot, final ClientTestItem item, final int count) {
		this.computeOnServer(player -> {
			player.inventory.setInventorySlotContents(slot, MinecraftClientTestPilot.stack(item, count));
			player.inventoryContainer.detectAndSendChanges();
			return null;
		});
	}

	@Override
	public void setContainerSlot(final int slot, final ClientTestItem item, final int count) {
		this.computeOnServer(player -> {
			player.openContainer.getSlot(slot).putStack(MinecraftClientTestPilot.stack(item, count));
			player.openContainer.detectAndSendChanges();
			return null;
		});
		this.sleep(500L);
	}

	@Override
	public Object getScreen() {
		return Minecraft.getMinecraft().currentScreen;
	}

	@Override
	public void closeScreen() {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@Override
	public boolean isScreenOpen() {
		return Minecraft.getMinecraft().currentScreen != null;
	}

	@Override
	public boolean isJoidScreen() {
		return Minecraft.getMinecraft().currentScreen instanceof UIScreen;
	}

	@Override
	public boolean isGamePaused() {
		return Minecraft.getMinecraft().isGamePaused();
	}

	@Override
	public boolean isTitleScreen() {
		return Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu;
	}

	@Override
	public boolean isPauseScreen() {
		return Minecraft.getMinecraft().currentScreen.doesGuiPauseGame();
	}

	@Override
	public String getScreenTitle() {
		throw new UnsupportedOperationException("A screen of Minecraft 1.7.10 has no title");
	}

	@Override
	public boolean isContainerScreen() {
		return Minecraft.getMinecraft().currentScreen instanceof ContainerUIScreen;
	}

	@Override
	public boolean isInventoryScreen() {
		return Minecraft.getMinecraft().currentScreen instanceof GuiInventory;
	}

	@Override
	public boolean isDebugScreenShown() {
		return Minecraft.getMinecraft().gameSettings.showDebugInfo;
	}

	@Override
	public int getGuiScale() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		return new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor();
	}

	@Override
	public void applyGuiScale(final int guiScale) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.gameSettings.guiScale = guiScale;
		if (minecraft.currentScreen != null) {
			final ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
			minecraft.currentScreen.setWorldAndResolution(minecraft, resolution.getScaledWidth(), resolution.getScaledHeight());
		}
	}

	@Override
	public int countReloads() {
		return MinecraftClientTestPilot.RELOADS.get();
	}

	@Override
	public void openControls() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.displayGuiScreen(new GuiControls(minecraft.currentScreen, minecraft.gameSettings));
	}

	@Override
	public List<String> getControlsLabels() {
		final Object list = MinecraftClientTestPilot.field(Minecraft.getMinecraft().currentScreen, "keyBindingList");
		final List<String> labels = new ArrayList<>();
		for (final Object entry : (Object[]) MinecraftClientTestPilot.field(list, "field_148190_m")) {
			if (entry != null) {
				labels.add((String) MinecraftClientTestPilot.field(entry, entry.getClass().getSimpleName().equals("CategoryEntry") ? "field_148285_b" : "field_148283_c"));
			}
		}
		return labels;
	}

	@Override
	public boolean isReloading() {
		return false;
	}

	@Override
	public void reloadResources() {
		Minecraft.getMinecraft().refreshResources();
	}

	@Override
	public File getGameDirectory() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public boolean isBackendRegistered() {
		return MinecraftBackend.inst().isRegistered();
	}

	@Override
	public Object createRedChat(final String text) {
		return new ChatComponentText(text).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
	}

	@Override
	public Object getKeyMapping(final String name) {
		for (final KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			if (binding.getKeyDescription().equals(name)) {
				return binding;
			}
		}
		throw new AssertionError("Minecraft has no key binding " + name);
	}

	@Override
	public int measureText(final IFont font, final String text) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final FontRenderer renderer = font == MinecraftFont.ALT ? minecraft.standardGalacticFontRenderer : minecraft.fontRenderer;
		return renderer.getStringWidth(text);
	}

	@Override
	public String translate(final String language, final String key) {
		final String file = language.substring(0, 3) + language.substring(3).toUpperCase();
		try (final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("joid", "lang/" + file + ".lang")).getInputStream()) {
			final Properties properties = new Properties();
			properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
			return properties.getProperty(key, key);
		} catch (final IOException exception) {
			throw new AssertionError("Unable to read the language " + language, exception);
		}
	}

	@Override
	public int getAudioError() {
		return AL10.alGetError();
	}

	@Override
	public int getStencilBits() {
		final Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
		framebuffer.bindFramebuffer(false);
		try {
			final int bits = GL11.glGetInteger(GL11.GL_STENCIL_BITS);
			return MinecraftForgeClient.getStencilBits() == bits ? bits : -1;
		} finally {
			framebuffer.unbindFramebuffer();
		}
	}

	@Override
	public Object getAudioContext() {
		return Lwjgl2AlBinding.inst().getCurrentContext();
	}

	@Override
	public void startSoundCapture() {
		this.sounds.clear();
		this.soundCapture = true;
	}

	@Override
	public List<String> stopSoundCapture() {
		this.soundCapture = false;
		return new ArrayList<>(this.sounds);
	}

	@Override
	public void playInterfaceSound() {
		throw new UnsupportedOperationException("Minecraft 1.7.10 has no subtitles to show a sound");
	}

	@Override
	public String getNoteBlockSound() {
		return "minecraft:note.pling";
	}

	@Override
	public float getAudioGain(final int source) {
		return AL10.alGetSourcef(source, AL10.AL_GAIN);
	}

	@Override
	public void setSubtitles(final boolean subtitles) {
		throw new UnsupportedOperationException("Minecraft 1.7.10 has no subtitles");
	}

	@Override
	public double getVolume(final ClientTestVolume volume) {
		return volume == ClientTestVolume.MASTER ? Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) : 1D;
	}

	@Override
	public void setVolume(final ClientTestVolume volume, final double value) {
		if (volume == ClientTestVolume.MASTER) {
			Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MASTER, (float) value);
		}
	}

	@Override
	public boolean hasEntity(final Node node) {
		return ((EntityNode) node).getEntity() != null;
	}

	@Override
	public boolean isOnCooldown(final ClientTestItem item) {
		throw new UnsupportedOperationException("Minecraft 1.7.10 has no item cooldowns");
	}

	@Override
	public Resource createSprite(final ClientTestItem item) {
		final ItemStack stack = MinecraftClientTestPilot.stack(item, 1);
		return Resource.of(stack.getItem().getIconFromDamage(stack.getItemDamage()));
	}

	@Override
	public Node[] createNestedRasterNodes(final boolean feature) {
		final ItemStack apple = new ItemStack(Items.apple);
		final ItemStack stone = new ItemStack(Blocks.stone);
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
		final BlockNode node = BlockNode.create(x, y, size, size);
		switch (block) {
			case GRASS:
				return node.block(Blocks.grass);
			case ICE:
				return node.block(Blocks.ice);
			case SNOW_BLOCK:
				return node.block(Blocks.snow);
			default:
				return node.block(Blocks.stone);
		}
	}

	@Override
	public Node createItemNode(final double x, final double y, final double size, final ClientTestItem item, final boolean glint) {
		final ItemNode node = ItemNode.create(x, y, size, size).stack(MinecraftClientTestPilot.stack(item, 1));
		return glint ? node.glint(true) : node;
	}

	@Override
	public Node createEntityNode(final double x, final double y, final double width, final double height, final ClientTestEntity entity, final double yaw, final double pitch) {
		final EntityNode node = EntityNode.create(x, y, width, height).rotationYaw(yaw).rotationPitch(pitch);
		switch (entity) {
			case PIG:
				return node.type(EntityPig.class);
			case ZOMBIE:
				return node.type(EntityZombie.class);
			case PLAYER_PROFILE:
				return node.profile(new GameProfile(new UUID(0L, 15L), "Steve"));
			default:
				return node.skin(new ResourceLocation("joid", "demo/textures/skin.png"));
		}
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
		return texture instanceof Texture && ((Texture) texture).isDeleted();
	}

	@Override
	public String getTexturePath(final ClientTestTexture texture) {
		return "minecraft:" + MinecraftClientTestPilot.texture(texture);
	}

	@Override
	public BufferedImage readTexture(final ClientTestTexture texture) {
		try (final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(MinecraftClientTestPilot.texture(texture))).getInputStream()) {
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
		GuiFrameCounter.inst().nextFrame();
		BridgeHandler.UI.getBridge(OverlayUIBridge.class).drawHud(null);
	}

	@Override
	public int getHoveredSlot() {
		final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if (!(screen instanceof ContainerUIScreen)) {
			return -1;
		}

		final Slot slot = (Slot) MinecraftClientTestPilot.field(screen, "hoveredSlot");
		return slot != null ? slot.slotNumber : -1;
	}

	@Override
	public int[] getHotbarBounds() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
		final int scale = resolution.getScaleFactor();
		return new int[] {(resolution.getScaledWidth() / 2 - 91) * scale, (resolution.getScaledHeight() - 22) * scale, 182 * scale, 22 * scale};
	}

	@Override
	public int countRenderTargets() {
		return 0;
	}

	@Override
	public int[] getContainerScreenBounds() {
		final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		final int scale = this.getGuiScale();
		return new int[] {(int) MinecraftClientTestPilot.field(screen, "guiLeft") * scale, (int) MinecraftClientTestPilot.field(screen, "guiTop") * scale, (int) MinecraftClientTestPilot.field(screen, "xSize") * scale, (int) MinecraftClientTestPilot.field(screen, "ySize") * scale};
	}

	@Override
	public int[] getHotbarSlotBounds(final int slot) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
		final int scale = resolution.getScaleFactor();
		return new int[] {(resolution.getScaledWidth() / 2 - 88 + slot * 20) * scale, (resolution.getScaledHeight() - 19) * scale, 16 * scale, 16 * scale};
	}

	@Override
	public int[] getInventorySlotBounds(final int slot) {
		final GuiContainer screen = (GuiContainer) Minecraft.getMinecraft().currentScreen;
		final Slot target = screen.inventorySlots.getSlot(slot);
		final int scale = this.getGuiScale();
		return new int[] {((int) MinecraftClientTestPilot.field(screen, "guiLeft") + target.xDisplayPosition) * scale, ((int) MinecraftClientTestPilot.field(screen, "guiTop") + target.yDisplayPosition) * scale, 16 * scale, 16 * scale};
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

	private String focusWindow() {
		if (this.window == null) {
			this.window = MinecraftClientTestPilot.xdotool("search", "--sync", "--limit", "1", "--name", "^Minecraft").trim();
			MinecraftClientTestPilot.xdotool("windowfocus", "--sync", this.window);
		}
		return this.window;
	}

	private <T> T computeOnServer(final Function<EntityPlayerMP, T> function) {
		final CompletableFuture<T> future = new CompletableFuture<>();
		this.serverTasks.add(() -> {
			try {
				future.complete(function.apply((EntityPlayerMP) MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0)));
			} catch (final RuntimeException exception) {
				future.completeExceptionally(exception);
			}
		});
		return MinecraftClientTestPilot.await(future, "computing on the server", 120);
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

	private static String keysym(final Key key) {
		switch (key) {
			case ESCAPE:
				return "Escape";
			case LEFT_SHIFT:
				return "shift";
			default:
				if (key.name().startsWith("DIGIT_")) {
					return key.name().substring(6);
				}
				return key.name().length() == 1 ? key.name().toLowerCase() : key.name();
		}
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

	private static ItemStack stack(final ClientTestItem item, final int count) {
		switch (item) {
			case APPLE:
				return new ItemStack(Items.apple, count);
			case DIAMOND:
				return new ItemStack(Items.diamond, count);
			case DIAMOND_SWORD:
				return new ItemStack(Items.diamond_sword, count);
			case EMERALD:
				return new ItemStack(Items.emerald, count);
			case ENDER_PEARL:
				return new ItemStack(Items.ender_pearl, count);
			case FURNACE:
				return new ItemStack(Blocks.furnace, count);
			case GOLDEN_APPLE:
				return new ItemStack(Items.golden_apple, count);
			case SNOW_BLOCK:
				return new ItemStack(Blocks.snow, count);
			default:
				return new ItemStack(Blocks.stone, count);
		}
	}

	private static String texture(final ClientTestTexture texture) {
		switch (texture) {
			case ANIMATED_BLOCK:
				return "textures/blocks/water_still.png";
			case DIAMOND_BLOCK:
				return "textures/blocks/diamond_block.png";
			default:
				return "textures/items/diamond.png";
		}
	}

	private static int[] describe(final ItemStack carried, final Container container) {
		final int[] counts = new int[container.inventorySlots.size() + 1];
		counts[0] = MinecraftClientTestPilot.count(carried);
		for (int index = 0; index < container.inventorySlots.size(); index++) {
			counts[index + 1] = MinecraftClientTestPilot.count(container.getSlot(index).getStack());
		}
		return counts;
	}

	private static int count(final ItemStack stack) {
		return stack == null ? 0 : stack.stackSize;
	}

	private static DrawableRaster itemRaster(final ItemStack stack) {
		return GuiDrawableRaster.of((width, height) -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			final float scale = Math.min(width, height) / 16F;
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glScalef(scale, scale, scale);
			MinecraftClientTestPilot.RENDER.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), stack, 0, 0);
		});
	}

	private static <T> T await(final Future<T> future, final String label, final int seconds) {
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