package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.window.IWindowBridge;

public final class GuiCompositor {

	private final IRenderBridge render;
	private final IWindowBridge window;

	public GuiCompositor(final IRenderBridge render, final IWindowBridge window) {
		this.render = render;
		this.window = window;
	}

	public void composite(final Runnable draw) {
		this.render.beginFrame();
		try {
			this.render.screen(this.window.getWidth(), this.window.getHeight());
			this.render.clearDepth();
			this.render.clearStencil();
			draw.run();
		} finally {
			this.render.endFrame();
		}
	}

}