package dev.joid.backend.minecraft.neoforge.network;

import dev.joid.backend.minecraft.loader.network.PayloadDeclaration;
import dev.joid.backend.minecraft.loader.network.PayloadRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgePayloads {

	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1").optional();
		for (final PayloadDeclaration<?> declaration : PayloadRegistry.getDeclarations()) {
			NeoForgePayloads.register(registrar, declaration);
		}
	}

	public static void registerSender() {
		PayloadRegistry.sender(ClientPacketDistributor::sendToServer);
	}

	private static <T extends CustomPacketPayload> void register(final PayloadRegistrar registrar, final PayloadDeclaration<T> declaration) {
		registrar.playToServer(declaration.type(), declaration.codec(), (payload, context) -> declaration.handler().accept(payload, (ServerPlayer) context.player()));
	}

}