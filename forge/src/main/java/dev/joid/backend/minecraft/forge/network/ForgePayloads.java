package dev.joid.backend.minecraft.forge.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.network.PayloadMessage;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
		MinecraftForge.EVENT_BUS.register(new ForgePayloads());
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