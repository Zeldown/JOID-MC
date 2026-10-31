package dev.joid.backend.minecraft.bridge.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import lombok.Getter;

@Getter
public final class MinecraftRenderTarget {

	private final int width;
	private final int height;
	private final int texture;
	private final int frameBuffer;
	private final int depthStencil;

	public MinecraftRenderTarget(final int width, final int height) {
		final int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		final int renderBuffer = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
		final int frameBuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		this.width   = width;
		this.height  = height;
		this.texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		this.depthStencil = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.depthStencil);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
		this.frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBuffer);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.texture, 0);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.depthStencil);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
	}

	public void delete() {
		GL30.glDeleteFramebuffers(this.frameBuffer);
		GL30.glDeleteRenderbuffers(this.depthStencil);
		GL11.glDeleteTextures(this.texture);
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBuffer);
	}

	public boolean hasSize(final int width, final int height) {
		return this.width == width && this.height == height;
	}

}