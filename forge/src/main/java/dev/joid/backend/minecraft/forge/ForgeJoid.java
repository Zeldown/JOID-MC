package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftBackend.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		ForgePayloads.register();
		ForgeTestFunctions.register(context.getModBusGroup());
		ForgeContainerTypes.register(context.getModBusGroup());
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register(context.getModBusGroup());
		}
	}

}