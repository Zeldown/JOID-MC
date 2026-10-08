package dev.joid.impl.minecraft.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyMappingRegistry {

	private static final Map<KeyMapping, Runnable> ACTION_MAP = new LinkedHashMap<>();

	public static void register(final @NonNull KeyMapping keyMapping, final @NonNull Runnable action) {
		KeyMappingRegistry.ACTION_MAP.put(keyMapping, action);
	}

	public static void tick() {
		KeyMappingRegistry.ACTION_MAP.forEach((keyMapping, action) -> {
			while (keyMapping.consumeClick()) {
				action.run();
			}
		});
	}

	public static @NonNull List<@NonNull KeyMapping> getKeyMappings() {
		return Collections.unmodifiableList(new ArrayList<>(KeyMappingRegistry.ACTION_MAP.keySet()));
	}

	public static @NonNull List<KeyMapping.@NonNull Category> getCategories() {
		final List<KeyMapping.Category> categoryList = new ArrayList<>();
		for (final KeyMapping keyMapping : KeyMappingRegistry.ACTION_MAP.keySet()) {
			if (!categoryList.contains(keyMapping.getCategory()) && !keyMapping.getCategory().id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
				categoryList.add(keyMapping.getCategory());
			}
		}
		return Collections.unmodifiableList(categoryList);
	}

}