package dev.joid.backend.minecraft.forge.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientEvents {

	public static void register() {
		FMLCommonHandler.instance().bus().register(new ForgeClientEvents());
	}

	@SubscribeEvent
	public void tick(final ClientTickEvent event) {
		if (event.phase == Phase.END) {
			MinecraftClientEvents.fireTick();
		}
	}

}