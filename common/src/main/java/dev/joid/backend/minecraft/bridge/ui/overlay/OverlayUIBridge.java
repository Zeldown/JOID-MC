package dev.joid.backend.minecraft.bridge.ui.overlay;

import java.util.function.Predicate;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.ui.TooltipQueue;
import dev.joid.backend.minecraft.bridge.ui.screen.GuiCompositor;
import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlayObject;
import dev.joid.lib.ui.core.data.overlay.render.UIDataOverlayRenderObject;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class OverlayUIBridge extends UIBridge {

	private final GuiCompositor         compositor;
	private final TooltipQueue          tooltipQueue;
	private final MinecraftWindowBridge window;

	private int  width;
	private int  height;
	private long frameTime;

	private OverlayUIBridge(final MinecraftRenderBridge render, final MinecraftWindowBridge window) {
		this.window       = window;
		this.compositor   = GuiCompositor.create(render);
		this.tooltipQueue = TooltipQueue.create();
		this.width        = window.getWidth();
		this.height       = window.getHeight();
		this.frameTime    = -1L;
	}

	public static @NonNull OverlayUIBridge create(final @NonNull MinecraftRenderBridge render, final @NonNull MinecraftWindowBridge window) {
		return new OverlayUIBridge(render, window);
	}

	@Override
	public void open(final @NonNull UI ui) {
		this.add(ui);
	}

	@Override
	public void close(final @NonNull UI ui) {
		this.remove(ui);
	}

	@Override
	public void add(final @NonNull UI ui) {
		super.getUiList().add(ui);
		ui.load(this.window.getWidth(), this.window.getHeight());
	}

	@Override
	public void remove(final @NonNull UI ui) {
		super.getUiList().remove(ui);
	}

	@Override
	public boolean isScreenOpen() {
		return Minecraft.getInstance().gui.screen() != null;
	}

	@Override
	public boolean isOverlayHidden() {
		return Minecraft.getInstance().gui.hud.isHidden();
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return ui.getOverlay().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return UIDataOverlayObject.getOrDefault(clazz).active();
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		return this.window.getInterfaceScale();
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull Object content, final double mouseX, final double mouseY) {
		this.tooltipQueue.push(content);
	}

	public void extract(final @NonNull GuiGraphicsExtractor graphics) {
		if (!this.isScreenOpen()) {
			this.extract(graphics, ui -> OverlayUIBridge.getLayer(ui) == null);
		}
	}

	public void extract(final @NonNull GuiGraphicsExtractor graphics, final @NonNull OverlayLayer layer, final boolean post) {
		if (!this.isScreenOpen()) {
			this.extract(graphics, ui -> OverlayUIBridge.isLayer(ui, layer) && OverlayUIBridge.getLayer(ui).post() == post);
		}
	}

	public void extractScreen(final @NonNull GuiGraphicsExtractor graphics) {
		if (this.isScreenOpen()) {
			this.extract(graphics, _ -> true);
			this.window.requestCursor(graphics, this.window.getCursor());
		}
	}

	public boolean isCancelled(final @NonNull OverlayLayer layer) {
		for (final UI ui : super.getUiList()) {
			if (OverlayUIBridge.isLayer(ui, layer) && OverlayUIBridge.getLayer(ui).cancel() && this.isShown(ui)) {
				return true;
			}
		}
		return false;
	}

	private void extract(final GuiGraphicsExtractor graphics, final Predicate<UI> filter) {
		this.prepareFrame();
		if (super.getUiList().ordered().stream().noneMatch(ui -> filter.test(ui) && this.isShown(ui))) {
			return;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		this.compositor.composite(graphics, () -> super.draw(filter));
		this.tooltipQueue.flush(graphics, (int) minecraft.mouseHandler.getScaledXPos(minecraft.getWindow()), (int) minecraft.mouseHandler.getScaledYPos(minecraft.getWindow()));
	}

	private void prepareFrame() {
		final long frameTime = Minecraft.getInstance().getFrameTimeNs();
		if (frameTime == this.frameTime || super.getUiList().isEmpty()) {
			return;
		}

		this.frameTime = frameTime;
		if (this.window.getWidth() != this.width || this.window.getHeight() != this.height) {
			this.width  = this.window.getWidth();
			this.height = this.window.getHeight();
			super.load();
		}

		super.update();
	}

	private boolean isShown(final UI ui) {
		final UIDataOverlayRenderObject render = ui.getOverlay().render();
		return ui.getData().visible() && (render.always() || !this.isOverlayHidden()) && (render.screens() || !this.isScreenOpen());
	}

	private static boolean isLayer(final UI ui, final OverlayLayer layer) {
		final UIDataOverlayLayer data = OverlayUIBridge.getLayer(ui);
		return data != null && data.layer() == layer;
	}

	private static UIDataOverlayLayer getLayer(final UI ui) {
		return ui.getClass().getAnnotation(UIDataOverlayLayer.class);
	}

}