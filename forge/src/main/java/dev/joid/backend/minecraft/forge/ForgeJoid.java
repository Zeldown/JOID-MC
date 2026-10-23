package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerTypeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MinecraftJoid.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid(final FMLJavaModLoadingContext context) {
		MinecraftJoid.init();
		ForgePayloads.register();
		ForgeContainerTypeRegistry.register(context.getModBusGroup());
		ForgeJoidClient.register(context.getModBusGroup());
	}

}