package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ReloadListener {

	private static final List<ReloadListener> REGISTERED = new ArrayList<>();

	private final String id;

	private Runnable onReload;

	private ReloadListener(final String id) {
		this.id       = id;
		this.onReload = () -> {};
	}

	public static @NonNull ReloadListener create(final @NonNull String id) {
		return new ReloadListener(id);
	}

	public @NonNull ReloadListener onReload(final @NonNull Runnable onReload) {
		this.onReload = onReload;
		return this;
	}

	public @NonNull ReloadListener register() {
		ReloadListener.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ReloadListener> getRegistered() {
		return Collections.unmodifiableList(ReloadListener.REGISTERED);
	}

}