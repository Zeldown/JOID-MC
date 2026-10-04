package fr.augma.joidmc;

import fr.augma.joidmc.demo.key.DemoKey;
import fr.augma.joidmc.overlay.OverlayHandler;
import fr.augma.joidmc.render.resource.MCResourceResolver;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

public final class JOIDMCClient {

	public static void init(final BusGroup busGroup) {
		FMLClientSetupEvent.getBus(busGroup).addListener(event -> event.enqueueWork(() -> JOIDMC.register(!FMLLoader.isProduction())));
		RegisterClientReloadListenersEvent.BUS.addListener((final RegisterClientReloadListenersEvent event) -> event.registerReloadListener(MCResourceResolver.inst()));
		OverlayHandler.register();
		if (!FMLLoader.isProduction()) {
			RegisterKeyMappingsEvent.BUS.addListener((final RegisterKeyMappingsEvent event) -> event.register(DemoKey.KEY));
			TickEvent.ClientTickEvent.Post.BUS.addListener((final TickEvent.ClientTickEvent.Post event) -> DemoKey.tick());
		}
	}

}