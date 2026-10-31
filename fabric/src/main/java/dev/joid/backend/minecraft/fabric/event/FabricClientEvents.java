package dev.joid.backend.minecraft.fabric.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricClientEvents {

	public static void register() {
		WorldRenderEvents.END.register(context -> MinecraftOverlayEvents.fireHiddenHud());
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> MinecraftClientEvents.fireTick());
	}

}