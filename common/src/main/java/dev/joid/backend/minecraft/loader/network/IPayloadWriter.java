package dev.joid.backend.minecraft.loader.network;

import java.util.UUID;

import lombok.NonNull;

public interface IPayloadWriter {

	public @NonNull IPayloadWriter writeInt(final int value);
	public @NonNull IPayloadWriter writeLong(final long value);
	public @NonNull IPayloadWriter writeFloat(final float value);
	public @NonNull IPayloadWriter writeDouble(final double value);
	public @NonNull IPayloadWriter writeBoolean(final boolean value);

	public @NonNull IPayloadWriter writeBytes(final byte[] value);
	public @NonNull IPayloadWriter writeUuid(final @NonNull UUID value);
	public @NonNull IPayloadWriter writeString(final @NonNull String value);

}