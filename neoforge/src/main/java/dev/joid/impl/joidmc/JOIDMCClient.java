package dev.joid.impl.joidmc;

import dev.joid.impl.joidmc.demo.DemoKey;
import dev.joid.impl.joidmc.lib.resource.dto.resolver.impl.MCResourceResolver;
import dev.joid.impl.joidmc.overlay.OverlayHandler;

import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class JOIDMCClient {

	private static boolean registered;

	public JOIDMCClient(final IEventBus eventBus) {
		NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Post event) -> JOIDMCClient.register());
		eventBus.addListener((final AddClientReloadListenersEvent event) -> event.addListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resources"), MCResourceResolver.inst()));
		OverlayHandler.register();
		if (!FMLLoader.getCurrent().isProduction()) {
			eventBus.addListener((final RegisterKeyMappingsEvent event) -> event.register(DemoKey.KEY));
			NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Post event) -> DemoKey.tick());
		}
	}

	private static void register() {
		if (JOIDMCClient.registered) {
			return;
		}

		JOIDMCClient.registered = true;
		JOIDMC.register(!FMLLoader.getCurrent().isProduction());
	}

}