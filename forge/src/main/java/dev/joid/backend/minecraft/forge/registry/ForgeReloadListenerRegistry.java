package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListenerRegistry {

	public static void register() {
		for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
			((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener) (manager, types) -> listener.getOnReload().run());
		}
	}

}