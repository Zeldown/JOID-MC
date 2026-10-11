package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerTypeRegistry<M extends AbstractContainerMenu> {

	private static final List<ContainerTypeRegistry<?>> REGISTERED = new ArrayList<>();

	private final String      id;
	private final MenuType<M> type;

	public static <M extends AbstractContainerMenu> @NonNull ContainerTypeRegistry<M> create(final @NonNull String id, final @NonNull MenuType<M> type) {
		return new ContainerTypeRegistry<>(id, type);
	}

	public @NonNull ContainerTypeRegistry<M> register() {
		ContainerTypeRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<ContainerTypeRegistry<?>> getRegistered() {
		return Collections.unmodifiableList(ContainerTypeRegistry.REGISTERED);
	}

}