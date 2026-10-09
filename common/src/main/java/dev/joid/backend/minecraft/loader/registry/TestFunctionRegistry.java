package dev.joid.backend.minecraft.loader.registry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestFunctionRegistry {

	private static final Map<Identifier, Consumer<GameTestHelper>> FUNCTIONS = new LinkedHashMap<>();

	public static void register(final Identifier id, final Consumer<GameTestHelper> function) {
		TestFunctionRegistry.FUNCTIONS.put(id, function);
	}

	public static Map<Identifier, Consumer<GameTestHelper>> getFunctions() {
		return Collections.unmodifiableMap(TestFunctionRegistry.FUNCTIONS);
	}

}