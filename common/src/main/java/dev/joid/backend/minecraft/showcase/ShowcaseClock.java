package dev.joid.backend.minecraft.showcase;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.clock.ManualClockBridge;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.util.Util;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseClock {

	private static final ManualClockBridge INTERFACE = ManualClockBridge.create(0L);

	private static volatile long    start;
	private static volatile long    nanos;
	private static volatile long    elapsed;
	private static volatile boolean frozen;

	public static void freeze() {
		if (ShowcaseClock.frozen) {
			return;
		}

		ShowcaseClock.start  = System.currentTimeMillis();
		ShowcaseClock.nanos  = Util.getNanos();
		ShowcaseClock.frozen = true;
		ShowcaseClock.INTERFACE.setTime(ShowcaseClock.start);
		BridgeHandler.CLOCK.register(ShowcaseClock.INTERFACE);
		Util.setTimeSource(() -> ShowcaseClock.nanos);
	}

	public static void release() {
		if (ShowcaseClock.frozen) {
			ShowcaseClock.frozen = false;
			Util.setTimeSource(System::nanoTime);
		}
	}

	public static void advance(final long nanos) {
		if (!ShowcaseClock.frozen) {
			return;
		}

		ShowcaseClock.nanos   = ShowcaseClock.nanos + nanos;
		ShowcaseClock.elapsed = ShowcaseClock.elapsed + nanos;
		ShowcaseClock.INTERFACE.setTime(ShowcaseClock.start + ShowcaseClock.elapsed / 1_000_000L);
	}

}