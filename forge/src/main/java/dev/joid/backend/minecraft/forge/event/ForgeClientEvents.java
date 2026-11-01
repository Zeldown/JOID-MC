package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientEvents {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
	}

	@SubscribeEvent
	public void tick(final ClientTickEvent event) {
		if (event.phase == Phase.START) {
			MinecraftScreenEvents.fireInput();
		} else {
			MinecraftClientEvents.fireTick();
		}
	}

	@SubscribeEvent
	public void render(final RenderTickEvent event) {
		if (event.phase == Phase.START) {
			MinecraftClientEvents.fireFrame();
		} else {
			MinecraftOverlayEvents.fireHiddenHud();
		}
	}

}