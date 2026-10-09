package dev.joid.backend.minecraft.fabric.network;

import dev.joid.backend.minecraft.bridge.network.PayloadPacket;
import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricPayloads {

	public static void register() {
		for (final PayloadType<?> type : PayloadType.getRegistered()) {
			FabricPayloads.register(type);
		}
	}

	public static void registerSender() {
		PayloadType.sender(payload -> ClientPlayNetworking.send(new PayloadPacket<>(payload)));
	}

	private static <T extends IPayload> void register(final PayloadType<T> type) {
		PayloadTypeRegistry.serverboundPlay().register(PayloadPacket.getType(type), PayloadPacket.codec(type));
		ServerPlayNetworking.registerGlobalReceiver(PayloadPacket.<T>getType(type), (packet, context) -> type.getOnServer().accept(packet.payload(), context.player()));
	}

}