package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.bridge.input.KeyBindMapping;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftClientEvents {

	public static void fireTick() {
		Backend.register();
		KeyBindMapping.firePresses();
	}

}