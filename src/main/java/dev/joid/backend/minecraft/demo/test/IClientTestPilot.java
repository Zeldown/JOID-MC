package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import dev.joid.backend.minecraft.demo.test.object.ClientTestBlock;
import dev.joid.backend.minecraft.demo.test.object.ClientTestEntity;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.backend.minecraft.demo.test.object.ClientTestTexture;
import dev.joid.backend.minecraft.demo.test.object.ClientTestVolume;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.font.IFont;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.test.snapshot.ISnapshotBackend;

public interface IClientTestPilot {

	public String getLoader();
	public String getVersion();
	public String getGraphicsBackend();
	public List<Class<?>> getContractTests();
	public Map<ClientTestScenario, String> getExclusions();

	public void reachTitleScreen();
	public void joinWorld();
	public void stop(final boolean passed);

	public void runOnClient(final Runnable runnable);
	public <T> T computeOnClient(final Callable<T> callable, final int seconds);

	public void scroll(final double amount);
	public void pressMouse(final boolean press);
	public BufferedImage capture(final String name);
	public void moveMouse(final double x, final double y);
	public void pressKey(final Key key, final boolean press);

	public void clearInventory();
	public int openDemoContainer();
	public void closeDemoContainer();
	public boolean isDemoContainerOpen();
	public int countDemoStorage(final int slot);
	public int[] readContainer(final boolean server);
	public void quickMoveDemoContainer(final int slot);
	public boolean canOpenDemoContainer(final int level);
	public int countInventory(final ClientTestItem item);
	public void clickDemoContainer(final int slot, final int button);
	public void giveItem(final int slot, final ClientTestItem item, final int count);
	public void setContainerSlot(final int slot, final ClientTestItem item, final int count);

	public Object getScreen();
	public void closeScreen();
	public boolean isScreenOpen();
	public boolean isJoidScreen();
	public boolean isGamePaused();
	public boolean isTitleScreen();
	public boolean isPauseScreen();
	public String getScreenTitle();
	public boolean isContainerScreen();
	public boolean isInventoryScreen();
	public boolean isDebugScreenShown();

	public int getGuiScale();
	public void applyGuiScale(final int guiScale);
	public int countReloads();
	public void openControls();
	public List<String> getControlsLabels();
	public boolean isReloading();
	public void reloadResources();
	public File getGameDirectory();
	public boolean isBackendRegistered();

	public Object createRedChat(final String text);
	public Object getKeyMapping(final String name);
	public int measureText(final IFont font, final String text);
	public String translate(final String language, final String key);

	public int getAudioError();
	public int getStencilBits();
	public Object getAudioContext();
	public void startSoundCapture();
	public List<String> stopSoundCapture();
	public void playInterfaceSound();
	public String getNoteBlockSound();
	public float getAudioGain(final int source);
	public void setSubtitles(final boolean subtitles);
	public double getVolume(final ClientTestVolume volume);
	public void setVolume(final ClientTestVolume volume, final double value);

	public boolean hasEntity(final Node node);
	public boolean isOnCooldown(final ClientTestItem item);
	public Resource createSprite(final ClientTestItem item);
	public Node[] createNestedRasterNodes(final boolean feature);
	public Node createBlockNode(final double x, final double y, final double size, final ClientTestBlock block);
	public Node createItemNode(final double x, final double y, final double size, final ClientTestItem item, final boolean glint);
	public Node createEntityNode(final double x, final double y, final double width, final double height, final ClientTestEntity entity, final double yaw, final double pitch);

	public int countTextures();
	public boolean isDeleted(final ITexture texture);
	public String getTexturePath(final ClientTestTexture texture);
	public BufferedImage readTexture(final ClientTestTexture texture);

	public Cursor getCursor();
	public void drawHudFrame();
	public int getHoveredSlot();
	public int[] getHotbarBounds();
	public int countRenderTargets();
	public int[] getContainerScreenBounds();
	public int[] getHotbarSlotBounds(final int slot);
	public int[] getInventorySlotBounds(final int slot);
	public UI createBackgroundUi(final boolean background);
	public UI createHiddenHotbarUi();

	public ISnapshotBackend createSnapshotBackend();

}