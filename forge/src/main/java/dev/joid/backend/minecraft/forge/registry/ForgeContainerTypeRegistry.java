package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerTypeRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addGenericListener(ContainerType.class, (final RegistryEvent.Register<ContainerType<?>> event) -> {
			for (final ContainerTypeRegistry<?> type : ContainerTypeRegistry.getRegistered()) {
				event.getRegistry().register(type.getType().setRegistryName(ResourceLocation.tryParse(type.getId())));
			}
		});
	}

}