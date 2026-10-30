package dev.joid.backend.minecraft.demo.snapshot;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import dev.joid.base.opengl.render.GlRenderBridge;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.utils.image.PixelLayout;
import dev.joid.test.snapshot.ISnapshotBackend;
import dev.joid.test.snapshot.SnapshotImage;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public final class MinecraftSnapshotBackend implements ISnapshotBackend {

	@Getter
	private GlRenderBridge bridge;

	private Framebuffer framebuffer;

	@Override
	public void create(final int width, final int height) {
		this.bridge      = BridgeHandler.RENDER.getBridge(GlRenderBridge.class);
		this.framebuffer = new Framebuffer(width, height, true);
		this.framebuffer.bindFramebuffer(true);
	}

	@Override
	public void destroy() {
		this.framebuffer.deleteFramebuffer();
		this.framebuffer = null;
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
	}

	@Override
	public void present() {}

	@Override
	public @NonNull SnapshotImage capture(final int width, final int height) {
		final ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		this.framebuffer.bindFramebuffer(false);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 4);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		return SnapshotImage.from(pixels, width, height, true, PixelLayout.RGBA8);
	}

	@Override
	public @NonNull String getRenderer() {
		return GL11.glGetString(GL11.GL_RENDERER);
	}

}