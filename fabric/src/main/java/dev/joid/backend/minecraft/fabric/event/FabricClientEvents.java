package dev.joid.backend.minecraft.fabric.event;

import dev.joid.backend.minecraft.loader.event.MinecraftClientEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricClientEvents {

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(_ -> MinecraftClientEvents.fireTick());
	}

}