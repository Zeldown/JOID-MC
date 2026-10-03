package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;
import fr.augma.joidblaze3d.render.resource.MCResourceResolver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class JOIDBlaze3DClient implements ClientModInitializer {

	private static boolean registered;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> JOIDBlaze3DClient.register());
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> dispatcher.register(JOIDCommand.create()));
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resources"), MCResourceResolver.inst());
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