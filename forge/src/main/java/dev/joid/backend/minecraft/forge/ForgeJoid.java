package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod(MinecraftJoid.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (version, network) -> true));
		if (FMLEnvironment.dist.isClient()) {
			final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
			MinecraftJoid.init();
			ForgePayloads.register();
			ForgeJoidClient.register(modBus);
			ForgeContainerTypeRegistry.register(modBus);
		}
	}

}