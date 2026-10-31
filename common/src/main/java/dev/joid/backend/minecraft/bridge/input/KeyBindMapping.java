package dev.joid.backend.minecraft.bridge.input;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.joid.backend.minecraft.loader.registry.KeyBindRegistry;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyBindMapping {

	private static final Map<KeyBindRegistry, KeyMapping> MAPPINGS = new LinkedHashMap<>();

	public static KeyMapping create(final KeyBindRegistry bind) {
		final KeyMapping mapping = new KeyMapping("key." + bind.getId(), GlfwKeys.getCode(bind.getKey()), KeyBindMapping.getCategory(bind.getCategory()));
		KeyBindMapping.MAPPINGS.put(bind, mapping);
		return mapping;
	}

	public static void firePresses() {
		for (final Entry<KeyBindRegistry, KeyMapping> entry : KeyBindMapping.MAPPINGS.entrySet()) {
			while (entry.getValue().consumeClick()) {
				entry.getKey().getOnPress().run();
			}
		}
	}

	private static String getCategory(final String category) {
		return "key.category." + category.replace(':', '.');
	}

}