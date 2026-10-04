package fr.augma.joidmc;

import fr.augma.joidmc.command.JOIDCommand;
import fr.augma.joidmc.overlay.OverlayHandler;
import fr.augma.joidmc.render.resource.MCResourceResolver;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

public final class JOIDMCClient {

	public static void init(final BusGroup busGroup) {
		FMLClientSetupEvent.getBus(busGroup).addListener(event -> event.enqueueWork(() -> JOIDMC.register(!FMLLoader.isProduction())));
		RegisterClientCommandsEvent.BUS.addListener(event -> event.getDispatcher().register(JOIDCommand.create()));
		RegisterClientReloadListenersEvent.BUS.addListener((final RegisterClientReloadListenersEvent event) -> event.registerReloadListener(MCResourceResolver.inst()));
		OverlayHandler.register();
	}

}