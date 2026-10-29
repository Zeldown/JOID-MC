package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.MinecraftJoid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftClientEvents {

	public static void fireTick() {
		MinecraftJoid.register();
	}

}