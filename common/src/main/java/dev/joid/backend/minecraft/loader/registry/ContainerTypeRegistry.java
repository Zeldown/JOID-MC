package dev.joid.backend.minecraft.loader.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerTypeRegistry<C extends Container> {

	private static final List<ContainerTypeRegistry<?>> REGISTERED = new ArrayList<>();

	private static BiConsumer<ContainerTypeRegistry<?>, EntityPlayer> opener;

	private final String                    id;
	private final Function<EntityPlayer, C> factory;

	public static <C extends Container> @NonNull ContainerTypeRegistry<C> create(final @NonNull String id, final @NonNull Function<EntityPlayer, C> factory) {
		return new ContainerTypeRegistry<>(id, factory);
	}

	public @NonNull ContainerTypeRegistry<C> register() {
		ContainerTypeRegistry.REGISTERED.add(this);
		return this;
	}

	public @NonNull ContainerTypeRegistry<C> open(final @NonNull EntityPlayer player) {
		if (ContainerTypeRegistry.opener == null) {
			throw new IllegalStateException("No container opener registered, the container " + this.id + " can only be opened once the loader has started");
		}

		ContainerTypeRegistry.opener.accept(this, player);
		return this;
	}

	public static void opener(final @NonNull BiConsumer<ContainerTypeRegistry<?>, EntityPlayer> opener) {
		ContainerTypeRegistry.opener = opener;
	}

	public static @NonNull List<ContainerTypeRegistry<?>> getRegistered() {
		return Collections.unmodifiableList(ContainerTypeRegistry.REGISTERED);
	}

}