package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.GameTestFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeGameTests {

	public static void register(final BusGroup modBusGroup) {
		RegisterEvent.getBus(modBusGroup).addListener(event -> event.register(Registries.TEST_FUNCTION, helper -> {
			for (final GameTestFunction function : GameTestFunction.getRegistered()) {
				helper.register(Identifier.parse(function.getId()), function.getFunction());
			}
		}));
	}

}