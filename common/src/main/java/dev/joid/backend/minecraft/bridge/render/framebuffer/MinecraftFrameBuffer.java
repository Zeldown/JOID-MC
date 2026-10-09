package dev.joid.backend.minecraft.bridge.render.framebuffer;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.backend.minecraft.bridge.render.texture.MinecraftTexture;
import dev.joid.lib.bridge.render.framebuffer.FrameBufferHandle;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class MinecraftFrameBuffer extends FrameBufferHandle<MinecraftTexture> {

	private final MinecraftRenderTarget target;

	private MinecraftFrameBuffer(final MinecraftRenderTarget target) {
		super(target.getTexture());
		this.target = target;
	}

	public static @NonNull MinecraftFrameBuffer create(final @NonNull MinecraftRenderBridge bridge, final int width, final int height) {
		return new MinecraftFrameBuffer(MinecraftRenderTarget.create(bridge, width, height, false));
	}

	@Override
	protected void onDelete() {
		this.target.delete();
	}

}