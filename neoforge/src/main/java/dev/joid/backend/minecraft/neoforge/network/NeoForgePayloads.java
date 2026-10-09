package dev.joid.backend.minecraft.neoforge.network;

import dev.joid.backend.minecraft.bridge.network.PayloadPacket;
import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgePayloads {

	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1").optional();
		for (final PayloadType<?> type : PayloadType.getRegistered()) {
			NeoForgePayloads.register(registrar, type);
		}
	}

	public static void registerSender() {
		PayloadType.sender(payload -> ClientPacketDistributor.sendToServer(new PayloadPacket<>(payload)));
	}

	private static <T extends IPayload> void register(final PayloadRegistrar registrar, final PayloadType<T> type) {
		registrar.playToServer(PayloadPacket.getType(type), PayloadPacket.codec(type), (packet, context) -> type.getOnServer().accept(packet.payload(), (ServerPlayer) context.player()));
	}

}