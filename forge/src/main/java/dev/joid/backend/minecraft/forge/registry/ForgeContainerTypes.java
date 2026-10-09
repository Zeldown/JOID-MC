package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerDeclaration;
import dev.joid.backend.minecraft.loader.registry.ContainerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerTypes {

	public static void register(final BusGroup modBusGroup) {
		RegisterEvent.getBus(modBusGroup).addListener(event -> event.register(Registries.MENU, helper -> {
			for (final ContainerDeclaration<?> declaration : ContainerRegistry.getDeclarations()) {
				helper.register(declaration.id(), declaration.type());
			}
		}));
	}

}