package dev.joid.backend.minecraft.fabric;

import dev.joid.backend.minecraft.registry.PayloadDeclaration;
import dev.joid.backend.minecraft.registry.PayloadRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricPayloads {

	public static void register() {
		for (final PayloadDeclaration<?> declaration : PayloadRegistry.getDeclarations()) {
			FabricPayloads.register(declaration);
		}
	}

	public static void registerSender() {
		PayloadRegistry.sender(ClientPlayNetworking::send);
	}

	private static <T extends CustomPacketPayload> void register(final PayloadDeclaration<T> declaration) {
		PayloadTypeRegistry.serverboundPlay().register(declaration.type(), declaration.codec());
		ServerPlayNetworking.registerGlobalReceiver(declaration.type(), (payload, context) -> declaration.handler().accept(payload, context.player()));
	}

}