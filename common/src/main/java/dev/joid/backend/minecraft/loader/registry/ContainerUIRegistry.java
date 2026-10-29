package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.inventory.Container;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerUIRegistry<C extends Container> {

	private static final List<ContainerUIRegistry<?>> REGISTERED = new ArrayList<>();

	private final ContainerTypeRegistry<C>              type;
	private final Function<C, ? extends ContainerUI<C>> factory;

	public static <C extends Container> @NonNull ContainerUIRegistry<C> create(final @NonNull ContainerTypeRegistry<C> type, final @NonNull Function<C, ? extends ContainerUI<C>> factory) {
		return new ContainerUIRegistry<>(type, factory);
	}

	public @NonNull ContainerUIRegistry<C> register() {
		ContainerUIRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ContainerUIRegistry<?>> getRegistered() {
		return Collections.unmodifiableList(ContainerUIRegistry.REGISTERED);
	}

}