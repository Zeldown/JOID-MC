package dev.joid.backend.minecraft.showcase;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.clock.ManualClockBridge;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseClock {

	private static final ManualClockBridge INTERFACE = new ManualClockBridge(0L);

	private static volatile long    start;
	private static volatile long    elapsed;
	private static volatile boolean frozen;

	public static void freeze() {
		if (ShowcaseClock.frozen) {
			return;
		}

		ShowcaseClock.start   = System.currentTimeMillis();
		ShowcaseClock.elapsed = 0L;
		ShowcaseClock.frozen  = true;
		ShowcaseClock.INTERFACE.time(ShowcaseClock.start);
		BridgeHandler.CLOCK.register(ShowcaseClock.INTERFACE);
	}

	public static void release() {
		if (ShowcaseClock.frozen) {
			ShowcaseClock.frozen = false;
			BridgeHandler.CLOCK.unregister(ShowcaseClock.INTERFACE);
		}
	}

	public static void advance(final long nanos) {
		if (!ShowcaseClock.frozen) {
			return;
		}

		ShowcaseClock.elapsed = ShowcaseClock.elapsed + nanos;
		ShowcaseClock.INTERFACE.time(ShowcaseClock.start + ShowcaseClock.elapsed / 1_000_000L);
	}

}