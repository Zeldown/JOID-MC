package dev.joid.backend.minecraft.neoforge.registry;

import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeContainerTypeRegistry {

	public static void register(final RegisterEvent event) {
		event.register(Registries.MENU, helper -> {
			for (final ContainerTypeRegistry<?> type : ContainerTypeRegistry.getRegistered()) {
				helper.register(ResourceLocation.tryParse(type.getId()), type.getType());
			}
		});
	}

}