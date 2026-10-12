package dev.joid.backend.minecraft.bridge.input;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConsumedKeys {

	private static final Set<Integer> PRESSED = new HashSet<>();

	public static boolean press(final int code, final boolean consumed) {
		if (consumed) {
			ConsumedKeys.PRESSED.add(code);
		} else {
			ConsumedKeys.PRESSED.remove(code);
		}
		return consumed;
	}

	public static boolean release(final int code) {
		return ConsumedKeys.PRESSED.remove(code);
	}

}