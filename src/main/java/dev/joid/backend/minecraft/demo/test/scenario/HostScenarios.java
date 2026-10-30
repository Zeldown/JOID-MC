package dev.joid.backend.minecraft.demo.test.scenario;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestLog;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.backend.minecraft.demo.test.object.ClientTestVolume;
import dev.joid.demo.ui.resource.UIDemoPlayer;
import dev.joid.lib.JOID;
import dev.joid.lib.JOIDEnv;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.audio.IAudioSource;
import dev.joid.lib.bridge.clock.IClockBridge;
import dev.joid.lib.font.converter.TextConverter;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.resource.decoder.impl.VideoResourceDecoder;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.resource.ResourcePlayerNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HostScenarios {

	public static void verifyKeyBindLabels(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.run(pilot::openControls);
		context.waitFor("the controls screen", pilot::isScreenOpen, 30);
		context.waitTicks(5);
		final List<String> labels = context.compute(pilot::getControlsLabels);
		context.waitTicks(5);
		context.capture("joid-controls");
		context.escape();
		context.waitFor("the title screen", pilot::isTitleScreen, 30);
		final int category = labels.indexOf("JOID");
		if (category < 0 || labels.indexOf("Open JOID demos") != category + 1) {
			context.fail("the controls screen does not list the category JOID with Open JOID demos: " + labels.subList(Math.max(0, labels.size() - 4), labels.size()));
		}

		final List<String> french = context.compute(() -> Arrays.asList(pilot.translate("fr_fr", "key.category.joid.main"), pilot.translate("fr_fr", "key.joid.demo")));
		context.check(french.equals(Arrays.asList("JOID", "Ouvrir les démos JOID")), "the French labels of the demo key are " + french);
	}

	public static void verifyBackend(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final List<String> order = context.compute(() -> BridgeHandler.UI.getBridgeList().stream().map(bridge -> bridge.getClass().getSimpleName()).collect(Collectors.toList()));
		context.check(order.equals(Arrays.asList("ScreenUIBridge", "ContainerUIBridge", "OverlayUIBridge")), "the UI bridges of MinecraftBackend are ordered from the lowest priority as " + order);
		context.check(context.test(() -> pilot.isBackendRegistered() && JOID.inst().isReady()), "JOID is not ready after the registration of MinecraftBackend");
		final File data = context.compute(JOIDEnv::getDataDir);
		context.check(data.getAbsoluteFile().equals(new File(pilot.getGameDirectory(), "config/joid").getAbsoluteFile()), "the data folder of JOID is " + data);
		context.check(JOIDEnv.isDevMode() && JOIDEnv.isDemoMode(), "the repository runs JOID with the dev mode " + JOIDEnv.isDevMode() + " and the demo mode " + JOIDEnv.isDemoMode());
		final List<String> missing = context.compute(() -> {
			final List<String> names = new ArrayList<>();
			if (BridgeHandler.RENDER.get() == null || BridgeHandler.WINDOW.get() == null || BridgeHandler.AUDIO.get() == null || BridgeHandler.CLOCK.get() == null) {
				names.add("render, window, audio or clock bridge");
			}

			if (BridgeHandler.THREAD.get() == null || !BridgeHandler.THREAD.get().getClass().getSimpleName().equals("MinecraftThreadBridge") || !BridgeHandler.THREAD.get().isRenderThread()) {
				names.add("MinecraftThreadBridge on the thread of the game");
			}
			return names;
		});
		context.check(missing.isEmpty(), "JOID started without " + String.join(", ", missing));
	}

	public static void verifyStencil(final ClientTestContext context) {
		final int bits = context.compute(context.getPilot()::getStencilBits);
		context.check(bits == 8, "the screen of the game has " + bits + " stencil bits instead of 8");
	}

	public static void verifyAudioContext(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final String failure = context.compute(() -> {
			final Object game = pilot.getAudioContext();
			final IAudioSource source = BridgeHandler.AUDIO.get().createSource(44100, 1);
			try {
				source.write(new short[4410]);
				source.play();
				final int error = pilot.getAudioError();
				if (error != 0 || game == null || pilot.getAudioContext() != game) {
					return "a JOID source plays with the OpenAL error " + error + " or out of the context of the game";
				}
			} finally {
				source.delete();
			}
			return null;
		});
		context.check(failure == null, failure);
	}

	public static void verifyKeyBind(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final Key key = context.compute(() -> KeyResolver.resolve(pilot.getKeyMapping("key.joid.demo")));
		context.check(key == Key.J, "the key binding key.joid.demo resolves to " + key + " instead of J");
		context.press(Key.J);
		context.waitFor("UIDemoMinecraftChoice opened by the key J", () -> pilot.isJoidScreen() && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)), 30);
		context.waitTicks(20);
		context.escape();
		context.waitFor("the game without screen", () -> !pilot.isScreenOpen(), 30);
	}

	public static void verifyDebugKey(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openChoice();
		context.press(Key.F3);
		context.waitTicks(5);
		final boolean panel = context.test(() -> {
			final UI ui = JOID.getUi(ClientTestDemos.type(ClientTestDemos.CHOICE));
			return ui.getNodeList().contains(ui.getDevNode());
		});
		final boolean hidden = context.test(() -> !pilot.isDebugScreenShown());
		context.press(Key.F3);
		context.waitTicks(5);
		context.escape();
		context.waitFor("the game without screen", () -> !pilot.isScreenOpen(), 30);
		context.press(Key.F3);
		context.waitTicks(5);
		final boolean vanilla = context.test(pilot::isDebugScreenShown);
		context.press(Key.F3);
		context.waitTicks(5);
		context.check(panel, "F3 in UIDemoMinecraftChoice does not show the dev panel of JOID");
		context.check(hidden, "F3 consumed by UIDemoMinecraftChoice also shows the debug screen of Minecraft");
		context.check(vanilla, "F3 without a JOID UI does not show the debug screen of Minecraft");
	}

	public static void verifyHostBindings(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openChoice();
		context.clickEntry("UIDemoMinecraft");
		context.waitFor("UIDemoMinecraft opened by a click on UIDemoMinecraftChoice", () -> JOID.isOpen(ClientTestDemos.type(ClientTestDemos.MINECRAFT)), 30);
		context.press(Key.E);
		context.waitTicks(40);
		context.check(context.test(() -> !JOID.isOpen(ClientTestDemos.type(ClientTestDemos.MINECRAFT)) && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE))), "the inventory key bound by UIDemoMinecraft did not close it back to UIDemoMinecraftChoice");
		final Key inventory = context.compute(() -> KeyResolver.resolve(pilot.getKeyMapping("key.inventory")));
		context.check(inventory == Key.E, "the inventory key mapping resolves to " + inventory + " instead of E");
		final String converted = context.compute(() -> TextConverter.convert(pilot.createRedChat("A")));
		context.check(converted.equals("§r§cA"), "a red chat component converts to " + converted);
		context.closeScreens();
	}

	public static void verifyAudioMaster(final ClientTestContext context) {
		HostScenarios.verifyGain(context, new double[][] {{1D, 1D}, {0.5D, 1D}, {0.8D, 1D}});
	}

	public static void verifyAudioInterface(final ClientTestContext context) {
		HostScenarios.verifyGain(context, new double[][] {{1D, 0.25D}, {0.5D, 0.25D}, {0.8D, 0.5D}});
	}

	public static void verifyShowcaseClock(final ClientTestContext context) {
		final String failure = context.compute(() -> {
			final IClockBridge clock = BridgeHandler.CLOCK.get();
			ClientTestDemos.invoke("dev.joid.backend.minecraft.showcase.ShowcaseClock", "freeze");
			final IClockBridge frozen = BridgeHandler.CLOCK.get();
			ClientTestDemos.invoke("dev.joid.backend.minecraft.showcase.ShowcaseClock", "release");
			return frozen == clock || BridgeHandler.CLOCK.get() != clock ? "ShowcaseClock.release() leaves " + BridgeHandler.CLOCK.get() + " as the clock instead of " + clock : null;
		});
		context.check(failure == null, failure);
	}

	public static void verifyWarnings(final ClientTestContext context) {
		final List<String> frames = ClientTestLog.getWarnings().stream().filter(warning -> warning.contains("ms to draw a frame")).collect(Collectors.toList());
		final List<String> others = ClientTestLog.getWarnings().stream().filter(warning -> !frames.contains(warning) && !warning.contains("is not loaded in the family of") && !warning.contains("The resource broken-")).collect(Collectors.toList());
		System.out.println("[JOID] " + frames.size() + " frame time warnings of the dev mode, llvmpipe draws on the CPU");
		context.check(others.isEmpty(), "JOID warned during the client tests:" + System.lineSeparator() + String.join(System.lineSeparator(), others));
	}

	private static void verifyGain(final ClientTestContext context, final double[][] volumes) {
		final IClientTestPilot pilot = context.getPilot();
		final UIDemoPlayer player = new UIDemoPlayer();
		final double master = context.compute(() -> pilot.getVolume(ClientTestVolume.MASTER));
		final double interfaceVolume = context.compute(() -> pilot.getVolume(ClientTestVolume.INTERFACE));
		context.openOver(player);
		context.waitFor("the audio source of UIDemoPlayer", () -> HostScenarios.audioSource(player) != 0, 1200);
		for (final double[] volume : volumes) {
			context.run(() -> {
				pilot.setVolume(ClientTestVolume.MASTER, volume[0]);
				pilot.setVolume(ClientTestVolume.INTERFACE, volume[1]);
			});
			context.waitTicks(10);
			final float gain = context.compute(() -> pilot.getAudioGain(HostScenarios.audioSource(player)));
			final float expected = (float) (volume[0] * volume[1]);
			context.check(Math.abs(gain - expected) <= 1E-4F, "master " + volume[0] + " and interface " + volume[1] + " give the video gain " + gain + " instead of " + expected);
		}

		context.run(() -> {
			pilot.setVolume(ClientTestVolume.MASTER, master);
			pilot.setVolume(ClientTestVolume.INTERFACE, interfaceVolume);
		});
		context.capture("joid-demo-player");
		context.closeScreens();
	}

	private static int audioSource(final UI ui) {
		for (final Node node : ui.getNodeList()) {
			final int source = HostScenarios.audioSource(node);
			if (source != 0) {
				return source;
			}
		}
		return 0;
	}

	private static int audioSource(final Node node) {
		if (node instanceof ResourcePlayerNode) {
			final Object decoder = ((ResourcePlayerNode) node).getPlayback();
			final Object audioPlayer = decoder instanceof VideoResourceDecoder ? ClientTestDemos.field(decoder, "audioPlayer") : null;
			final Object source = audioPlayer == null ? null : ClientTestDemos.field(audioPlayer, "source");
			return source == null ? 0 : (int) ClientTestDemos.field(source, "source");
		}

		for (final Node child : node.getChildren(Node.class)) {
			final int source = HostScenarios.audioSource(child);
			if (source != 0) {
				return source;
			}
		}
		return 0;
	}

}