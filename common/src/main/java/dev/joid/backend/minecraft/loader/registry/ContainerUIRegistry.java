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

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerUIRegistry<M extends AbstractContainerMenu> {

	private static final List<ContainerUIRegistry<?>> REGISTERED = new ArrayList<>();

	private final MenuType<M>                           type;
	private final Function<M, ? extends ContainerUI<M>> factory;

	public static <M extends AbstractContainerMenu> @NonNull ContainerUIRegistry<M> create(final @NonNull MenuType<M> type, final @NonNull Function<M, ? extends ContainerUI<M>> factory) {
		return new ContainerUIRegistry<>(type, factory);
	}

	public @NonNull ContainerUIRegistry<M> register() {
		ContainerUIRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ContainerUIRegistry<?>> getRegistered() {
		return Collections.unmodifiableList(ContainerUIRegistry.REGISTERED);
	}

}