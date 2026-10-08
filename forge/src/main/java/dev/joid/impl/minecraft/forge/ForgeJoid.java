package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.JoidMinecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(JoidMinecraft.MOD_ID)
public final class ForgeJoid {

	public ForgeJoid() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ForgeJoidClient.register();
		}
	}

}