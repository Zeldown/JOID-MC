package dev.joid.backend.minecraft.bridge.input;

import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.input.key.Key;

public class KeyRepeatFilterTest {

	@Test
	public void takesASecondPressWithoutReleaseForARepeat() {
		final KeyRepeatFilter filter = new KeyRepeatFilter();
		Assert.assertTrue(filter.press(Key.K));
		Assert.assertFalse(filter.press(Key.K));
		filter.release(Key.K);
		Assert.assertTrue(filter.press(Key.K));
	}

	@Test
	public void takesTheKeysHeldWhenTheScreenChangesForRepeats() {
		final Set<Key> heldKeySet = EnumSet.of(Key.J);
		final IWindowBridge window = (IWindowBridge) Proxy.newProxyInstance(IWindowBridge.class.getClassLoader(), new Class<?>[] {IWindowBridge.class}, (proxy, method, arguments) -> {
			if (method.getName().equals("isKeyDown")) {
				return heldKeySet.contains(arguments[0]);
			}
			return method.getName().equals("equals") ? proxy == arguments[0] : System.identityHashCode(proxy);
		});
		BridgeHandler.WINDOW.register(window);
		try {
			final KeyRepeatFilter filter = new KeyRepeatFilter();
			final Object screen = new Object();
			filter.follow(screen);
			Assert.assertFalse(filter.press(Key.J));
			Assert.assertTrue(filter.press(Key.K));
			filter.follow(screen);
			Assert.assertFalse(filter.press(Key.K));
			heldKeySet.clear();
			filter.follow(new Object());
			Assert.assertTrue(filter.press(Key.J));
			Assert.assertTrue(filter.press(Key.K));
		} finally {
			BridgeHandler.WINDOW.unregister(window);
		}
	}

}