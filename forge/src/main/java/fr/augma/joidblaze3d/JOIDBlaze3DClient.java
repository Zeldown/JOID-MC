package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;
import fr.augma.joidblaze3d.overlay.OverlayHandler;
import fr.augma.joidblaze3d.render.resource.MCResourceResolver;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

public final class JOIDBlaze3DClient {

	public static void init(final BusGroup busGroup) {
		FMLClientSetupEvent.getBus(busGroup).addListener(event -> event.enqueueWork(() -> JOIDMC.register(!FMLLoader.isProduction())));
		RegisterClientCommandsEvent.BUS.addListener(event -> event.getDispatcher().register(JOIDCommand.create()));
		RegisterClientReloadListenersEvent.BUS.addListener((final RegisterClientReloadListenersEvent event) -> event.registerReloadListener(MCResourceResolver.inst()));
		OverlayHandler.register();
	}

}