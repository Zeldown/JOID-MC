package dev.joid.backend.minecraft.bridge.render.framebuffer;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.lib.bridge.render.framebuffer.FrameBufferHandle;
import lombok.Getter;

@Getter
public final class MinecraftFrameBuffer extends FrameBufferHandle<MinecraftTexture> {

	private final MinecraftRenderTarget target;

	private MinecraftFrameBuffer(final MinecraftRenderTarget target) {
		super(target.getTexture());
		this.target = target;
	}

	public MinecraftFrameBuffer(final MinecraftRenderBridge bridge, final int width, final int height) {
		this(new MinecraftRenderTarget(bridge, width, height, false));
	}

	@Override
	protected void deleteHandle() {
		this.target.delete();
	}

}