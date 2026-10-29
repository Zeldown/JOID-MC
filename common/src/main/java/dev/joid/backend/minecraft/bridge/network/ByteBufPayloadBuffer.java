package dev.joid.backend.minecraft.bridge.network;

import java.util.UUID;

import cpw.mods.fml.common.network.ByteBufUtils;
import dev.joid.backend.minecraft.loader.network.IPayloadReader;
import dev.joid.backend.minecraft.loader.network.IPayloadWriter;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public final class ByteBufPayloadBuffer implements IPayloadReader, IPayloadWriter {

	private final ByteBuf buffer;

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
		final byte[] bytes = new byte[ByteBufUtils.readVarInt(this.buffer, 5)];
		this.buffer.readBytes(bytes);
		return bytes;
	}

	@Override
	public @NonNull UUID readUuid() {
		return new UUID(this.buffer.readLong(), this.buffer.readLong());
	}

	@Override
	public @NonNull String readString() {
		return ByteBufUtils.readUTF8String(this.buffer);
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
		ByteBufUtils.writeVarInt(this.buffer, value.length, 5);
		this.buffer.writeBytes(value);
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeUuid(final @NonNull UUID value) {
		this.buffer.writeLong(value.getMostSignificantBits());
		this.buffer.writeLong(value.getLeastSignificantBits());
		return this;
	}

	@Override
	public @NonNull IPayloadWriter writeString(final @NonNull String value) {
		ByteBufUtils.writeUTF8String(this.buffer, value);
		return this;
	}

}