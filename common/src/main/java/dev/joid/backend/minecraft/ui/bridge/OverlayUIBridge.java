package dev.joid.backend.minecraft.ui.bridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.composite.GuiCompositor;
import dev.joid.backend.minecraft.window.WindowBridge;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.resource.dto.ResourceData;
import dev.joid.lib.shader.pipeline.ShaderPipeline;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlayObject;
import dev.joid.lib.ui.core.data.overlay.render.UIDataOverlayRenderObject;
import dev.joid.lib.ui.node.Node;
import lombok.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class OverlayUIBridge extends UIBridge {

	private final WindowBridge  window;
	private final GuiCompositor compositor;

	private int          width;
	private int          height;
	private long         frameTime;
	private List<String> hoverList;

	private OverlayUIBridge(final RenderBridge render, final WindowBridge window) {
		this.window     = window;
		this.compositor = GuiCompositor.create(render);
		this.width      = window.getWidth();
		this.height     = window.getHeight();
		this.frameTime  = -1L;
	}

	public static @NonNull OverlayUIBridge create(final @NonNull RenderBridge render, final @NonNull WindowBridge window) {
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
	public void drawHover(final @NonNull UI ui, final @NonNull List<@NonNull String> lines, final double mouseX, final double mouseY) {
		this.hoverList = new ArrayList<>(lines);
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
			this.requestCursor(graphics);
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
		this.frame();
		final List<UI> drawList = new ArrayList<>();
		for (final UI ui : this.getDrawList()) {
			if (filter.test(ui) && this.isShown(ui)) {
				drawList.add(ui);
			}
		}

		if (drawList.isEmpty()) {
			return;
		}

		this.compositor.composite(graphics, () -> this.draw(drawList));
		final List<String> hoverList = this.hoverList;
		this.hoverList = null;
		if (hoverList != null && !hoverList.isEmpty()) {
			final Minecraft minecraft = Minecraft.getInstance();
			graphics.setComponentTooltipForNextFrame(minecraft.font, hoverList.stream().<Component>map(Component::literal).toList(), (int) minecraft.mouseHandler.getScaledXPos(minecraft.getWindow()), (int) minecraft.mouseHandler.getScaledYPos(minecraft.getWindow()));
		}
	}

	private void frame() {
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
		ResourceData.releaseCollected();
		ShaderPipeline.releaseUnused();
	}

	private void draw(final List<UI> drawList) {
		final IRenderBridge render = BridgeHandler.RENDER.get();
		final boolean grabbed = this.window.isMouseGrabbed();
		final double mouseX = grabbed ? -1D : this.window.getMouseX();
		final double mouseY = grabbed ? -1D : this.window.getMouseY();

		double renderPipeline = 0D;
		render.pushMatrix();
		try {
			render.translate(0D, 0D, -2000D);
			for (final UI ui : drawList) {
				renderPipeline += ui.getData().zlevel();
				render.translate(0D, 0D, renderPipeline);
				ui.draw(mouseX, mouseY);
				renderPipeline = ui.getRenderPipelineLevel() + 10D;
			}
		} catch (final Exception exception) {
			exception.printStackTrace();
		} finally {
			render.popMatrix();
		}
	}

	private void requestCursor(final GuiGraphicsExtractor graphics) {
		final List<UI> drawList = this.getDrawList();
		for (int i = drawList.size() - 1; i >= 0; i--) {
			final UI ui = drawList.get(i);
			if (!ui.getData().active() || !ui.getOverlay().interaction().active() || !this.isShown(ui)) {
				continue;
			}

			final Node hovered = ui.getHoveredNode();
			if (hovered != null) {
				this.window.requestCursor(graphics, hovered.getResolvedCursor());
				return;
			}
		}
	}

	private boolean isShown(final UI ui) {
		final UIDataOverlayRenderObject render = ui.getOverlay().render();
		return ui.getData().visible() && (render.always() || !this.isOverlayHidden()) && (render.screens() || !this.isScreenOpen());
	}

	private List<UI> getDrawList() {
		super.getUiList().sort();
		final List<UI> drawList = new ArrayList<>(super.getUiList().ordered());
		drawList.sort(Comparator.comparingInt(ui -> ui.getOverlay().render().zindex()));
		return drawList;
	}

	private static boolean isLayer(final UI ui, final OverlayLayer layer) {
		final UIDataOverlayLayer data = OverlayUIBridge.getLayer(ui);
		return data != null && data.layer() == layer;
	}

	private static UIDataOverlayLayer getLayer(final UI ui) {
		return ui.getClass().getAnnotation(UIDataOverlayLayer.class);
	}

}