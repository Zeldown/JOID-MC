package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeScreenEvents {

	public static void register() {
		ScreenEvent.Init.Post.BUS.addListener(event -> MinecraftBackend.initScreen(event.getScreen(), event::addListener));
	}

}