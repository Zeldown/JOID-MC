package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.forge.demo.ForgeGameTestRegistry;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypeRegistry;
import dev.joid.internal.JOIDEnv;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftJoid.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		MinecraftJoid.init();
		ForgePayloads.register();
		ForgeContainerTypeRegistry.register(context.getModBusGroup());
		if (JOIDEnv.isDemoMode()) {
			ForgeGameTestRegistry.register(context.getModBusGroup());
		}

		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register(context.getModBusGroup());
		}
	}

}