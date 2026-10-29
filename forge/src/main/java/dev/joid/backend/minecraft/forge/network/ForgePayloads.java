package dev.joid.backend.minecraft.forge.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.network.PayloadMessage;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgePayloads {

	private static final Queue<Runnable> SERVER_TASKS = new ConcurrentLinkedQueue<>();

	public static void register() {
		if (PayloadType.getRegistered().isEmpty()) {
			return;
		}

		final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(MinecraftJoid.MOD_ID);
		channel.registerMessage((final PayloadMessage message, final MessageContext context) -> ForgePayloads.receive(message, context.getServerHandler().playerEntity), PayloadMessage.class, 0, Side.SERVER);
		PayloadType.sender(payload -> channel.sendToServer(new PayloadMessage(payload)));
		FMLCommonHandler.instance().bus().register(new ForgePayloads());
	}

	@SubscribeEvent
	public void tick(final ServerTickEvent event) {
		if (event.phase != Phase.START) {
			return;
		}

		for (Runnable task = ForgePayloads.SERVER_TASKS.poll(); task != null; task = ForgePayloads.SERVER_TASKS.poll()) {
			task.run();
		}
	}

	private static IMessage receive(final PayloadMessage message, final EntityPlayerMP player) {
		ForgePayloads.SERVER_TASKS.add(() -> message.fireServer(player));
		return null;
	}

}