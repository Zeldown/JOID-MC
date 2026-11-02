package dev.joid.backend.minecraft.bridge.input;

import java.util.EnumSet;
import java.util.Set;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.input.key.Key;

public final class KeyRepeatFilter {

	private final Set<Key> heldKeySet = EnumSet.noneOf(Key.class);

	private Object screen;

	public boolean press(final Key physicalKey) {
		return this.heldKeySet.add(physicalKey);
	}

	public void release(final Key physicalKey) {
		this.heldKeySet.remove(physicalKey);
	}

	public void reset() {
		this.heldKeySet.clear();
		final IWindowBridge window = BridgeHandler.WINDOW.get();
		for (final Key key : Key.values()) {
			if (window.isKeyDown(key)) {
				this.heldKeySet.add(key);
			}
		}
	}

	public void follow(final Object screen) {
		if (this.screen != screen) {
			this.screen = screen;
			this.reset();
		}
	}

}