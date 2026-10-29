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
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class OverlayUIBridge extends BaseOverlayUIBridge<GuiGraphicsExtractor, OverlayLayer> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	public OverlayUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		super(window);
		this.window     = window;
		this.compositor = new GuiCompositor(render);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected long countFrame(final GuiGraphicsExtractor graphics) {
		return GuiFrameCounter.inst().getFrame(graphics);
	}

	@Override
	protected void requestCursor(final GuiGraphicsExtractor graphics) {
		this.window.requestCursor(graphics, this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final GuiGraphicsExtractor graphics, final Runnable draw) {
		this.compositor.composite(graphics, draw);
	}

	@Override
	protected void drawTooltip(final GuiGraphicsExtractor graphics, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(graphics, content, mouseX, mouseY);
	}

	@Override
	protected int getGuiMouseX() {
		final Minecraft minecraft = Minecraft.getInstance();
		return (int) minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
	}

	@Override
	protected int getGuiMouseY() {
		final Minecraft minecraft = Minecraft.getInstance();
		return (int) minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
	}

	@Override
	protected boolean isHudHidden() {
		return Minecraft.getInstance().gui.hud.isHidden();
	}

	@Override
	protected boolean isScreenDisplayed() {
		return Minecraft.getInstance().gui.screen() != null;
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