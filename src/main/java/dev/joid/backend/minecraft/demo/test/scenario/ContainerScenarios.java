package dev.joid.backend.minecraft.demo.test.scenario;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import dev.joid.backend.minecraft.bridge.ui.container.BaseContainerUIBridge;
import dev.joid.backend.minecraft.demo.test.ClientTestBounds;
import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestImages;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.lib.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.color.Color;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataPopup;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.CircleNodeEffect;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerScenarios {

	public static void verifyContainer(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final BufferedImage world = context.capture("joid-demo-container-world");
		ContainerScenarios.openContainer(context);
		pilot.setContainerSlot(0, ClientTestItem.DIAMOND, 10);
		pilot.setContainerSlot(1, ClientTestItem.GOLDEN_APPLE, 1);
		context.waitTicks(10);

		final List<int[]> slots = context.compute(ContainerScenarios::slotBounds);
		final BufferedImage opened = context.capture("joid-demo-container");
		final int[] outside = {40, 600, 500, 440};
		final double sharp = ClientTestImages.edges(world, outside);
		final double background = ClientTestImages.edges(opened, outside);
		System.out.println("[JOID] Container background: world edges " + sharp + ", behind the container " + background);
		context.check(background <= sharp * 0.6D, "the demo container with the default Minecraft background leaves the world as sharp: edges " + background + " behind the panel, " + sharp + " in the world");
		ClientTestImages.expectDrawn(context, "diamonds in the first storage slot", opened, slots.get(0));
		context.moveTo(slots.get(0));
		context.click();
		context.moveTo(slots.get(9));
		final BufferedImage carried = context.capture("joid-demo-container-carried");
		ClientTestImages.expectChange(context, "carried diamonds drawn under the mouse", Arrays.asList(opened, carried), slots.get(9), false);
		ContainerScenarios.expectContainer(context, "a left click on 10 diamonds", 10, new int[] {0, 0});

		context.holdMouse();
		context.moveMouse(slots.get(9)[0] + slots.get(9)[2] / 2D + 4D, slots.get(9)[1] + slots.get(9)[3] / 2D);
		context.moveTo(slots.get(10));
		context.moveTo(slots.get(11));
		context.capture("joid-demo-container-drag");
		context.releaseMouse();
		context.waitTicks(5);
		ContainerScenarios.expectContainer(context, "a left drag of 10 diamonds over three slots", 1, new int[] {9, 3}, new int[] {10, 3}, new int[] {11, 3});

		context.click(slots.get(12));
		context.waitTicks(5);
		ContainerScenarios.expectContainer(context, "a left click with 1 diamond on an empty slot", 0, new int[] {12, 1});

		context.moveTo(slots.get(9));
		context.holdKey(Key.LEFT_SHIFT);
		context.click();
		context.releaseKey(Key.LEFT_SHIFT);
		context.waitTicks(5);
		ContainerScenarios.expectContainer(context, "a shift-click on 3 diamonds", 0, new int[] {9, 0}, new int[] {62, 3});

		context.moveTo(slots.get(10));
		context.press(Key.DIGIT_1);
		context.waitTicks(5);
		ContainerScenarios.expectContainer(context, "the key 1 over 3 diamonds", 0, new int[] {10, 0}, new int[] {54, 3});

		context.moveTo(slots.get(13));
		final BufferedImage unhovered = context.capture("joid-demo-container-unhovered");
		context.moveTo(slots.get(1));
		context.waitTicks(5);
		final BufferedImage tooltip = context.capture("joid-demo-container-tooltip");
		final int[] apple = slots.get(1);
		ClientTestImages.expectChange(context, "vanilla tooltip of the hovered golden apple", Arrays.asList(unhovered, tooltip), new int[] {apple[0] + apple[2], apple[1] - apple[3], apple[2] * 3, apple[3]}, false);
		context.check(context.compute(pilot::getHoveredSlot) == 1, "the hovered slot of the container screen is not the golden apple");
		context.press(Key.Q);
		context.waitTicks(5);
		ContainerScenarios.expectContainer(context, "the key Q over the golden apple", 0, new int[] {1, 0});

		ContainerScenarios.verifyPopup(context);
		ContainerScenarios.verifyPopupButton(context);
		ContainerScenarios.verifyBounds(context);
		ContainerScenarios.verifyDepth(context);

		context.press(Key.ESCAPE);
		context.waitFor("UIDemoMinecraftChoice after the container", () -> pilot.isJoidScreen() && !pilot.isContainerScreen(), 30);
		context.waitTicks(5);
		context.check(!pilot.isDemoContainerOpen(), "Escape does not close the container on the server");
		context.check(context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)) && !JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CONTAINER))), "Escape on the container does not come back to UIDemoMinecraftChoice");

		context.waitTicks(20);
		context.clickEntry("UIDemoContainer");
		context.waitFor("the demo container opened again", pilot::isContainerScreen, 60);
		context.waitTicks(20);
		context.press(Key.E);
		context.waitFor("UIDemoMinecraftChoice after the inventory key", () -> pilot.isJoidScreen() && !pilot.isContainerScreen(), 30);
		context.waitTicks(5);
		context.check(!pilot.isDemoContainerOpen(), "the inventory key does not close the container on the server");
		context.check(context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)) && !JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CONTAINER))), "the inventory key on the container does not come back to UIDemoMinecraftChoice");
		context.moveMouse(960D, 540D);
		context.closeScreens();
	}

	public static void verifyContainerServer(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.check(!pilot.canOpenDemoContainer(0), "the demo container opens for a player who is not an operator");
		context.check(!pilot.canOpenDemoContainer(1), "the demo container opens for an operator of level 1");
		context.check(pilot.canOpenDemoContainer(2), "the demo container does not open for an operator of level 2");
		final int slots = pilot.openDemoContainer();
		if (slots < 0) {
			context.fail("the demo container does not open for the owner of the world");
			return;
		}

		context.check(slots == 63, "the demo container has " + slots + " slots instead of 63");
		pilot.setContainerSlot(0, ClientTestItem.EMERALD, 5);
		pilot.quickMoveDemoContainer(0);
		final int[] moved = pilot.readContainer(true);
		context.check(moved[1] == 0 && moved[63] == 5, "a shift-click on a storage slot does not move its 5 emeralds to the last hotbar slot");
		pilot.quickMoveDemoContainer(62);
		context.check(pilot.readContainer(true)[1] == 5, "a shift-click on the hotbar does not move the 5 emeralds back to the first storage slot");
		pilot.clickDemoContainer(0, 0);
		pilot.clickDemoContainer(1, 1);
		final int[] clicked = pilot.readContainer(true);
		context.check(clicked[2] == 1 && clicked[0] == 4, "a right click places " + clicked[2] + " emerald and carries " + clicked[0] + " instead of 1 and 4");
		pilot.closeDemoContainer();
		final int stored = pilot.countDemoStorage(1);
		context.check(stored == 1, "closing the demo container keeps " + stored + " emeralds in the storage instead of 1");
		context.waitFor("the game without screen", () -> !pilot.isScreenOpen(), 30);
	}

	public static void verifyContainerClose(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		if (pilot.openDemoContainer() < 0) {
			context.fail("the demo container does not open for the owner of the world");
			return;
		}

		pilot.setContainerSlot(0, ClientTestItem.EMERALD, 4);
		pilot.clickDemoContainer(0, 0);
		pilot.closeDemoContainer();
		final int inventory = pilot.countInventory(ClientTestItem.EMERALD);
		context.check(inventory == 4, "closing the demo container gives back " + inventory + " carried emeralds instead of 4");
		context.waitFor("the game without screen", () -> !pilot.isScreenOpen(), 30);
	}

	private static void openContainer(final ClientTestContext context) {
		context.openChoice();
		context.clickEntry("UIDemoContainer");
		context.waitFor("the demo container", context.getPilot()::isContainerScreen, 60);
		context.waitTicks(20);
		context.check(context.getPilot().isDemoContainerOpen(), "the payload of UIDemoContainer did not open the demo container on the server");
	}

	private static void verifyPopup(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		pilot.setContainerSlot(2, ClientTestItem.DIAMOND, 5);
		context.waitTicks(10);
		final Object screen = context.compute(pilot::getScreen);
		final ContainerPopup popup = new ContainerPopup();
		context.run(() -> JOID.open(popup));
		context.waitTicks(20);
		final int[] button = context.compute(() -> ClientTestBounds.of(popup.getNodeList().get(0)));
		context.moveTo(button);
		context.capture("joid-demo-container-popup");
		context.check(context.test(() -> pilot.getScreen() == screen && JOID.isOpen(popup) && ContainerScenarios.containerBridge().isOpen(popup)), "the popup replaced the container screen instead of opening over it");
		context.check(context.compute(pilot::getHoveredSlot) < 0, "the slot under the popup is hovered");
		context.click();
		context.press(Key.E);
		context.waitTicks(5);
		context.check(popup.getClicks() == 1, "a click on the popup gives " + popup.getClicks() + " popup clicks instead of 1");
		ContainerScenarios.expectContainer(context, "a click on the popup over 5 diamonds", 0, new int[] {2, 5});
		context.check(context.test(() -> pilot.getScreen() == screen), "the key E closed the container under the popup");
		context.press(Key.ESCAPE);
		context.waitTicks(20);
		context.check(!context.test(() -> JOID.isOpen(popup) || pilot.getScreen() != screen), "Escape does not close the popup alone");
		context.check(pilot.isDemoContainerOpen(), "the container is closed on the server after the popup");
		context.moveTo(button);
		context.click();
		ContainerScenarios.expectContainer(context, "a left click on 5 diamonds after the popup", 5, new int[] {2, 0});
		context.click();
		ContainerScenarios.expectContainer(context, "a left click with 5 diamonds after the popup", 0, new int[] {2, 5});
		context.run(() -> JOID.open(ClientTestDemos.create(ClientTestDemos.CHOICE)));
		context.waitTicks(20);
		context.check(context.test(() -> pilot.getScreen() == screen && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE))), "UIDemoMinecraftChoice replaced the container screen instead of opening over it");
		context.press(Key.ESCAPE);
		context.waitTicks(20);
		context.check(!context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)) || pilot.getScreen() != screen), "Escape does not close UIDemoMinecraftChoice alone over the container");
	}

	private static void verifyPopupButton(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final Object screen = context.compute(pilot::getScreen);
		final int[] button = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.CONTAINER));
		context.click(button);
		context.waitTicks(20);
		if (!context.test(() -> pilot.getScreen() == screen && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CONTAINER_POPUP)) && ContainerScenarios.containerBridge().isOpen(JOID.getUi(ClientTestDemos.type(ClientTestDemos.CONTAINER_POPUP))))) {
			context.fail("the Popup button did not open UIDemoContainer.Popup over the container screen");
			return;
		}

		final int[] panel = context.compute(() -> ClientTestBounds.first(ClientTestDemos.CONTAINER_POPUP));
		final int[] close = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.CONTAINER_POPUP));
		final List<int[]> slots = context.compute(ContainerScenarios::slotBounds);
		final int[] covered = slots.stream().filter(slot -> slot[0] > panel[0] && slot[1] > panel[1] && slot[0] + slot[2] < panel[0] + panel[2] && slot[1] + slot[3] < panel[1] + panel[3]).findFirst().orElseThrow(() -> new AssertionError("UIDemoContainer.Popup covers no slot"));
		context.moveTo(covered);
		final BufferedImage opened = context.capture("joid-demo-container-popup-button");
		ClientTestImages.expectColor(context, "Close button of the popup over the container", opened, close, 0x999999, true);
		context.check(context.compute(pilot::getHoveredSlot) < 0, "the slot under UIDemoContainer.Popup is hovered");
		context.click(close);
		context.waitTicks(20);
		final BufferedImage closed = context.capture("joid-demo-container-popup-closed");
		ClientTestImages.expectColor(context, "container under the closed popup", closed, close, 0x999999, false);
		context.check(!context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CONTAINER_POPUP)) || pilot.getScreen() != screen || !JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CONTAINER))), "the Close button of UIDemoContainer.Popup does not close the popup alone");
		context.check(pilot.isDemoContainerOpen(), "the container is closed on the server after UIDemoContainer.Popup");
		ContainerScenarios.expectContainer(context, "the container after UIDemoContainer.Popup", 0, new int[] {2, 5});
		context.click(slots.get(2));
		ContainerScenarios.expectContainer(context, "a left click on 5 diamonds after UIDemoContainer.Popup", 5, new int[] {2, 0});
		context.click();
		ContainerScenarios.expectContainer(context, "a left click with 5 diamonds after UIDemoContainer.Popup", 0, new int[] {2, 5});
	}

	private static void verifyBounds(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.check(!context.test(pilot::isPauseScreen), "the container screen pauses the game");
		final int[] panel = context.compute(() -> ClientTestBounds.first(ClientTestDemos.CONTAINER));
		final int[] image = context.compute(pilot::getContainerScreenBounds);
		final int scale = context.compute(pilot::getGuiScale);
		for (int index = 0; index < 4; index++) {
			if (Math.abs(image[index] - panel[index]) >= scale * 2) {
				context.fail("the container screen bounds " + Arrays.toString(image) + " do not follow the panel " + Arrays.toString(panel));
				break;
			}
		}

		pilot.setContainerSlot(3, ClientTestItem.DIAMOND, 7);
		context.waitTicks(10);
		final List<int[]> slots = context.compute(ContainerScenarios::slotBounds);
		context.click(slots.get(3));
		context.click(new int[] {panel[0] + 10, panel[1] + 10, 2, 2});
		ContainerScenarios.expectContainer(context, "a click in a hole of the panel with 7 diamonds", 7, new int[] {3, 0});
		context.click(new int[] {panel[0] - 60, panel[1] + panel[3] / 2, 2, 2});
		ContainerScenarios.expectContainer(context, "a click outside the panel with 7 diamonds", 0, new int[] {3, 0});
	}

	private static void verifyDepth(final ClientTestContext context) {
		final UI ui = context.compute(() -> JOID.getUi(ClientTestDemos.type(ClientTestDemos.CONTAINER)));
		final Node root = ContainerNode.create(0, 0, 1920, 1080).zindex(20);
		context.run(() -> root.attach(ui));
		context.moveMouse(4D, 4D);
		for (final boolean circle : new boolean[] {false, true}) {
			RenderScenarios.verifyDepth(context, "joid-depth-container" + (circle ? "-circle" : ""), circle ? CircleNodeEffect::create : null, 2, root);
		}
		context.run(() -> ui.remove(root));
		context.waitTicks(5);
	}

	private static void expectContainer(final ClientTestContext context, final String label, final int carried, final int[]... slots) {
		final IClientTestPilot pilot = context.getPilot();
		final String expected = ContainerScenarios.describe(null, carried, slots);
		final String client = ContainerScenarios.describe(pilot.readContainer(false), 0, slots);
		final String server = ContainerScenarios.describe(pilot.readContainer(true), 0, slots);
		context.check(server.equals(expected) && client.equals(expected), label + " gives " + server + " on the server and " + client + " on the client instead of " + expected);
	}

	private static String describe(final int[] counts, final int carried, final int[]... slots) {
		final StringBuilder description = new StringBuilder("carried " + (counts == null ? carried : counts[0]));
		for (final int[] slot : slots) {
			description.append(", slot ").append(slot[0]).append(' ').append(counts == null ? slot[1] : counts[slot[0] + 1]);
		}
		return description.toString();
	}

	private static List<int[]> slotBounds() {
		return ClientTestBounds.all(ClientTestDemos.CONTAINER, ClientTestDemos.SLOT_NODE);
	}

	private static BaseContainerUIBridge<?> containerBridge() {
		for (final UIBridge bridge : BridgeHandler.UI.getBridgeList()) {
			if (bridge instanceof BaseContainerUIBridge) {
				return (BaseContainerUIBridge<?>) bridge;
			}
		}
		throw new AssertionError("JOID has no container bridge");
	}

	@UIDataPopup(active = true)
	public static final class ContainerPopup extends UI {

		@Getter
		private int clicks;

		@Override
		public void init() {
			RectNode.create(760, 244, 108, 108).color(Color.WHITE).onClick(event -> this.clicks++).attach(this);
		}

	}

}