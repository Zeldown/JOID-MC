package dev.joid.backend.minecraft.loader.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PayloadRegistry {

	private static final List<PayloadDeclaration<?>> DECLARATIONS = new ArrayList<>();

	private static Consumer<CustomPacketPayload> sender;

	public static <T extends CustomPacketPayload> void register(final CustomPacketPayload.Type<T> type, final StreamCodec<RegistryFriendlyByteBuf, T> codec, final BiConsumer<T, ServerPlayer> handler) {
		PayloadRegistry.DECLARATIONS.add(new PayloadDeclaration<>(type, codec, handler));
	}

	public static void sender(final Consumer<CustomPacketPayload> sender) {
		PayloadRegistry.sender = sender;
	}

	public static void send(final CustomPacketPayload payload) {
		if (PayloadRegistry.sender == null) {
			throw new IllegalStateException("No payload sender registered, the payload " + payload.type().id() + " can only be sent from a client");
		}
		PayloadRegistry.sender.accept(payload);
	}

	public static List<PayloadDeclaration<?>> getDeclarations() {
		return Collections.unmodifiableList(PayloadRegistry.DECLARATIONS);
	}

}