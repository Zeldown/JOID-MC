package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.forge.demo.ForgeGameTests;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Backend.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		Backend.init();
		ForgePayloads.register();
		ForgeContainerTypes.register(context.getModBusGroup());
		if (Backend.isDemo()) {
			ForgeGameTests.register(context.getModBusGroup());
		}

		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register(context.getModBusGroup());
		}
	}

}