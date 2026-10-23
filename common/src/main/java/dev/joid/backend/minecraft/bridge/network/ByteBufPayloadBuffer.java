package dev.joid.backend.minecraft.bridge.network;

import java.util.UUID;

import dev.joid.backend.minecraft.loader.network.IPayloadReader;
import dev.joid.backend.minecraft.loader.network.IPayloadWriter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.network.FriendlyByteBuf;

@AllArgsConstructor
public final class ByteBufPayloadBuffer implements IPayloadReader, IPayloadWriter {

	private final FriendlyByteBuf buffer;

	@Override
	public int readInt() {
		return this.buffer.readInt();
	}

	@Override
	public long readLong() {
		return this.buffer.readLong();
	}

	@Override
	public float readFloat() {
		return this.buffer.readFloat();
	}

	@Override
	public double readDouble() {
		return this.buffer.readDouble();
	}

	@Override
	public boolean readBoolean() {
		return this.buffer.readBoolean();
	}

	@Override
	public byte[] readBytes() {
		return this.buffer.readByteArray();
	}

	@Override
	public @NonNull UUID readUuid() {
		return this.buffer.readUUID();
	}

	@Override
	public @NonNull String readString() {
		return this.buffer.readUtf();
	}

	@Override
	public @NonNull IPayloadWriter writeInt(final int value) {
		this.buffer.writeInt(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeLong(final long value) {
		this.buffer.writeLong(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeFloat(final float value) {
		this.buffer.writeFloat(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeDouble(final double value) {
		this.buffer.writeDouble(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeBoolean(final boolean value) {
		this.buffer.writeBoolean(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeBytes(final byte[] value) {
		this.buffer.writeByteArray(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeUuid(final @NonNull UUID value) {
		this.buffer.writeUUID(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeString(final @NonNull String value) {
		this.buffer.writeUtf(value);
		return this;
	}

}