package dev.joid.backend.minecraft.bridge.render;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import net.minecraft.client.Minecraft;

public final class MinecraftRenderTargetPool {

	private final MinecraftRenderBridge       bridge;
	private final List<MinecraftRenderTarget> targetList;

	private int  index;
	private long frameTime;

	private MinecraftRenderTargetPool(final MinecraftRenderBridge bridge) {
		this.bridge     = bridge;
		this.targetList = new ArrayList<>();
		this.frameTime  = -1L;
	}

	public static @NonNull MinecraftRenderTargetPool create(final @NonNull MinecraftRenderBridge bridge) {
		return new MinecraftRenderTargetPool(bridge);
	}

	public @NonNull MinecraftRenderTarget acquire(final int width, final int height) {
		final long frameTime = Minecraft.getInstance().getFrameTimeNs();
		if (frameTime != this.frameTime) {
			this.frameTime = frameTime;
			this.index     = 0;
		}

		if (this.index == this.targetList.size()) {
			this.targetList.add(MinecraftRenderTarget.create(this.bridge, width, height, true));
		}

		MinecraftRenderTarget target = this.targetList.get(this.index);
		if (target.getWidth() != width || target.getHeight() != height) {
			target.delete();
			target = MinecraftRenderTarget.create(this.bridge, width, height, true);
			this.targetList.set(this.index, target);
		}

		this.index++;
		return target;
	}

}