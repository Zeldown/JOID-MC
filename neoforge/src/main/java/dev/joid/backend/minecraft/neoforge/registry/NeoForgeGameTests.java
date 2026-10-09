package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.TestFunctionRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeGameTests {

	public static void register(final RegisterEvent event) {
		event.register(Registries.TEST_FUNCTION, helper -> TestFunctionRegistry.getFunctions().forEach(helper::register));
	}

}