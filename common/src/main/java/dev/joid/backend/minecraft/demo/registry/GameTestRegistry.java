package dev.joid.backend.minecraft.demo.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.gametest.framework.GameTestHelper;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameTestRegistry {

	private static final List<GameTestRegistry> REGISTERED = new ArrayList<>();

	private final String                   id;
	private final Consumer<GameTestHelper> function;

	public static @NonNull GameTestRegistry create(final @NonNull String id, final @NonNull Consumer<GameTestHelper> function) {
		return new GameTestRegistry(id, function);
	}

	public @NonNull GameTestRegistry register() {
		GameTestRegistry.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<GameTestRegistry> getRegistered() {
		return Collections.unmodifiableList(GameTestRegistry.REGISTERED);
	}

}