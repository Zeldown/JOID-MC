package dev.joid.backend.minecraft.demo.network;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.IPayloadWriter;
import dev.joid.backend.minecraft.loader.network.PayloadType;

public record OpenDemoContainerPayload() implements IPayload {

	public static final OpenDemoContainerPayload              INSTANCE = new OpenDemoContainerPayload();
	public static final PayloadType<OpenDemoContainerPayload> TYPE     = PayloadType.create(Backend.MOD_ID + ":demo/container", _ -> OpenDemoContainerPayload.INSTANCE);

	@Override
	public PayloadType<OpenDemoContainerPayload> getType() {
		return OpenDemoContainerPayload.TYPE;
	}

	@Override
	public void write(final IPayloadWriter writer) {}

}