package dev.joid.backend.minecraft.bridge.ui.overlay;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.lib.ui.core.UI;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class OverlayUIBridge extends BaseOverlayUIBridge<GuiGraphics, OverlayLayer> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	public OverlayUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		super(window);
		this.window     = window;
		this.compositor = new GuiCompositor(render, window);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected long countFrame(final GuiGraphics graphics) {
		return GuiFrameCounter.inst().getFrame(graphics);
	}

	@Override
	protected void requestCursor(final GuiGraphics graphics) {
		this.window.requestCursor(this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final GuiGraphics graphics, final Runnable draw) {
		this.compositor.composite(graphics, draw);
	}

	@Override
	protected void drawTooltip(final GuiGraphics graphics, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(graphics, content, mouseX, mouseY);
	}

	@Override
	protected int getGuiMouseX() {
		final Minecraft minecraft = Minecraft.getInstance();
		return (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
	}

	@Override
	protected int getGuiMouseY() {
		final Minecraft minecraft = Minecraft.getInstance();
		return (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
	}

	@Override
	protected boolean isHudHidden() {
		final Minecraft minecraft = Minecraft.getInstance();
		return minecraft.options.hideGui && minecraft.screen == null;
	}

	@Override
	protected boolean isScreenDisplayed() {
		return Minecraft.getInstance().screen != null;
	}

	@Override
	protected OverlayLayer getLayer(final UI ui) {
		final UIDataOverlayLayer data = OverlayUIBridge.getLayerData(ui);
		return data != null ? data.layer() : null;
	}

	@Override
	protected boolean isPostLayer(final UI ui) {
		return OverlayUIBridge.getLayerData(ui).post();
	}

	@Override
	protected boolean isCancellingLayer(final UI ui) {
		return OverlayUIBridge.getLayerData(ui).cancel();
	}

	private static UIDataOverlayLayer getLayerData(final UI ui) {
		return ui.getClass().getAnnotation(UIDataOverlayLayer.class);
	}

}