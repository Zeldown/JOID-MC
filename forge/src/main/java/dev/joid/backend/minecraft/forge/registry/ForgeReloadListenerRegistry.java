package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.loader.registry.ReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeReloadListenerRegistry {

	public static void register() {
		final IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		for (final ReloadListenerRegistry listener : ReloadListenerRegistry.getRegistered()) {
			manager.registerReloadListener((ISelectiveResourceReloadListener) (resources, types) -> listener.getOnReload().run());
		}
	}

}