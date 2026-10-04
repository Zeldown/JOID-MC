package fr.augma.joidmc;

import fr.augma.joidmc.command.JOIDCommand;
import fr.augma.joidmc.overlay.OverlayHandler;
import fr.augma.joidmc.render.resource.MCResourceResolver;

import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class JOIDMCClient {

	private static boolean registered;

	public JOIDMCClient(final IEventBus eventBus) {
		NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Post event) -> JOIDMCClient.register());
		NeoForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent event) -> event.getDispatcher().register(JOIDCommand.create()));
		eventBus.addListener((final AddClientReloadListenersEvent event) -> event.addListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resources"), MCResourceResolver.inst()));
		OverlayHandler.register();
	}

	private static void register() {
		if (JOIDMCClient.registered) {
			return;
		}

		JOIDMCClient.registered = true;
		JOIDMC.register(!FMLLoader.getCurrent().isProduction());
	}

}