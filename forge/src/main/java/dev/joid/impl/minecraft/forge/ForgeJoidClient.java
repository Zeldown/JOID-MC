package dev.joid.impl.minecraft.forge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register() {
		ForgeClientTicks.register();
		ForgeScreenEvents.register();
		ForgeReloadListeners.register();
	}

}