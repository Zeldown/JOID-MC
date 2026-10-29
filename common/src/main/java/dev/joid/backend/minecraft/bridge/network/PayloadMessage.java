package dev.joid.backend.minecraft.bridge.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class PayloadMessage implements IMessage {

	private IPayload payload;

	@Override
	public void fromBytes(final ByteBuf buffer) {
		final String id = ByteBufUtils.readUTF8String(buffer);
		for (final PayloadType<?, ?> type : PayloadType.getRegistered()) {
			if (type.getId().equals(id)) {
				this.payload = type.getReader().apply(new ByteBufPayloadBuffer(buffer));
				return;
			}
		}
		throw new IllegalArgumentException("No payload type " + id + " is registered on this side");
	}

	@Override
	public void toBytes(final ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.payload.getType().getId());
		this.payload.write(new ByteBufPayloadBuffer(buffer));
	}

	public <T extends IPayload> void fireServer(final Object player) {
		((PayloadType<T, ?>) this.payload.getType()).fireServer((T) this.payload, player);
	}

}