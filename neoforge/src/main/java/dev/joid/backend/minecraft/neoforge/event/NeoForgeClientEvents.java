package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeClientEvents {

	public static void tick(final ClientTickEvent.Post event) {
		MinecraftClientEvents.fireTick();
	}

	public static void render(final RenderFrameEvent.Post event) {
		MinecraftOverlayEvents.fireHiddenHud();
	}

}