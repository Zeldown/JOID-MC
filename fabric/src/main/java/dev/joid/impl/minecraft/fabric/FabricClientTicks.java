package dev.joid.impl.minecraft.fabric;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricClientTicks {

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(_ -> JoidMinecraft.tick());
	}

}