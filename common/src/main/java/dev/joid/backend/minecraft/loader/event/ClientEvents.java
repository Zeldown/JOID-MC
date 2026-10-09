package dev.joid.backend.minecraft.loader.event;

import dev.joid.backend.minecraft.Backend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientEvents {

	public static void fireTick() {
		Backend.register();
	}

}