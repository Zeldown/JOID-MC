package dev.joid.backend.minecraft.fabric.demo;

import dev.joid.backend.minecraft.demo.registry.GameTestFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricGameTests {

	public static void register() {
		for (final GameTestFunction function : GameTestFunction.getRegistered()) {
			Registry.register(BuiltInRegistries.TEST_FUNCTION, Identifier.parse(function.getId()), function.getFunction());
		}
	}

}