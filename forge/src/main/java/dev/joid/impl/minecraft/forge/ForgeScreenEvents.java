package dev.joid.impl.minecraft.forge;

import dev.joid.impl.minecraft.JoidMinecraft;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeScreenEvents {

	public static void register() {
		ScreenEvent.Init.Post.BUS.addListener(event -> JoidMinecraft.initScreen(event.getScreen(), event::addListener));
	}

}