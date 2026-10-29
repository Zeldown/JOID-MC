package dev.joid.backend.minecraft.bridge.input;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.base.lwjgl2.input.Lwjgl2Keys;
import dev.joid.lib.input.key.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.settings.KeyBinding;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyBindMapping {

	private static final Map<KeyBindRegistry, KeyBinding> MAPPINGS = new LinkedHashMap<>();

	public static KeyBinding create(final KeyBindRegistry bind) {
		final KeyBinding binding = new KeyBinding("key." + bind.getId(), KeyBindMapping.getCode(bind.getKey()), KeyBindMapping.getCategory(bind.getCategory()));
		KeyBindMapping.MAPPINGS.put(bind, binding);
		return binding;
	}

	public static void firePresses() {
		for (final Entry<KeyBindRegistry, KeyBinding> entry : KeyBindMapping.MAPPINGS.entrySet()) {
			while (entry.getValue().isPressed()) {
				entry.getKey().getOnPress().run();
			}
		}
	}

	private static int getCode(final Key key) {
		for (int code = 0; code < Keyboard.KEYBOARD_SIZE; code++) {
			if (Lwjgl2Keys.getPhysicalKey(code) == key) {
				return code;
			}
		}
		return Keyboard.KEY_NONE;
	}

	private static String getCategory(final String category) {
		return "key.category." + category.replace(':', '.');
	}

}