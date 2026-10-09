package dev.joid.backend.minecraft.forge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.eventbus.api.bus.BusGroup;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register(final BusGroup modBusGroup) {
		ForgeClientTicks.register();
		ForgeScreenEvents.register();
		ForgeOverlayLayers.register();
		ForgePayloads.registerSender();
		ForgeReloadListeners.register();
		ForgeContainerScreens.register(modBusGroup);
	}

}