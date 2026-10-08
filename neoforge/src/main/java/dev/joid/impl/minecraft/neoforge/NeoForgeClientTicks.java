package dev.joid.impl.minecraft.neoforge;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ClientTickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeClientTicks {

	public static void tick(final ClientTickEvent.Post event) {
		JoidMinecraft.tick();
	}

}