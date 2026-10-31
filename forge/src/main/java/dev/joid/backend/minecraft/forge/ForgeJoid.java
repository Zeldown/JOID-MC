package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftJoid.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		if (FMLEnvironment.dist.isClient()) {
			MinecraftJoid.init();
			ForgePayloads.register();
			ForgeJoidClient.register(context.getModEventBus());
			ForgeContainerTypeRegistry.register(context.getModEventBus());
		}
	}

}