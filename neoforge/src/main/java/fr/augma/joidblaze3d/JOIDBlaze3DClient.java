package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class JOIDBlaze3DClient {

	private static boolean registered;

	public JOIDBlaze3DClient(final IEventBus eventBus) {
		NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Post event) -> JOIDBlaze3DClient.register());
		NeoForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent event) -> event.getDispatcher().register(JOIDCommand.create()));
		OverlayHandler.register();
	}

	private static void register() {
		if (JOIDBlaze3DClient.registered) {
			return;
		}

		JOIDBlaze3DClient.registered = true;
		JOIDMC.register(!FMLLoader.getCurrent().isProduction());
	}

}