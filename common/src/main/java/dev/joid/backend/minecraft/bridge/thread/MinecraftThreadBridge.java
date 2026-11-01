package dev.joid.backend.minecraft.bridge.thread;

import dev.joid.lib.bridge.thread.IThreadBridge;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class MinecraftThreadBridge implements IThreadBridge {

	@Override
	public boolean isRenderThread() {
		return Minecraft.getMinecraft().isCallingFromMinecraftThread();
	}

	@Override
	public void execute(final @NonNull Runnable runnable) {
		if (this.isRenderThread()) {
			runnable.run();
		} else {
			Minecraft.getMinecraft().addScheduledTask(runnable);
		}
	}

}