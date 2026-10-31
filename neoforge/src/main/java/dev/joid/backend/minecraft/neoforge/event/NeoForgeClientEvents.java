package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.event.TickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeClientEvents {

	public static void tick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			MinecraftClientEvents.fireTick();
		}
	}

	public static void render(final TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			MinecraftOverlayEvents.fireHiddenHud();
		}
	}

}