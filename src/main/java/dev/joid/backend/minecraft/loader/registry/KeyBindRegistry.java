package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.lib.input.key.Key;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class KeyBindRegistry {

	private static final List<KeyBindRegistry> REGISTERED = new ArrayList<>();

	private final Key    key;
	private final String id;

	private String   category;
	private Runnable onPress;

	private KeyBindRegistry(final String id, final Key key) {
		this.id       = id;
		this.key      = key;
		this.category = "joid:main";
		this.onPress  = () -> {};
	}

	public static @NonNull KeyBindRegistry create(final @NonNull String id, final @NonNull Key key) {
		return new KeyBindRegistry(id, key);
	}

	public @NonNull KeyBindRegistry category(final @NonNull String category) {
		this.category = category;
		return this;
	}

	public @NonNull KeyBindRegistry onPress(final @NonNull Runnable onPress) {
		this.onPress = onPress;
		return this;
	}

	public @NonNull KeyBindRegistry register() {
		KeyBindRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<String> getCategories() {
		return Collections.unmodifiableList(KeyBindRegistry.REGISTERED.stream().map(KeyBindRegistry::getCategory).distinct().collect(Collectors.toList()));
	}

	public static @NonNull List<KeyBindRegistry> getRegistered() {
		return Collections.unmodifiableList(KeyBindRegistry.REGISTERED);
	}

}