package dev.joid.backend.minecraft.bridge.ui.screen;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.base.opengl.render.texture.GlBorrowedTexture;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.draw.DrawUtils;
import lombok.Getter;

public final class GuiCompositor {

	private final IRenderBridge render;
	private final IWindowBridge window;

	@Getter
	private MinecraftRenderTarget target;

	private ITexture texture;

	public GuiCompositor(final IRenderBridge render, final IWindowBridge window) {
		this.render = render;
		this.window = window;
	}

	public void composite(final Runnable draw) {
		final int width = this.window.getWidth();
		final int height = this.window.getHeight();
		if (this.target == null || !this.target.hasSize(width, height)) {
			if (this.target != null) {
				this.target.delete();
			}

			this.target  = new MinecraftRenderTarget(width, height);
			this.texture = GlBorrowedTexture.of(this.target.getTexture());
		}

		final int screen = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		this.target.bind();
		this.render.beginFrame();
		try {
			this.render.screen(width, height);
			this.render.clearColor(0F, 0F, 0F, 0F);
			this.render.clearDepth();
			this.render.clearStencil();
			draw.run();
		} finally {
			this.render.endFrame();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, screen);
		}

		this.render.beginFrame();
		try {
			this.render.screen(width, height);
			DrawUtils.RESOURCE.drawTexture(0D, 0D, width, height, this.texture, 0D, 1D, 1D, 0D, TextureFilter.NEAREST, BlendState.PREMULTIPLIED);
		} finally {
			this.render.endFrame();
		}
	}

}