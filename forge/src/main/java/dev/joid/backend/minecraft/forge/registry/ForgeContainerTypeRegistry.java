package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerTypeRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addGenericListener(MenuType.class, (final RegistryEvent.Register<MenuType<?>> event) -> {
			for (final ContainerTypeRegistry<?> type : ContainerTypeRegistry.getRegistered()) {
				event.getRegistry().register(type.getType().setRegistryName(ResourceLocation.tryParse(type.getId())));
			}
		});
	}

}