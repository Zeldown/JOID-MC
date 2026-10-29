package dev.joid.backend.minecraft.loader.network;

import java.util.UUID;

import lombok.NonNull;

public interface IPayloadReader {

	public int readInt();
	public long readLong();
	public float readFloat();
	public double readDouble();
	public boolean readBoolean();

	public byte[] readBytes();
	public @NonNull UUID readUuid();
	public @NonNull String readString();

}