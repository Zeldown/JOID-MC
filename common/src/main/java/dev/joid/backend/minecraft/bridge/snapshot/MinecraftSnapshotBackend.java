package dev.joid.backend.minecraft.bridge.snapshot;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.base.opengl.binding.GlConstants;
import dev.joid.base.opengl.render.GlRenderBridge;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.utils.image.PixelLayout;
import dev.joid.test.snapshot.ISnapshotBackend;
import dev.joid.test.snapshot.SnapshotImage;
import lombok.NonNull;

public final class MinecraftSnapshotBackend implements ISnapshotBackend {

	private GlRenderBridge        bridge;
	private MinecraftGlState      state;
	private MinecraftRenderTarget target;

	@Override
	public void create(final int width, final int height) {
		this.bridge = BridgeHandler.RENDER.getBridge(GlRenderBridge.class);
		this.state  = MinecraftGlState.read();
		this.target = new MinecraftRenderTarget(width, height);
		this.target.bind();
	}

	@Override
	public void destroy() {
		this.target.delete();
		this.target = null;
		this.state.restore();
	}

	@Override
	public void present() {}

	@Override
	public @NonNull SnapshotImage capture(final int width, final int height) {
		final ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		this.bridge.getGuard().run(() -> {
			this.bridge.getFrameBufferBinding().bindFramebuffer(GlConstants.FRAMEBUFFER, this.target.getFrameBuffer());
			this.bridge.getBinding().readPixels(0, 0, width, height, GlConstants.RGBA, GlConstants.UNSIGNED_BYTE, pixels);
		});
		return SnapshotImage.from(pixels, width, height, true, PixelLayout.RGBA8);
	}

	@Override
	public @NonNull String getRenderer() {
		return GL11.glGetString(GL11.GL_RENDERER);
	}

}