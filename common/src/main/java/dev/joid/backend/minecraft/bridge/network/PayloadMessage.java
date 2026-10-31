package dev.joid.backend.minecraft.bridge.network;

import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;

@Getter
@AllArgsConstructor
public final class PayloadMessage {

	private final IPayload payload;

	public static PayloadMessage read(final FriendlyByteBuf buffer) {
		final String id = buffer.readUtf();
		for (final PayloadType<?, ?> type : PayloadType.getRegistered()) {
			if (type.getId().equals(id)) {
				return new PayloadMessage(type.getReader().apply(new ByteBufPayloadBuffer(buffer)));
			}
		}
		throw new IllegalArgumentException("No payload type " + id + " is registered on this side");
	}

	public void write(final FriendlyByteBuf buffer) {
		buffer.writeUtf(this.payload.getType().getId());
		this.payload.write(new ByteBufPayloadBuffer(buffer));
	}

	public <T extends IPayload> void fireServer(final Object player) {
		((PayloadType<T, ?>) this.payload.getType()).fireServer((T) this.payload, player);
	}

}