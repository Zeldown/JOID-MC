package dev.joid.backend.minecraft.bridge.ui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.bridge.input.KeyRepeatFilter;
import dev.joid.backend.minecraft.bridge.render.GuiFrameCounter;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class OverlayUIBridge extends BaseOverlayUIBridge<PoseStack, OverlayLayer> {

	private final GuiCompositor         compositor;
	private final MinecraftWindowBridge window;

	@Getter
	private final KeyRepeatFilter keyRepeats;

	public OverlayUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		super(window);
		this.window     = window;
		this.compositor = new GuiCompositor(render, window);
		this.keyRepeats = new KeyRepeatFilter();
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	protected long countFrame(final PoseStack pose) {
		return GuiFrameCounter.inst().getFrame(pose);
	}

	@Override
	protected void requestCursor(final PoseStack pose) {
		this.window.requestCursor(this.window.getCursor());
	}

	@Override
	protected void compositeFrame(final PoseStack pose, final Runnable draw) {
		this.compositor.composite(draw);
	}

	@Override
	protected void drawTooltip(final PoseStack pose, final Object content, final int mouseX, final int mouseY) {
		TooltipRenderer.draw(pose, content, mouseX, mouseY);
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