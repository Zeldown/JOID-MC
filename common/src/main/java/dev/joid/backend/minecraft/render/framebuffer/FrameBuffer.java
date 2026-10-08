package dev.joid.backend.minecraft.render.framebuffer;

import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.RenderTarget;
import dev.joid.backend.minecraft.render.texture.Texture;
import dev.joid.lib.bridge.render.framebuffer.FrameBufferHandle;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class FrameBuffer extends FrameBufferHandle<Texture> {

	private final RenderTarget target;

	private FrameBuffer(final RenderTarget target) {
		super(target.getTexture());
		this.target = target;
	}

	public static @NonNull FrameBuffer create(final @NonNull RenderBridge bridge, final int width, final int height) {
		return new FrameBuffer(RenderTarget.create(bridge, width, height, false));
	}

	@Override
	protected void onDelete() {
		this.target.delete();
	}

}