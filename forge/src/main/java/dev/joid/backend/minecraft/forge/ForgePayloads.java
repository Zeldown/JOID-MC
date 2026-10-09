package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.registry.PayloadDeclaration;
import dev.joid.backend.minecraft.registry.PayloadRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadFlow;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgePayloads {

	private static Channel<CustomPacketPayload> channel;

	public static void register() {
		if (PayloadRegistry.getDeclarations().isEmpty()) {
			return;
		}

		PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> flow = ChannelBuilder.named(Identifier.fromNamespaceAndPath(MinecraftBackend.MOD_ID, "main")).networkProtocolVersion(1).optional().payloadChannel().play().serverbound();
		for (final PayloadDeclaration<?> declaration : PayloadRegistry.getDeclarations()) {
			flow = ForgePayloads.add(flow, declaration);
		}
		ForgePayloads.channel = flow.build();
	}

	public static void registerSender() {
		if (ForgePayloads.channel != null) {
			PayloadRegistry.sender(payload -> ForgePayloads.channel.send(payload, PacketDistributor.SERVER.noArg()));
		}
	}

	private static <T extends CustomPacketPayload> PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> add(final PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> flow, final PayloadDeclaration<T> declaration) {
		return flow.addMain(declaration.type(), declaration.codec(), (payload, context) -> declaration.handler().accept(payload, context.getSender()));
	}

}