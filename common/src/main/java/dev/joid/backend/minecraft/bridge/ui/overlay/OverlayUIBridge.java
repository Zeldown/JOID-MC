package dev.joid.backend.minecraft.bridge.ui.overlay;

import org.lwjgl.input.Mouse;

import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.ui.core.UI;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class OverlayUIBridge extends BaseOverlayUIBridge<Void, OverlayLayer> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	public OverlayUIBridge(final IRenderBridge render, final MinecraftWindowBridge window) {
		super(window);
		this.window     = window;
		this.compositor = new GuiCompositor(render, window);
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected long countFrame(final Void graphics) {
		return GuiFrameCounter.inst().getFrame();
	}

	@Override
	protected void requestCursor(final Void graphics) {
		this.window.requestCursor(this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final Void graphics, final Runnable draw) {
		this.compositor.composite(draw);
	}

	@Override
	protected void drawTooltip(final Void graphics, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(content, mouseX, mouseY);
	}

	@Override
	protected int getGuiMouseX() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		return Mouse.getX() * new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaledWidth() / minecraft.displayWidth;
	}

	@Override
	protected int getGuiMouseY() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final int height = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaledHeight();
		return height - Mouse.getY() * height / minecraft.displayHeight - 1;
	}

	@Override
	protected boolean isHudHidden() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		return minecraft.gameSettings.hideGUI && minecraft.theWorld != null && minecraft.currentScreen == null;
	}

	@Override
	protected boolean isScreenDisplayed() {
		return Minecraft.getMinecraft().currentScreen != null;
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