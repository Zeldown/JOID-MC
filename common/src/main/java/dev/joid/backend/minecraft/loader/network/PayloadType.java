package dev.joid.backend.minecraft.loader.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;

import net.minecraft.server.level.ServerPlayer;

@Getter
public final class PayloadType<T extends IPayload> {

	private static final List<PayloadType<?>> REGISTERED = new ArrayList<>();

	private static Consumer<IPayload> sender;

	private final String                      id;
	private final Function<IPayloadReader, T> reader;

	private BiConsumer<T, ServerPlayer> onServer;

	private PayloadType(final String id, final Function<IPayloadReader, T> reader) {
		this.id       = id;
		this.reader   = reader;
		this.onServer = (_, _) -> {};
	}

	public static <T extends IPayload> @NonNull PayloadType<T> create(final @NonNull String id, final @NonNull Function<IPayloadReader, T> reader) {
		return new PayloadType<>(id, reader);
	}

	public @NonNull PayloadType<T> onServer(final @NonNull BiConsumer<T, ServerPlayer> onServer) {
		this.onServer = onServer;
		return this;
	}

	public @NonNull PayloadType<T> register() {
		PayloadType.REGISTERED.add(this);
		return this;
	}

	public static void sender(final @NonNull Consumer<IPayload> sender) {
		PayloadType.sender = sender;
	}

	public static void send(final @NonNull IPayload payload) {
		if (PayloadType.sender == null) {
			throw new IllegalStateException("No payload sender registered, the payload " + payload.getType().getId() + " can only be sent from a client");
		}
		PayloadType.sender.accept(payload);
	}

	public static @NonNull List<PayloadType<?>> getRegistered() {
		return Collections.unmodifiableList(PayloadType.REGISTERED);
	}

}