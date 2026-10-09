package dev.joid.backend.minecraft.loader.registry;

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
public final class GameTestFunction {

	private static final List<GameTestFunction> REGISTERED = new ArrayList<>();

	private final String                   id;
	private final Consumer<GameTestHelper> function;

	public static @NonNull GameTestFunction create(final @NonNull String id, final @NonNull Consumer<GameTestHelper> function) {
		return new GameTestFunction(id, function);
	}

	public @NonNull GameTestFunction register() {
		GameTestFunction.REGISTERED.add(this);
		return this;
	}

	public static @NonNull List<GameTestFunction> getRegistered() {
		return Collections.unmodifiableList(GameTestFunction.REGISTERED);
	}

}