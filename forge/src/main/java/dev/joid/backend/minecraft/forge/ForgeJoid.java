package dev.joid.backend.minecraft.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import dev.joid.backend.minecraft.MinecraftJoid;

@Mod(modid = MinecraftJoid.MOD_ID, useMetadata = true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.7.10]")
public final class ForgeJoid {

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		if (event.getSide().isClient()) {
			ForgeJoidClient.register();
		}
	}

}