package dev.joid.backend.minecraft.neoforge.network;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.network.PayloadMessage;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgePayloads {

	public static void register() {
		if (PayloadType.getRegistered().isEmpty()) {
			return;
		}

		final SimpleChannel channel = NetworkRegistry.newSimpleChannel(ResourceLocation.tryBuild(MinecraftJoid.MOD_ID, "main"), () -> "1", NetworkRegistry.acceptMissingOr("1"), NetworkRegistry.acceptMissingOr("1"));
		channel.messageBuilder(PayloadMessage.class, 0, NetworkDirection.PLAY_TO_SERVER).encoder(PayloadMessage::write).decoder(PayloadMessage::read).consumerMainThread((message, context) -> message.fireServer(context.get().getSender())).add();
		PayloadType.sender(payload -> channel.sendToServer(new PayloadMessage(payload)));
	}

}