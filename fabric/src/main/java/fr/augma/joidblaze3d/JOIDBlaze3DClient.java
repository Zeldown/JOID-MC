package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class JOIDBlaze3DClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> JOIDMC.register());
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> dispatcher.register(JOIDCommand.create()));
	}

}