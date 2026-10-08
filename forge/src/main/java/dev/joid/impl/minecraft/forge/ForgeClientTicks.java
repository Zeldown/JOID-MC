package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.event.TickEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeClientTicks {

	public static void register() {
		TickEvent.ClientTickEvent.Post.BUS.addListener(_ -> JoidMinecraft.tick());
	}

}