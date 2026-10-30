package dev.joid.backend.minecraft.demo.test.scenario;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.backend.minecraft.demo.test.ClientTestBounds;
import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestImages;
import dev.joid.backend.minecraft.demo.test.ClientTestLog;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.demo.ui.DemoEntry;
import dev.joid.demo.ui.UIDemo;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.demo.ui.cursor.UIDemoCursor;
import dev.joid.demo.ui.event.UIDemoEventFlow;
import dev.joid.demo.ui.font.UIDemoFont;
import dev.joid.demo.ui.node.impl.control.checkbox.DemoCheckboxNode;
import dev.joid.demo.ui.shader.UIDemoEffect;
import dev.joid.demo.ui.shader.UIDemoShader;
import dev.joid.demo.ui.simple.UIDemoSimple;
import dev.joid.lib.JOID;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScreenScenarios {

	public static void verifyDemoChoice(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openChoice();
		context.capture("joid-demo-choice");
		ScreenScenarios.expectInterfaceScale(context, ClientTestDemos.CHOICE, 2, 0.5D, "joid-demo-choice-gui-scale-2");
		ScreenScenarios.expectInterfaceScale(context, ClientTestDemos.CHOICE, 0, 1D, "joid-demo-choice-gui-scale-auto");
		context.clickEntry("UIDemoSimple");
		context.waitFor("UIDemoSimple opened by a click on UIDemoMinecraftChoice", () -> JOID.isOpen(UIDemoSimple.class), 30);
		context.escape();
		context.waitFor("UIDemoMinecraftChoice opened again by Escape on UIDemoSimple", () -> !JOID.isOpen(UIDemoSimple.class) && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)), 30);
		context.check(!context.test(() -> JOID.isOpen(UIDemoChoice.class)), "Escape on UIDemoSimple opens UIDemoChoice instead of UIDemoMinecraftChoice");
		context.clickEntry("UIDemoMinecraft");
		context.capture("joid-demo-choice-minecraft");
		context.check(context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.MINECRAFT))), "clicking the entry UIDemoMinecraft of UIDemoMinecraftChoice did not open it");
		final int[] card = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.MINECRAFT));
		ClientTestImages.expectColor(context, "first card of UIDemoMinecraft", context.capture("joid-demo-minecraft-first-card"), card, 0xDDDDDD, true);
		context.check(!context.test(pilot::isGamePaused), "UIDemoMinecraft, declared with pause = false, pauses the game");
		context.escape();
		context.waitFor("UIDemoMinecraftChoice opened again by Escape on UIDemoMinecraft", () -> !JOID.isOpen(ClientTestDemos.type(ClientTestDemos.MINECRAFT)) && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)), 30);
		context.closeScreens();
	}

	public static void verifyDemoCaptures(final ClientTestContext context) {
		for (final UI ui : Arrays.asList(new UIDemoFont(), new UIDemoShader(), new UIDemoEffect(), ClientTestDemos.create(ClientTestDemos.MINECRAFT))) {
			context.openOver(ui);
			context.waitTicks(20);
			context.capture("joid-demo-" + ui.getClass().getSimpleName().substring(6).toLowerCase());
			context.closeScreens();
		}
	}

	public static void verifyMinecraftData(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.check(!context.test(pilot::isPauseScreen), "the screen of UIDemoMinecraft pauses the game against its UIDataMinecraft");
		context.closeScreens();
		context.open(pilot.createBackgroundUi(true));
		context.check(context.test(pilot::isPauseScreen), "a UI without UIDataMinecraft does not pause the game");
		context.closeScreens();
	}

	public static void verifyScreenTitle(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		final String title = context.compute(pilot::getScreenTitle);
		context.check(title.equals("Minecraft demo"), "the screen of UIDemoMinecraft has the title " + title + " instead of the one of its UIDataMinecraft");
		context.closeScreens();
		context.open(pilot.createBackgroundUi(true));
		final String empty = context.compute(pilot::getScreenTitle);
		context.check(empty.isEmpty(), "a UI without UIDataMinecraft has the title " + empty);
		context.closeScreens();
	}

	public static void verifyScale(final ClientTestContext context) {
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		ScreenScenarios.clickToggle(context, 2);
		ScreenScenarios.expectScale(context, "clicking the active toggle", false, false, 1D);
		ScreenScenarios.clickToggle(context, 2);
		ScreenScenarios.clickToggle(context, 1);
		ScreenScenarios.expectScale(context, "clicking the limit toggle", true, true, 0.75D);
		context.capture("joid-demo-minecraft-scale-limited");
		ScreenScenarios.clickToggle(context, 1);
		ScreenScenarios.expectScale(context, "clicking the limit toggle again", true, false, 1D);
		for (final int guiScale : new int[] {2, 0}) {
			for (final boolean scaled : new boolean[] {true, false}) {
				context.run(() -> JOID.getUi(ClientTestDemos.type(ClientTestDemos.MINECRAFT)).getScale().active(scaled));
				context.guiScale(guiScale);
				context.waitTicks(20);
				context.capture("joid-demo-minecraft-scale-gui-" + guiScale + "-" + (scaled ? "active" : "inactive"));
				ScreenScenarios.expectScale(context, "the GUI scale " + guiScale, scaled, false, scaled && guiScale == 2 ? 0.5D : 1D);
			}
		}

		context.run(() -> JOID.getUi(ClientTestDemos.type(ClientTestDemos.MINECRAFT)).getScale().active(true));
		context.closeScreens();
	}

	public static void verifyBackground(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.waitTicks(5);
		final BufferedImage world = context.capture("joid-background-world");
		final BufferedImage vanilla = ScreenScenarios.captureBackground(context, pilot.createBackgroundUi(true), "joid-background-vanilla");
		final BufferedImage none = ScreenScenarios.captureBackground(context, pilot.createBackgroundUi(false), "joid-background-none");
		final double darkened = ClientTestImages.difference(world, vanilla);
		final double unchanged = ClientTestImages.difference(world, none);
		context.check(darkened >= 20D, "the default background differs from the world by " + darkened + " levels on average, the vanilla menu background is missing");
		context.check(unchanged <= 4D, "the background disabled by UIDataMinecraft differs from the world by " + unchanged + " levels on average");
		System.out.println("[JOID] Background: vanilla " + darkened + ", none " + unchanged + " levels from the world");
	}

	public static void verifySubtitles(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final int[] subtitles = {1400, 640, 520, 440};
		context.run(() -> pilot.setSubtitles(true));
		for (final boolean background : new boolean[] {true, false}) {
			context.open(pilot.createBackgroundUi(background));
			final BufferedImage image = context.capture("joid-subtitles-" + background);
			context.run(pilot::playInterfaceSound);
			context.waitTicks(5);
			final BufferedImage subtitled = context.capture("joid-subtitles-" + background + "-played");
			ClientTestImages.expectChange(context, "subtitles over a UI with the background " + background, Arrays.asList(image, subtitled), subtitles, false);
			context.closeScreens();
		}
		context.run(() -> pilot.setSubtitles(false));
	}

	public static void verifyTooltip(final ClientTestContext context) {
		context.openChoice();
		final int[] entry = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.CHOICE));
		final int scale = context.compute(context.getPilot()::getGuiScale);
		final int mouseX = (entry[0] / scale + 6) * scale;
		final int mouseY = ((entry[1] + entry[3]) / scale - 2) * scale;
		final int[] region = {mouseX + 10 * scale, mouseY - 12 * scale, 2 * scale, 8 * scale};
		context.moveMouse(4D, 4D);
		final int before = ScreenScenarios.countTooltipPixels(context.capture("joid-tooltip-none"), region);
		context.moveMouse(mouseX, mouseY);
		context.waitTicks(20);
		final int after = ScreenScenarios.countTooltipPixels(context.capture("joid-tooltip"), region);
		context.check(before == 0 && after * 2 >= region[2] * region[3], "the tooltip of a demo entry is not drawn by the game: " + before + " tooltip pixels before the hover and " + after + " of " + region[2] * region[3] + " after, at " + Arrays.toString(region));
		context.moveMouse(4D, 4D);
		context.closeScreens();
	}

	public static void verifyDemos(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final List<DemoEntry> entries = ClientTestDemos.entries().stream().filter(entry -> !entry.getLabel().endsWith("Store")).collect(Collectors.toList());
		for (final DemoEntry entry : entries) {
			context.openChoice();
			final int errors = ClientTestLog.countErrors();
			context.run(entry.getAction());
			context.waitTicks(40);
			context.capture("joid-demos-" + entry.getLabel());
			if (!context.test(() -> ScreenScenarios.isShown(entry))) {
				context.fail(entry.getLabel() + " is not shown");
			}

			if (ClientTestLog.countErrors() != errors) {
				context.fail(entry.getLabel() + " printed errors: " + String.join(System.lineSeparator(), ClientTestLog.getErrorsSince(errors)));
			}

			if (entry.getState() != null) {
				context.run(entry.getAction());
			} else if (UIDemo.class.isAssignableFrom(ClientTestDemos.entryType(entry)) || entry.getHover().equals(ClientTestDemos.CONTAINER)) {
				if (ClientTestDemos.entryType(entry) == UIDemoEventFlow.class) {
					context.click(context.compute(() -> ClientTestBounds.all(JOID.getUi(UIDemoEventFlow.class), DemoCheckboxNode.class)).get(1));
				}

				for (int attempt = 0; attempt < 3 && context.test(() -> ScreenScenarios.isShown(entry)); attempt++) {
					context.press(Key.ESCAPE);
					for (int tick = 0; tick < 60 && context.test(() -> ScreenScenarios.isShown(entry)); tick += 5) {
						context.waitTicks(5);
					}
				}

				context.waitTicks(10);
				if (!context.test(() -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)) && !JOID.isOpen(UIDemoChoice.class))) {
					context.fail(entry.getLabel() + " does not come back to UIDemoMinecraftChoice on Escape");
				}
			}

			for (int attempt = 0; attempt < 6 && context.test(pilot::isScreenOpen); attempt++) {
				context.press(Key.ESCAPE);
				context.waitTicks(20);
			}

			if (context.test(pilot::isScreenOpen)) {
				context.fail(entry.getLabel() + " keeps a screen open after Escape");
				context.run(pilot::closeScreen);
				context.waitTicks(10);
			}
		}
	}

	public static void verifyCursor(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openChoice();
		context.clickEntry("UIDemoContainer");
		context.waitFor("the demo container", pilot::isContainerScreen, 60);
		context.waitTicks(20);
		final int[] popup = context.compute(() -> ClientTestBounds.firstRect(ClientTestDemos.CONTAINER));
		ScreenScenarios.expectCursor(context, "Popup button of the container", popup, Cursor.POINTER);
		context.escape();
		context.waitFor("UIDemoMinecraftChoice after the container", () -> pilot.isJoidScreen() && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)), 30);
		context.waitTicks(20);
		context.clickEntry("UIDemoCursor");
		final List<int[]> cells = context.compute(() -> {
			final UI ui = JOID.getUi(UIDemoCursor.class);
			final List<Node> nodes = ui.getNodeList();
			return Arrays.asList(ClientTestBounds.of(nodes.get(1).getChild(0, RectNode.class)), ClientTestBounds.of(nodes.get(2).getChild(0, RectNode.class)), new int[] {(int) ui.getView().toScreenX(410D), (int) ui.getView().toScreenY(240D), 20, 20});
		});
		ScreenScenarios.expectCursor(context, "Pointer cell of UIDemoCursor after the container", cells.get(0), Cursor.POINTER);
		ScreenScenarios.expectCursor(context, "Text cell of UIDemoCursor after the container", cells.get(1), Cursor.TEXT);
		ScreenScenarios.expectCursor(context, "gap between the cells of UIDemoCursor after the container", cells.get(2), Cursor.DEFAULT);
		context.moveMouse(960D, 540D);
		context.closeScreens();
	}

	private static void expectCursor(final ClientTestContext context, final String label, final int[] bounds, final Cursor expected) {
		context.moveTo(bounds);
		context.waitTicks(3);
		final Cursor actual = context.compute(context.getPilot()::getCursor);
		context.check(actual == expected, label + " at " + Arrays.toString(bounds) + " shows the cursor " + actual + " instead of " + expected);
	}

	private static BufferedImage captureBackground(final ClientTestContext context, final UI ui, final String name) {
		context.open(ui);
		context.waitTicks(20);
		final BufferedImage image = context.capture(name);
		context.closeScreens();
		context.waitTicks(40);
		return image;
	}

	private static void expectInterfaceScale(final ClientTestContext context, final String ui, final int guiScale, final double interfaceScale, final String name) {
		context.guiScale(guiScale);
		context.capture(name);
		final double actual = context.compute(() -> JOID.getUi(ClientTestDemos.type(ui)).getView().getInterfaceScale());
		context.check(actual == interfaceScale, "the GUI scale " + guiScale + " gives the interface scale " + actual + " instead of " + interfaceScale);
	}

	private static void clickToggle(final ClientTestContext context, final int fromEnd) {
		final List<int[]> rects = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, RectNode.class.getName()));
		context.click(rects.get(rects.size() - 6 - fromEnd));
		context.moveMouse(960D, 540D);
	}

	private static void expectScale(final ClientTestContext context, final String label, final boolean active, final boolean limited, final double interfaceScale) {
		final String actual = context.compute(() -> {
			final UI ui = JOID.getUi(ClientTestDemos.type(ClientTestDemos.MINECRAFT));
			return ui.getScale().active() + " " + ui.getScale().limited() + " " + ui.getView().getInterfaceScale();
		});
		final String expected = active + " " + limited + " " + interfaceScale;
		context.check(actual.equals(expected), label + " gives active, limited and interface scale " + actual + " instead of " + expected);
	}

	private static boolean isShown(final DemoEntry entry) {
		if (entry.getState() != null) {
			return entry.isActive();
		}
		return JOID.isOpen(ClientTestDemos.entryType(entry));
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

}