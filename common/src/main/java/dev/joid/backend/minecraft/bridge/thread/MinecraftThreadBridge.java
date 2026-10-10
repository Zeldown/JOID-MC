package dev.joid.backend.minecraft.bridge.thread;

import dev.joid.lib.bridge.thread.IThreadBridge;

import net.minecraft.client.Minecraft;

public final class MinecraftThreadBridge implements IThreadBridge {

	@Override
	public boolean isRenderThread() {
		return Minecraft.getInstance().isSameThread();
	}

	@Override
	public void execute(final Runnable runnable) {
		Minecraft.getInstance().execute(runnable);
	}

}