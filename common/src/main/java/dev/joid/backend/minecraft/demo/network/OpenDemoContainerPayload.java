package dev.joid.backend.minecraft.demo.network;

import dev.joid.backend.minecraft.Backend;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenDemoContainerPayload() implements CustomPacketPayload {

	public static final OpenDemoContainerPayload                                       INSTANCE     = new OpenDemoContainerPayload();
	public static final CustomPacketPayload.Type<OpenDemoContainerPayload>             TYPE         = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Backend.MOD_ID, "demo/container"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenDemoContainerPayload> STREAM_CODEC = StreamCodec.unit(OpenDemoContainerPayload.INSTANCE);

	@Override
	public CustomPacketPayload.Type<OpenDemoContainerPayload> type() {
		return OpenDemoContainerPayload.TYPE;
	}

}