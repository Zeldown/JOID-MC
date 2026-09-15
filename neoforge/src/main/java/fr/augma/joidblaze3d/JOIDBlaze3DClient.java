package fr.augma.joidblaze3d;

import fr.augma.joidblaze3d.command.JOIDCommand;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class JOIDBlaze3DClient {

	public JOIDBlaze3DClient(final IEventBus eventBus) {
		eventBus.addListener((final FMLClientSetupEvent event) -> event.enqueueWork(Backend::register));
		NeoForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent event) -> event.getDispatcher().register(JOIDCommand.create()));
	}

}