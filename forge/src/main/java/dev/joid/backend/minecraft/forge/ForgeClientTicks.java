package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.event.TickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientTicks {

	public static void register() {
		TickEvent.ClientTickEvent.Post.BUS.addListener(_ -> MinecraftBackend.tick());
	}

}