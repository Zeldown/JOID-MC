package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.bridge.ui.TooltipQueue;
import dev.joid.lib.bridge.ui.StackUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.core.data.UIDataOverlayObject;
import dev.joid.lib.utils.annotation.AnnotationValues;
import lombok.NonNull;

public abstract class BaseScreenUIBridge<G> extends StackUIBridge {

	private final TooltipQueue tooltipQueue;

	protected BaseScreenUIBridge() {
		this.tooltipQueue = new TooltipQueue();
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return !ui.getOverlay().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return !AnnotationValues.read(clazz.getAnnotation(UIDataOverlay.class), UIDataOverlayObject.create()).active();
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull Object content, final double mouseX, final double mouseY) {
		this.tooltipQueue.push(content);
	}

	public final void frame(final G graphics, final int mouseX, final int mouseY) {
		this.countFrame(graphics);
		super.update();
		this.compositeFrame(graphics, super::draw);
		this.requestCursor(graphics);
		this.tooltipQueue.flush(content -> this.drawTooltip(graphics, content, mouseX, mouseY));
	}

	protected abstract long countFrame(final G graphics);
	protected abstract void requestCursor(final G graphics);
	protected abstract void compositeFrame(final G graphics, final Runnable draw);
	protected abstract void drawTooltip(final G graphics, final Object content, final int mouseX, final int mouseY);

}