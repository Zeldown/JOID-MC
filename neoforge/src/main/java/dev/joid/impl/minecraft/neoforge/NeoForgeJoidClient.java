package dev.joid.impl.minecraft.neoforge;

import dev.joid.impl.minecraft.JoidMinecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = JoidMinecraft.MOD_ID, dist = Dist.CLIENT)
public final class NeoForgeJoidClient {

	public NeoForgeJoidClient() {
		JoidMinecraft.bootstrap();
		NeoForge.EVENT_BUS.addListener(NeoForgeClientTicks::tick);
		NeoForge.EVENT_BUS.addListener(NeoForgeScreenEvents::init);
	}

}