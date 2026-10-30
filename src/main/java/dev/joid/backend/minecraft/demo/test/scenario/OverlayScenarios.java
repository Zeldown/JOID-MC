package dev.joid.backend.minecraft.demo.test.scenario;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import dev.joid.backend.minecraft.bridge.ui.overlay.BaseOverlayUIBridge;
import dev.joid.backend.minecraft.demo.test.ClientTestBounds;
import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestImages;
import dev.joid.backend.minecraft.demo.test.ClientTestLog;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.lib.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.color.Color;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OverlayScenarios {

	public static void verifyOverlays(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		OverlayScenarios.toggleOverlays(context, true, "joid-demo-choice-overlay-on");
		final int[] hotbar = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_HOTBAR));
		final int[] experience = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_EXPERIENCE));
		final int[] interactive = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_INTERACTIVE));
		final int[] crosshair = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_CROSSHAIR));
		final int[] always = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_ALWAYS));
		final int[] button = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.OVERLAY_INTERACTIVE));
		final int[] vanillaHotbar = context.compute(pilot::getHotbarBounds);
		final BufferedImage hud = context.capture("joid-overlay-hud");
		ClientTestImages.expectColor(context, "overlay above the hotbar", hud, hotbar, 0xDDDDDD, true);
		ClientTestImages.expectColor(context, "overlay replacing the experience bar", hud, new int[] {experience[0] + experience[2] - 8, experience[1], 8, experience[3]}, 0xDDDDDD, true);
		ClientTestImages.expectColor(context, "interactive overlay in game", hud, interactive, 0xDDDDDD, true);
		ClientTestImages.expectColor(context, "overlay under the crosshair", hud, crosshair, 0xDDDDDD, true);
		ClientTestImages.expectColor(context, "vanilla crosshair over the overlay", hud, new int[] {954, 534, 8, 8}, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "overlay always shown in game", hud, always, 0xDDDDDD, true);
		context.check(OverlayScenarios.isCancelled(context, "CONTEXTUAL_BAR"), "the overlay replacing the experience bar does not cancel the contextual bar");

		final UI hidden = context.compute(pilot::createHiddenHotbarUi);
		context.run(() -> JOID.open(hidden));
		context.waitTicks(5);
		final BufferedImage withoutHotbar = context.capture("joid-overlay-hotbar-hidden");
		ClientTestImages.expectChange(context, "vanilla hotbar cancelled by an overlay", Arrays.asList(hud, withoutHotbar), vanillaHotbar, false);
		context.run(() -> JOID.close(hidden));
		context.waitTicks(5);
		final BufferedImage restored = context.capture("joid-overlay-hotbar-restored");
		ClientTestImages.expectChange(context, "vanilla hotbar after closing the cancelling overlay", Arrays.asList(withoutHotbar, restored), vanillaHotbar, false);

		context.press(Key.F1);
		context.waitTicks(5);
		final BufferedImage hiddenGui = context.capture("joid-overlay-hidden-gui");
		ClientTestImages.expectColor(context, "overlay above the hotbar with the GUI hidden", hiddenGui, hotbar, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "interactive overlay with the GUI hidden", hiddenGui, interactive, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "overlay under the crosshair with the GUI hidden", hiddenGui, crosshair, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "overlay always shown with the GUI hidden", hiddenGui, always, 0xDDDDDD, true);
		context.check(context.test(() -> OverlayScenarios.overlayBridge().isOverlayHidden()), "F1 does not hide the overlays");
		context.press(Key.F1);
		context.waitTicks(5);

		context.press(Key.E);
		context.waitFor("the inventory", pilot::isInventoryScreen, 30);
		context.waitTicks(10);
		final BufferedImage inventory = context.capture("joid-overlay-inventory");
		ClientTestImages.expectColor(context, "interactive overlay over the inventory", inventory, interactive, 0xDDDDDD, true);
		ClientTestImages.expectColor(context, "overlay always shown over the inventory", inventory, always, 0xDDDDDD, false);
		context.run(() -> JOID.close(JOID.getUi(ClientTestDemos.type(ClientTestDemos.OVERLAY_HOTBAR))));
		context.waitTicks(5);
		final BufferedImage inventoryWithoutHotbar = context.capture("joid-overlay-inventory-without-hotbar-overlay");
		ClientTestImages.expectChange(context, "overlay above the hotbar kept in the HUD under the inventory", Arrays.asList(inventory, inventoryWithoutHotbar), hotbar, false);
		context.run(() -> JOID.open(ClientTestDemos.create(ClientTestDemos.OVERLAY_HOTBAR)));
		context.waitTicks(5);
		context.check(OverlayScenarios.isCancelled(context, "CONTEXTUAL_BAR"), "the overlay replacing the experience bar does not cancel the contextual bar under the inventory");
		final UI hiddenUnder = context.compute(pilot::createHiddenHotbarUi);
		context.run(() -> JOID.open(hiddenUnder));
		context.waitTicks(5);
		final BufferedImage inventoryHidden = context.capture("joid-overlay-inventory-hotbar-hidden");
		ClientTestImages.expectChange(context, "vanilla hotbar cancelled by an overlay under the inventory", Arrays.asList(inventory, inventoryHidden), vanillaHotbar, false);
		context.run(() -> JOID.close(hiddenUnder));
		context.waitTicks(5);
		context.moveTo(button);
		context.waitTicks(5);
		context.capture("joid-overlay-inventory-hover");
		final Cursor cursor = context.compute(pilot::getCursor);
		context.check(cursor == Cursor.POINTER, "hovering the interactive overlay shows the cursor " + cursor + " instead of the pointer");
		context.click();
		context.moveMouse(960D, 40D);
		context.click();
		final int clicks = context.compute(() -> (int) ClientTestDemos.field(JOID.getUi(ClientTestDemos.type(ClientTestDemos.OVERLAY_INTERACTIVE)), "clicks"));
		context.check(clicks == 1, "a click on the interactive overlay then one beside it give " + clicks + " overlay clicks instead of 1");
		context.capture("joid-overlay-inventory-clicked");
		context.escape();
		context.waitFor("the inventory to close", () -> !pilot.isScreenOpen(), 30);

		context.guiScale(2);
		context.capture("joid-overlay-gui-scale-2");
		final double interfaceScale = context.compute(() -> JOID.getUi(ClientTestDemos.type(ClientTestDemos.OVERLAY_HOTBAR)).getView().getInterfaceScale());
		context.check(interfaceScale == 0.5D, "the GUI scale 2 gives the overlays the interface scale " + interfaceScale + " instead of 0.5");
		context.guiScale(0);

		OverlayScenarios.toggleOverlays(context, false, "joid-demo-choice-overlay-off");
		final BufferedImage off = context.capture("joid-overlay-off");
		ClientTestImages.expectColor(context, "overlay above the hotbar toggled off", off, hotbar, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "interactive overlay toggled off", off, interactive, 0xDDDDDD, false);
		ClientTestImages.expectColor(context, "overlay always shown toggled off", off, always, 0xDDDDDD, false);
	}

	public static void verifyScreenInput(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		OverlayScenarios.toggleOverlays(context, true, "joid-screen-input-overlays");
		pilot.giveItem(0, ClientTestItem.DIAMOND, 3);
		context.waitTicks(20);
		context.press(Key.E);
		context.waitFor("the inventory", pilot::isInventoryScreen, 30);
		context.waitTicks(20);
		final int[] slot = context.compute(() -> pilot.getInventorySlotBounds(36));
		final int[] button = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.OVERLAY_INTERACTIVE));
		final int before = context.compute(() -> (int) ClientTestDemos.field(JOID.getUi(ClientTestDemos.type(ClientTestDemos.OVERLAY_INTERACTIVE)), "clicks"));
		context.click(slot);
		OverlayScenarios.expectCarried(context, "a click on 3 diamonds of the inventory", 3);
		context.click(button);
		final int clicks = context.compute(() -> (int) ClientTestDemos.field(JOID.getUi(ClientTestDemos.type(ClientTestDemos.OVERLAY_INTERACTIVE)), "clicks")) - before;
		context.check(clicks == 1, "a click on the interactive overlay over the inventory gives " + clicks + " overlay clicks instead of 1");
		OverlayScenarios.expectCarried(context, "a click on the interactive overlay carrying 3 diamonds", 3);
		context.click(slot);
		OverlayScenarios.expectCarried(context, "a click back on the slot of the inventory", 0);
		context.capture("joid-screen-input-inventory");
		context.escape();
		context.waitFor("the inventory to close", () -> !pilot.isScreenOpen(), 30);
		OverlayScenarios.toggleOverlays(context, false, "joid-screen-input-overlays-off");
	}

	public static void verifyReloadKeys(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		OverlayScenarios.toggleOverlays(context, true, "joid-reload-keys-overlays");
		final int errors = ClientTestLog.countErrors();
		final int reloads = context.compute(pilot::countReloads);
		context.holdKey(Key.F3);
		context.press(Key.T);
		context.releaseKey(Key.F3);
		context.waitFor("the reload started by F3+T", () -> pilot.countReloads() > reloads, 120);
		context.waitFor("the end of the reload started by F3+T", () -> !pilot.isReloading(), 600);
		context.waitTicks(60);
		context.closeScreens();
		if (context.test(pilot::isDebugScreenShown)) {
			context.press(Key.F3);
		}

		context.check(ClientTestLog.countErrors() == errors, "F3+T printed errors: " + String.join(System.lineSeparator(), ClientTestLog.getErrorsSince(errors)));
		final int[] hotbar = context.compute(() -> ClientTestBounds.first(ClientTestDemos.OVERLAY_HOTBAR));
		ClientTestImages.expectColor(context, "overlay above the hotbar after F3+T", context.capture("joid-reload-keys"), hotbar, 0xDDDDDD, true);
		OverlayScenarios.toggleOverlays(context, false, "joid-reload-keys-overlays-off");
		HostScenarios.verifyAudioContext(context);
	}

	public static void verifyFrameCounter(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final FrameOverlay overlay = new FrameOverlay();
		context.run(() -> JOID.open(overlay));
		context.waitTicks(5);
		final int[] counts = context.compute(() -> {
			final int updates = overlay.getUpdates();
			final int targets = pilot.countRenderTargets();
			for (int frame = 0; frame < 3; frame++) {
				pilot.drawHudFrame();
			}
			return new int[] {overlay.getUpdates() - updates, pilot.countRenderTargets() - targets};
		});
		context.run(() -> JOID.close(overlay));
		context.waitTicks(5);
		context.check(counts[0] == 3 && counts[1] == 0, "three frames of the same frame time give " + counts[0] + " overlay updates instead of 3 and " + counts[1] + " new render targets instead of 0");
	}

	private static void toggleOverlays(final ClientTestContext context, final boolean open, final String name) {
		final IClientTestPilot pilot = context.getPilot();
		if (context.test(() -> (boolean) ClientTestDemos.invoke(ClientTestDemos.OVERLAY, "isOpen")) == open) {
			return;
		}

		context.openChoice();
		context.clickEntry("UIDemoOverlayLayer");
		context.capture(name);
		final boolean toggled = context.test(() -> {
			for (final String overlay : Arrays.asList(ClientTestDemos.OVERLAY_HOTBAR, ClientTestDemos.OVERLAY_EXPERIENCE, ClientTestDemos.OVERLAY_CROSSHAIR, ClientTestDemos.OVERLAY_INTERACTIVE, ClientTestDemos.OVERLAY_ALWAYS)) {
				if (JOID.isOpen(ClientTestDemos.type(overlay)) != open) {
					return false;
				}
			}
			return true;
		});
		context.check(toggled, "the overlay entry of UIDemoMinecraftChoice did not toggle the demo overlays " + (open ? "on" : "off"));
		context.escape();
		context.waitFor("the game without screen", () -> !pilot.isScreenOpen(), 30);
		context.waitTicks(10);
	}

	private static boolean isCancelled(final ClientTestContext context, final String layer) {
		return context.test(() -> ((BaseOverlayUIBridge<?, Object>) OverlayScenarios.overlayBridge()).isCancelled(ClientTestDemos.layer(layer)));
	}

	private static BaseOverlayUIBridge<?, ?> overlayBridge() {
		for (final UIBridge bridge : BridgeHandler.UI.getBridgeList()) {
			if (bridge instanceof BaseOverlayUIBridge) {
				return (BaseOverlayUIBridge<?, ?>) bridge;
			}
		}
		throw new AssertionError("JOID has no overlay bridge");
	}

	private static void expectCarried(final ClientTestContext context, final String label, final int count) {
		final IClientTestPilot pilot = context.getPilot();
		final int client = pilot.readContainer(false)[0];
		final int server = pilot.readContainer(true)[0];
		context.check(client == count && server == count, label + " carries " + server + " on the server and " + client + " on the client instead of " + count);
	}

	@UIDataOverlay(active = true)
	public static final class FrameOverlay extends UI {

		@Getter
		private int updates;

		@Override
		public void init() {
			RectNode.create(0, 0, 8, 8).color(Color.TRANSPARENT).onUpdate(event -> this.updates++).attach(this);
		}

	}

}