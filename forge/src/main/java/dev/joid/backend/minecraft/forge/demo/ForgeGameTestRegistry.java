package dev.joid.backend.minecraft.forge.demo;

import dev.joid.backend.minecraft.demo.registry.GameTestRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeGameTestRegistry {

	public static void register(final BusGroup modBusGroup) {
		RegisterEvent.getBus(modBusGroup).addListener(event -> event.register(Registries.TEST_FUNCTION, helper -> {
			for (final GameTestRegistry function : GameTestRegistry.getRegistered()) {
				helper.register(Identifier.parse(function.getId()), function.getFunction());
			}
		}));
	}

}