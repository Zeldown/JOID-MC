package dev.joid.backend.minecraft.neoforge.demo;

import dev.joid.backend.minecraft.demo.registry.GameTestFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeGameTests {

	public static void register(final RegisterEvent event) {
		event.register(Registries.TEST_FUNCTION, helper -> {
			for (final GameTestFunction function : GameTestFunction.getRegistered()) {
				helper.register(Identifier.parse(function.getId()), function.getFunction());
			}
		});
	}

}