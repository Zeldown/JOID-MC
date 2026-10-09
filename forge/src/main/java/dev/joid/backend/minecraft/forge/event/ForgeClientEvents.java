package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.ClientEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.event.TickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientEvents {

	public static void register() {
		TickEvent.ClientTickEvent.Post.BUS.addListener(_ -> ClientEvents.fireTick());
	}

}