package dev.joid.backend.minecraft.demo.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dev.joid.lib.JOID;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientTestPlan {

	public static void start(final IClientTestPilot pilot) {
		ClientTestLog.install();
		final Thread thread = new Thread(() -> ClientTestPlan.run(pilot), "JOID Client Tests");
		thread.setDaemon(true);
		thread.start();
	}

	public static boolean isActive() {
		return Boolean.getBoolean("joid.test");
	}

	public static Set<ClientTestScenario> getSelection() {
		final String property = System.getProperty("joid.test.scenarios", "").trim();
		if (property.isEmpty()) {
			return EnumSet.allOf(ClientTestScenario.class);
		}

		final Set<ClientTestScenario> selection = EnumSet.noneOf(ClientTestScenario.class);
		for (final String name : property.split(",")) {
			selection.add(ClientTestScenario.valueOf(name.trim()));
		}
		return selection;
	}

	private static void run(final IClientTestPilot pilot) {
		final List<String> failures = new ArrayList<>();
		final Set<ClientTestScenario> selection = ClientTestPlan.getSelection();
		final Map<ClientTestScenario, String> exclusions = pilot.getExclusions();
		System.out.println("[JOID] Client tests of Minecraft " + pilot.getVersion() + " on " + pilot.getLoader() + " with " + pilot.getGraphicsBackend() + ", snapshots of the " + pilot.getRenderPipeline() + " pipeline");
		for (final Entry<ClientTestScenario, String> exclusion : exclusions.entrySet()) {
			System.out.println("[JOID] Excluded " + exclusion.getKey() + ": " + exclusion.getValue());
		}

		pilot.runOnClient(() -> JOID.open(new ClientTestFrameUI()));
		ClientTestStage stage = null;
		for (final ClientTestScenario scenario : ClientTestScenario.values()) {
			if (!selection.contains(scenario) || exclusions.containsKey(scenario)) {
				continue;
			}

			final ClientTestContext context = new ClientTestContext(pilot, scenario, failures);
			try {
				if (scenario.getStage() != stage) {
					stage = scenario.getStage();
					if (stage == ClientTestStage.TITLE) {
						pilot.reachTitleScreen();
					} else {
						pilot.joinWorld();
					}
				}

				if (stage == ClientTestStage.WORLD) {
					ClientTestPlan.reset(context);
				}

				final int before = failures.size();
				final long start = System.nanoTime();
				System.out.println("[JOID] Client test " + scenario);
				scenario.getStep().run(context);
				System.out.println("[JOID] Client test " + scenario + " " + (failures.size() == before ? "passed" : "failed") + " in " + (System.nanoTime() - start) / 1_000_000_000L + " s");
			} catch (final Throwable throwable) {
				throwable.printStackTrace();
				context.fail("stopped on " + throwable);
				ClientTestPlan.recover(context);
			}
		}

		if (failures.isEmpty()) {
			System.out.println("[JOID] Client tests passed");
		} else {
			System.out.println("[JOID] " + failures.size() + " client test checks failed:" + System.lineSeparator() + String.join(System.lineSeparator(), failures));
		}
		pilot.stop(failures.isEmpty());
	}

	private static void reset(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.closeScreens();
		if (context.test(pilot::isDebugScreenShown)) {
			context.press(Key.F3);
		}
		pilot.clearInventory();
		context.moveMouse(960D, 540D);
	}

	private static void recover(final ClientTestContext context) {
		try {
			context.getPilot().pressMouse(false);
			for (final Key key : Arrays.asList(Key.LEFT_SHIFT, Key.F3)) {
				context.getPilot().pressKey(key, false);
			}
			context.closeScreens();
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

}