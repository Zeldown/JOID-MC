package dev.joid.impl.joidmc.lib.bridge.render.framebuffer;

import be.zeldown.joid.lib.bridge.render.framebuffer.IFrameBuffer;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FrameBuffer implements IFrameBuffer {

	private final Texture texture;

	public static @NonNull FrameBuffer create(final RenderBridge bridge, final int width, final int height) {
		return new FrameBuffer(new Texture(bridge).allocate(width, height));
	}

	@Override
	public int getWidth() {
		return this.texture.getWidth();
	}

	@Override
	public int getHeight() {
		return this.texture.getHeight();
	}

	@Override
	public void delete() {
		this.texture.delete();
	}

}