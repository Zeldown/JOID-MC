package dev.joid.backend.minecraft.bridge.network;

import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.PayloadType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PayloadPacket<T extends IPayload>(T payload) implements CustomPacketPayload {

	@Override
	public CustomPacketPayload.Type<PayloadPacket<T>> type() {
		return PayloadPacket.getType(this.payload.getType());
	}

	public static <T extends IPayload> CustomPacketPayload.Type<PayloadPacket<T>> getType(final PayloadType<?> type) {
		return new CustomPacketPayload.Type<>(Identifier.parse(type.getId()));
	}

	public static <T extends IPayload> StreamCodec<RegistryFriendlyByteBuf, PayloadPacket<T>> codec(final PayloadType<T> type) {
		return StreamCodec.of((buffer, packet) -> packet.payload().write(ByteBufPayloadBuffer.create(buffer)), buffer -> new PayloadPacket<>(type.getReader().apply(ByteBufPayloadBuffer.create(buffer))));
	}

}