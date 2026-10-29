package dev.joid.backend.minecraft.bridge.ui.overlay;

import java.util.function.Predicate;

import dev.joid.backend.minecraft.bridge.ui.TooltipQueue;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.core.data.UIDataOverlayObject;
import dev.joid.lib.ui.core.data.UIDataOverlayRenderObject;
import dev.joid.lib.utils.annotation.AnnotationValues;
import lombok.NonNull;

public abstract class BaseOverlayUIBridge<G, L> extends UIBridge {

	private final TooltipQueue  tooltipQueue;
	private final IWindowBridge window;

	private int     width;
	private int     height;
	private long    frame;
	private boolean layerPass;

	protected BaseOverlayUIBridge(final IWindowBridge window) {
		this.window       = window;
		this.tooltipQueue = new TooltipQueue();
		this.width        = window.getWidth();
		this.height       = window.getHeight();
		this.frame        = -1L;
	}

	@Override
	public void open(final @NonNull UI ui) {
		this.add(ui);
	}

	@Override
	public void close(final @NonNull UI ui) {
		super.remove(ui);
	}

	@Override
	public void add(final @NonNull UI ui) {
		super.add(ui);
		ui.load(this.window.getWidth(), this.window.getHeight());
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return ui.getOverlay().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return AnnotationValues.read(clazz.getAnnotation(UIDataOverlay.class), UIDataOverlayObject.create()).active();
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull Object content, final double mouseX, final double mouseY) {
		this.tooltipQueue.push(content);
	}

	@Override
	public boolean isScreenOpen() {
		return !this.layerPass && this.isScreenDisplayed();
	}

	@Override
	public boolean isOverlayHidden() {
		return this.isHudHidden();
	}

	public final void drawHud(final G graphics) {
		if (!this.isOverlayHidden()) {
			this.drawOverlays(graphics, ui -> this.getLayer(ui) == null && !this.isAboveScreen(ui));
		}
	}

	public final void drawLayer(final G graphics, final L layer, final boolean post) {
		if (this.isOverlayHidden()) {
			return;
		}

		this.layerPass = true;
		try {
			this.drawOverlays(graphics, ui -> this.isLayer(ui, layer) && this.isPostLayer(ui) == post);
		} finally {
			this.layerPass = false;
		}
	}

	public final void drawAboveScreen(final G graphics) {
		if (this.isScreenOpen()) {
			this.drawOverlays(graphics, ui -> this.getLayer(ui) == null && ui.getOverlay().render().aboveScreens());
			this.requestCursor(graphics);
		}
	}

	public final void drawHiddenHud(final G graphics) {
		if (this.isOverlayHidden()) {
			this.drawOverlays(graphics, ui -> !this.isAboveScreen(ui));
		}
	}

	public final boolean isCancelled(final L layer) {
		for (final UI ui : super.getUiList()) {
			if (this.isLayer(ui, layer) && this.isCancellingLayer(ui) && this.isShown(ui)) {
				return true;
			}
		}
		return false;
	}

	protected abstract long countFrame(final G graphics);
	protected abstract void requestCursor(final G graphics);
	protected abstract void compositeFrame(final G graphics, final Runnable draw);
	protected abstract void drawTooltip(final G graphics, final Object content, final int mouseX, final int mouseY);

	protected abstract int getGuiMouseX();
	protected abstract int getGuiMouseY();

	protected abstract boolean isHudHidden();
	protected abstract boolean isScreenDisplayed();

	protected abstract L getLayer(final UI ui);
	protected abstract boolean isPostLayer(final UI ui);
	protected abstract boolean isCancellingLayer(final UI ui);

	private void drawOverlays(final G graphics, final Predicate<UI> filter) {
		this.prepareFrame(graphics);
		if (super.getUiList().stream().noneMatch(ui -> filter.test(ui) && this.isShown(ui))) {
			return;
		}

		this.compositeFrame(graphics, () -> super.draw(filter));
		this.tooltipQueue.flush(content -> this.drawTooltip(graphics, content, this.getGuiMouseX(), this.getGuiMouseY()));
	}

	private void prepareFrame(final G graphics) {
		final long frame = this.countFrame(graphics);
		if (frame == this.frame || super.getUiList().isEmpty()) {
			return;
		}

		this.frame = frame;
		if (this.window.getWidth() != this.width || this.window.getHeight() != this.height) {
			this.width  = this.window.getWidth();
			this.height = this.window.getHeight();
			super.load();
		}

		super.update();
	}

	private boolean isShown(final UI ui) {
		final UIDataOverlayRenderObject render = ui.getOverlay().render();
		return ui.getData().visible() && (render.always() || !this.isOverlayHidden());
	}

	private boolean isAboveScreen(final UI ui) {
		return this.isScreenOpen() && ui.getOverlay().render().aboveScreens();
	}

	private boolean isLayer(final UI ui, final L layer) {
		final L uiLayer = this.getLayer(ui);
		return uiLayer != null && uiLayer == layer;
	}

}