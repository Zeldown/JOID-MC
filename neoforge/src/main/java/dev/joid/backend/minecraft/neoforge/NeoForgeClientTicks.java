package dev.joid.backend.minecraft.neoforge;

import dev.joid.backend.minecraft.MinecraftBackend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.neoforged.neoforge.client.event.ClientTickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeClientTicks {

	public static void tick(final ClientTickEvent.Post event) {
		MinecraftBackend.tick();
	}

}