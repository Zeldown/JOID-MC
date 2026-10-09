package dev.joid.backend.minecraft.bridge.input;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.joid.backend.minecraft.loader.registry.KeyBind;
import dev.joid.base.glfw.input.GlfwKeys;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyBindMapping {

	private static final Map<KeyBind, KeyMapping> MAPPINGS = new LinkedHashMap<>();

	public static KeyMapping create(final KeyBind bind) {
		final KeyMapping mapping = new KeyMapping("key." + bind.getId(), GlfwKeys.getCode(bind.getKey()), KeyBindMapping.category(bind.getCategory()));
		KeyBindMapping.MAPPINGS.put(bind, mapping);
		return mapping;
	}

	public static KeyMapping.Category category(final String category) {
		return new KeyMapping.Category(Identifier.parse(category));
	}

	public static void firePresses() {
		for (final Entry<KeyBind, KeyMapping> entry : KeyBindMapping.MAPPINGS.entrySet()) {
			while (entry.getValue().consumeClick()) {
				entry.getKey().getOnPress().run();
			}
		}
	}

}