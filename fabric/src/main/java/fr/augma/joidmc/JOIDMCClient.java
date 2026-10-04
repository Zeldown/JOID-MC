package fr.augma.joidmc;

import fr.augma.joidmc.demo.key.DemoKey;
import fr.augma.joidmc.overlay.OverlayHandler;
import fr.augma.joidmc.render.resource.MCResourceResolver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class JOIDMCClient implements ClientModInitializer {

	private static boolean registered;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> JOIDMCClient.register());
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resources"), MCResourceResolver.inst());
		OverlayHandler.register();
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			KeyMappingHelper.registerKeyMapping(DemoKey.KEY);
			ClientTickEvents.END_CLIENT_TICK.register(minecraft -> DemoKey.tick());
		}
	}

	private static void register() {
		if (JOIDMCClient.registered) {
			return;
		}

		JOIDMCClient.registered = true;
		JOIDMC.register(FabricLoader.getInstance().isDevelopmentEnvironment());
	}

}