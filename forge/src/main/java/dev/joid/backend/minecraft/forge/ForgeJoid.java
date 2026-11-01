package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftJoid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MinecraftJoid.MOD_ID, useMetadata = true, clientSideOnly = true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.8.9]")
public final class ForgeJoid {

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		MinecraftJoid.init();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		ForgeJoidClient.register(this);
	}

}