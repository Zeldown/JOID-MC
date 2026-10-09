package dev.joid.backend.minecraft.fabric;

import java.util.Map;
import java.util.function.Consumer;

import dev.joid.backend.minecraft.registry.TestFunctionRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricTestFunctions {

	public static void register() {
		for (final Map.Entry<Identifier, Consumer<GameTestHelper>> function : TestFunctionRegistry.getFunctions().entrySet()) {
			Registry.register(BuiltInRegistries.TEST_FUNCTION, function.getKey(), function.getValue());
		}
	}

}