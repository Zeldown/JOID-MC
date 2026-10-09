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
public final class ContainerBinding<M extends AbstractContainerMenu> {

	private static final List<ContainerBinding<?>> REGISTERED = new ArrayList<>();

	private final MenuType<M>                           type;
	private final Function<M, ? extends ContainerUI<M>> factory;

	public static <M extends AbstractContainerMenu> @NonNull ContainerBinding<M> create(final @NonNull MenuType<M> type, final @NonNull Function<M, ? extends ContainerUI<M>> factory) {
		return new ContainerBinding<>(type, factory);
	}

	public @NonNull ContainerBinding<M> register() {
		ContainerBinding.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ContainerBinding<?>> getRegistered() {
		return Collections.unmodifiableList(ContainerBinding.REGISTERED);
	}

}