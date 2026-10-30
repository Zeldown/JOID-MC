package dev.joid.backend.minecraft.demo.test;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import dev.joid.lib.JOID;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;

public final class ClientTestContext {

	@Getter private final IClientTestPilot   pilot;
	@Getter private final ClientTestScenario scenario;
	private final List<String>               failureList;

	public ClientTestContext(final IClientTestPilot pilot, final ClientTestScenario scenario, final List<String> failureList) {
		this.pilot       = pilot;
		this.scenario    = scenario;
		this.failureList = failureList;
	}

	public void fail(final String failure) {
		this.failureList.add(this.scenario + ": " + failure);
	}

	public void check(final boolean condition, final String failure) {
		if (!condition) {
			this.fail(failure);
		}
	}

	public void run(final Runnable runnable) {
		this.pilot.runOnClient(runnable);
	}

	public <T> T compute(final Callable<T> callable) {
		return this.pilot.computeOnClient(callable, 120);
	}

	public <T> T compute(final Callable<T> callable, final int seconds) {
		return this.pilot.computeOnClient(callable, seconds);
	}

	public boolean test(final BooleanSupplier condition) {
		return this.compute(condition::getAsBoolean);
	}

	public void waitFor(final String label, final BooleanSupplier condition, final int seconds) {
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
		while (!this.test(condition)) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after " + seconds + " s waiting for " + label + " on the screen " + this.compute(this.pilot::getScreen));
			}

			ClientTestContext.sleep(100L);
		}
	}

	public void waitFrames(final int frames) {
		final long target = ClientTestFrameUI.getFrames() + frames;
		final long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
		while (ClientTestFrameUI.getFrames() < target) {
			if (System.nanoTime() > end) {
				throw new AssertionError("Timed out after 60 s waiting for " + frames + " frames");
			}

			ClientTestContext.sleep(10L);
		}
	}

	public void waitTicks(final int ticks) {
		ClientTestContext.sleep(ticks * 50L);
		this.waitFrames(2);
	}

	public void moveMouse(final double x, final double y) {
		this.pilot.moveMouse(x, y);
		this.waitFrames(2);
	}

	public void moveTo(final int[] bounds) {
		this.moveMouse(bounds[0] + bounds[2] / 2D, bounds[1] + bounds[3] / 2D);
	}

	public void click() {
		this.pilot.pressMouse(true);
		this.waitFrames(1);
		this.pilot.pressMouse(false);
		this.waitFrames(3);
	}

	public void click(final int[] bounds) {
		this.moveTo(bounds);
		this.click();
	}

	public void holdMouse() {
		this.pilot.pressMouse(true);
		this.waitFrames(2);
	}

	public void releaseMouse() {
		this.pilot.pressMouse(false);
		this.waitFrames(2);
	}

	public void press(final Key key) {
		this.pilot.pressKey(key, true);
		this.waitFrames(1);
		this.pilot.pressKey(key, false);
		this.waitFrames(3);
	}

	public void holdKey(final Key key) {
		this.pilot.pressKey(key, true);
		this.waitFrames(2);
	}

	public void releaseKey(final Key key) {
		this.pilot.pressKey(key, false);
		this.waitFrames(2);
	}

	public void scroll(final int notches) {
		this.moveMouse(960D, 540D);
		for (int notch = 0; notch < notches; notch++) {
			this.pilot.scroll(-1D);
			this.waitFrames(1);
		}
		this.waitTicks(40);
	}

	public BufferedImage capture(final String name) {
		this.waitFrames(2);
		return this.pilot.capture(name);
	}

	public void guiScale(final int guiScale) {
		this.run(() -> this.pilot.applyGuiScale(guiScale));
		this.waitTicks(5);
	}

	public void open(final UI ui) {
		this.run(() -> JOID.open(ui));
		this.waitTicks(20);
	}

	public void openOver(final UI ui) {
		this.openChoice();
		this.open(ui);
		this.waitTicks(20);
	}

	public void openChoice() {
		this.run(() -> JOID.open(ClientTestDemos.create(ClientTestDemos.CHOICE)));
		this.waitFor("UIDemoMinecraftChoice", () -> this.pilot.isJoidScreen() && JOID.isOpen(ClientTestDemos.type(ClientTestDemos.CHOICE)), 30);
		this.waitTicks(20);
	}

	public void clickEntry(final String label) {
		this.click(this.compute(() -> ClientTestBounds.entry(label)));
		this.waitTicks(40);
	}

	public void escape() {
		this.press(Key.ESCAPE);
		this.waitTicks(10);
	}

	public void closeScreens() {
		for (int attempt = 0; attempt < 6 && this.test(this.pilot::isScreenOpen); attempt++) {
			this.press(Key.ESCAPE);
			this.waitTicks(20);
		}

		if (this.test(this.pilot::isScreenOpen)) {
			this.run(this.pilot::closeScreen);
			this.waitTicks(10);
		}
	}

	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while sleeping", exception);
		}
	}

}