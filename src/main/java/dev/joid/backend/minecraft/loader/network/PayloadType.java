package dev.joid.backend.minecraft.loader.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class PayloadType<T extends IPayload, P> {

	private static final List<PayloadType<?, ?>> REGISTERED = new ArrayList<>();

	private static Consumer<IPayload> sender;

	private final String                      id;
	private final Function<IPayloadReader, T> reader;

	private BiConsumer<T, P> onServer;

	private PayloadType(final String id, final Function<IPayloadReader, T> reader) {
		this.id       = id;
		this.reader   = reader;
		this.onServer = (payload, player) -> {};
	}

	public static <T extends IPayload, P> @NonNull PayloadType<T, P> create(final @NonNull String id, final @NonNull Function<IPayloadReader, T> reader) {
		return new PayloadType<>(id, reader);
	}

	public @NonNull PayloadType<T, P> onServer(final @NonNull BiConsumer<T, P> onServer) {
		this.onServer = onServer;
		return this;
	}

	public void fireServer(final @NonNull T payload, final @NonNull Object player) {
		this.onServer.accept(payload, (P) player);
	}

	public @NonNull PayloadType<T, P> register() {
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

	public static @NonNull List<PayloadType<?, ?>> getRegistered() {
		return Collections.unmodifiableList(PayloadType.REGISTERED);
	}

}