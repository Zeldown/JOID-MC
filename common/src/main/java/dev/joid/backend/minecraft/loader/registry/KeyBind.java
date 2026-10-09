package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.joid.backend.minecraft.Backend;
import dev.joid.lib.utils.key.Key;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class KeyBind {

	private static final List<KeyBind> REGISTERED = new ArrayList<>();

	private final Key    key;
	private final String id;

	private String   category;
	private Runnable onPress;

	private KeyBind(final String id, final Key key) {
		this.id       = id;
		this.key      = key;
		this.category = Backend.MOD_ID + ":main";
		this.onPress  = () -> {};
	}

	public static @NonNull KeyBind create(final @NonNull String id, final @NonNull Key key) {
		return new KeyBind(id, key);
	}

	public @NonNull KeyBind onPress(final @NonNull Runnable onPress) {
		this.onPress = onPress;
		return this;
	}

	public @NonNull KeyBind category(final @NonNull String category) {
		this.category = category;
		return this;
	}

	public @NonNull KeyBind register() {
		KeyBind.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<KeyBind> getRegistered() {
		return Collections.unmodifiableList(KeyBind.REGISTERED);
	}

	public static @NonNull List<String> getCategories() {
		return KeyBind.REGISTERED.stream().map(KeyBind::getCategory).distinct().toList();
	}

}