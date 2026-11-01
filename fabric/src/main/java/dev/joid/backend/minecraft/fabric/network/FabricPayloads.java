package dev.joid.backend.minecraft.fabric.network;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.network.PayloadMessage;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricPayloads {

	private static final ResourceLocation CHANNEL = new ResourceLocation(MinecraftJoid.MOD_ID, "main");

	public static void register() {
		if (!PayloadType.getRegistered().isEmpty()) {
			ServerPlayNetworking.registerGlobalReceiver(FabricPayloads.CHANNEL, (server, player, handler, buffer, sender) -> {
				final PayloadMessage message = PayloadMessage.read(buffer);
				server.execute(() -> message.fireServer(player));
			});
		}
	}

	public static void registerSender() {
		PayloadType.sender(payload -> {
			final FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
			new PayloadMessage(payload).write(buffer);
			ClientPlayNetworking.send(FabricPayloads.CHANNEL, buffer);
		});
	}

}