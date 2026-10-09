package dev.joid.backend.minecraft.loader.network;

import lombok.NonNull;

public interface IPayload {

	public @NonNull PayloadType<?> getType();

	public void write(final @NonNull IPayloadWriter writer);

	public default @NonNull IPayload sendToServer() {
		PayloadType.send(this);
		return this;
	}

}