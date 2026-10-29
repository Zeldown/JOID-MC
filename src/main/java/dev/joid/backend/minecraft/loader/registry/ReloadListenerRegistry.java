package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ReloadListenerRegistry {

	private static final List<ReloadListenerRegistry> REGISTERED = new ArrayList<>();

	private final String id;

	private Runnable onReload;

	private ReloadListenerRegistry(final String id) {
		this.id       = id;
		this.onReload = () -> {};
	}

	public static @NonNull ReloadListenerRegistry create(final @NonNull String id) {
		return new ReloadListenerRegistry(id);
	}

	public @NonNull ReloadListenerRegistry onReload(final @NonNull Runnable onReload) {
		this.onReload = onReload;
		return this;
	}

	public @NonNull ReloadListenerRegistry register() {
		ReloadListenerRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ReloadListenerRegistry> getRegistered() {
		return Collections.unmodifiableList(ReloadListenerRegistry.REGISTERED);
	}

}