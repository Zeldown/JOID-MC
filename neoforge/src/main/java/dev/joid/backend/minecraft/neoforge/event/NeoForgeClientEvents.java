package dev.joid.backend.minecraft.neoforge.event;

import dev.joid.backend.minecraft.loader.event.ClientEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ClientTickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeClientEvents {

	public static void tick(final ClientTickEvent.Post event) {
		ClientEvents.fireTick();
	}

}