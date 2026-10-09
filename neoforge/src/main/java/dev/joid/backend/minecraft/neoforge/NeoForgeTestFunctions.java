package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.registry.TestFunctionRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeTestFunctions {

	public static void register(final RegisterEvent event) {
		event.register(Registries.TEST_FUNCTION, helper -> TestFunctionRegistry.getFunctions().forEach(helper::register));
	}

}