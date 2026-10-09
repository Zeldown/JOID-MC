package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.registry.TestFunctionRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeTestFunctions {

	public static void register(final BusGroup modBusGroup) {
		RegisterEvent.getBus(modBusGroup).addListener(event -> event.register(Registries.TEST_FUNCTION, helper -> TestFunctionRegistry.getFunctions().forEach(helper::register)));
	}

}