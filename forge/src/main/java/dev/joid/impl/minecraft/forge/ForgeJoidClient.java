package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register() {
		JoidMinecraft.bootstrap();
		ForgeKeyMappings.register();
		ForgeClientTicks.register();
	}

}