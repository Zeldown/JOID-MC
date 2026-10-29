package dev.joid.backend.minecraft.demo.network;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.loader.network.IPayload;
import dev.joid.backend.minecraft.loader.network.IPayloadWriter;
import dev.joid.backend.minecraft.loader.network.PayloadType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenDemoContainerPayload implements IPayload {

	public static final OpenDemoContainerPayload                              INSTANCE = new OpenDemoContainerPayload();
	public static final PayloadType<OpenDemoContainerPayload, EntityPlayerMP> TYPE     = PayloadType.create(MinecraftJoid.MOD_ID + ":demo/container", reader -> OpenDemoContainerPayload.INSTANCE);

	@Override
	public PayloadType<OpenDemoContainerPayload, EntityPlayerMP> getType() {
		return OpenDemoContainerPayload.TYPE;
	}

	@Override
	public void write(final IPayloadWriter writer) {}

}