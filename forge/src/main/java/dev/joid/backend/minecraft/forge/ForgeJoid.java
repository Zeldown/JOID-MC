package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MinecraftBackend.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register();
		}
	}

}