package dev.joid.impl.minecraft.render.framebuffer;

import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.impl.minecraft.render.RenderTarget;
import dev.joid.impl.minecraft.render.texture.Texture;
import dev.joid.lib.bridge.render.framebuffer.IFrameBuffer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FrameBuffer implements IFrameBuffer {

	private final RenderTarget target;

	public static @NonNull FrameBuffer create(final @NonNull RenderBridge bridge, final int width, final int height) {
		return new FrameBuffer(RenderTarget.create(bridge, width, height, false));
	}

	@Override
	public int getWidth() {
		return this.target.getWidth();
	}

	@Override
	public int getHeight() {
		return this.target.getHeight();
	}

	@Override
	public @NonNull Texture getTexture() {
		return this.target.getTexture();
	}

	@Override
	public void delete() {
		this.target.delete();
	}

}