package dev.joid.impl.minecraft.render;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import net.minecraft.client.Minecraft;

public final class RenderTargetPool {

	private final RenderBridge       bridge;
	private final List<RenderTarget> targetList;

	private int  index;
	private long frameTime;

	private RenderTargetPool(final RenderBridge bridge) {
		this.bridge     = bridge;
		this.targetList = new ArrayList<>();
		this.frameTime  = -1L;
	}

	public static @NonNull RenderTargetPool create(final @NonNull RenderBridge bridge) {
		return new RenderTargetPool(bridge);
	}

	public @NonNull RenderTarget acquire(final int width, final int height) {
		final long frameTime = Minecraft.getInstance().getFrameTimeNs();
		if (frameTime != this.frameTime) {
			this.frameTime = frameTime;
			this.index     = 0;
		}

		if (this.index == this.targetList.size()) {
			this.targetList.add(RenderTarget.create(this.bridge, width, height, true));
		}

		RenderTarget target = this.targetList.get(this.index);
		if (target.getWidth() != width || target.getHeight() != height) {
			target.delete();
			target = RenderTarget.create(this.bridge, width, height, true);
			this.targetList.set(this.index, target);
		}

		this.index++;
		return target;
	}

}