package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class JOIDBlaze3DClient implements ClientModInitializer {

	private static boolean registered;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> JOIDBlaze3DClient.register());
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> dispatcher.register(JOIDCommand.create()));
		OverlayHandler.register();
	}

	private static void register() {
		if (JOIDBlaze3DClient.registered) {
			return;
		}

		JOIDBlaze3DClient.registered = true;
		JOIDMC.register(FabricLoader.getInstance().isDevelopmentEnvironment());
	}

}