package dev.joid.backend.minecraft.forge.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeScreenEvents {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ForgeScreenEvents());
	}

	@SubscribeEvent
	public void render(final DrawScreenEvent.Post event) {
		MinecraftScreenEvents.fireRender();
	}

}