package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypes;
import dev.joid.backend.minecraft.forge.registry.ForgeGameTests;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftBackend.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		MinecraftBackend.init();
		ForgePayloads.register();
		ForgeGameTests.register(context.getModBusGroup());
		ForgeContainerTypes.register(context.getModBusGroup());
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register(context.getModBusGroup());
		}
	}

}