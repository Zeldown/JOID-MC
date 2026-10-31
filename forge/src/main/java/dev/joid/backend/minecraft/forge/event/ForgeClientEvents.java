package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientEvents {

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::tick);
		MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::render);
	}

	private static void tick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			MinecraftClientEvents.fireTick();
		}
	}

	private static void render(final TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			MinecraftOverlayEvents.fireHiddenHud();
		}
	}

}