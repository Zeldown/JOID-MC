package dev.joid.backend.minecraft.bridge.network;

import java.util.UUID;

import dev.joid.backend.minecraft.loader.network.IPayloadReader;
import dev.joid.backend.minecraft.loader.network.IPayloadWriter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import net.minecraft.network.FriendlyByteBuf;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ByteBufPayloadBuffer implements IPayloadReader, IPayloadWriter {

	private final FriendlyByteBuf buffer;

	public static ByteBufPayloadBuffer create(final FriendlyByteBuf buffer) {
		return new ByteBufPayloadBuffer(buffer);
	}

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
	public UUID readUuid() {
		return this.buffer.readUUID();
	}

	@Override
	public String readString() {
		return this.buffer.readUtf();
	}

	@Override
	public IPayloadWriter writeInt(final int value) {
		this.buffer.writeInt(value);
		return this;
	}

	@Override
	public IPayloadWriter writeLong(final long value) {
		this.buffer.writeLong(value);
		return this;
	}

	@Override
	public IPayloadWriter writeFloat(final float value) {
		this.buffer.writeFloat(value);
		return this;
	}

	@Override
	public IPayloadWriter writeDouble(final double value) {
		this.buffer.writeDouble(value);
		return this;
	}

	@Override
	public IPayloadWriter writeBoolean(final boolean value) {
		this.buffer.writeBoolean(value);
		return this;
	}

	@Override
	public IPayloadWriter writeBytes(final byte[] value) {
		this.buffer.writeByteArray(value);
		return this;
	}

	@Override
	public IPayloadWriter writeUuid(final UUID value) {
		this.buffer.writeUUID(value);
		return this;
	}

	@Override
	public IPayloadWriter writeString(final String value) {
		this.buffer.writeUtf(value);
		return this;
	}

}