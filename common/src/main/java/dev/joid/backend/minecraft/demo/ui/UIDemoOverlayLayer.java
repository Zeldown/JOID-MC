package dev.joid.backend.minecraft.demo.ui;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.demo.DemoFont;
import dev.joid.internal.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.core.data.overlay.interaction.UIDataOverlayInteraction;
import dev.joid.lib.ui.core.data.overlay.render.UIDataOverlayRender;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UIDemoOverlayLayer {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PANEL       = new Color(48, 48, 48);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	public static void toggle() {
		if (UIDemoOverlayLayer.isOpen()) {
			JOID.close(JOID.getUi(Hotbar.class));
			JOID.close(JOID.getUi(Experience.class));
			JOID.close(JOID.getUi(Crosshair.class));
			JOID.close(JOID.getUi(Interactive.class));
			JOID.close(JOID.getUi(Always.class));
		} else {
			JOID.open(new Hotbar());
			JOID.open(new Experience());
			JOID.open(new Crosshair());
			JOID.open(new Interactive());
			JOID.open(new Always());
		}
	}

	public static boolean isOpen() {
		return JOID.isOpen(Hotbar.class);
	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.HOTBAR, post = true)
	public static class Hotbar extends UI {

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlayLayer.INK);

			RectNode
			.create(596, 840, 728, 60)
			.color(UIDemoOverlayLayer.PLACEHOLDER)
			.body(rect -> {
				TextNode.create(364, 30).text(Text.create("Above the hotbar", caption, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(rect);
			})
			.attach(this);
		}

	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.CONTEXTUAL_BAR, cancel = true)
	public static class Experience extends UI {

		@Override
		public void init() {
			RectNode
			.create(596, 964, 728, 20)
			.color(UIDemoOverlayLayer.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(0, 0, 0, 20).color(UIDemoOverlayLayer.INK).width(() -> Minecraft.getInstance().player != null ? 728D * Minecraft.getInstance().player.experienceProgress : 0D).attach(rect);
			})
			.attach(this);
		}

	}

	@UIData(background = false)
	@UIDataOverlay(active = true)
	@UIDataOverlayLayer(layer = OverlayLayer.CROSSHAIR)
	public static class Crosshair extends UI {

		@Override
		public void init() {
			RectNode.create(928, 508, 64, 64).color(UIDemoOverlayLayer.PLACEHOLDER).attach(this);
		}

	}

	@UIData(background = false)
	@UIDataOverlay(active = true, interaction = @UIDataOverlayInteraction(active = true), render = @UIDataOverlayRender(screens = true))
	public static class Interactive extends UI {

		private int clicks;

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlayLayer.INK);
			final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlayLayer.PLACEHOLDER);

			RectNode
			.create(80, 420, 320, 240)
			.color(UIDemoOverlayLayer.PLACEHOLDER)
			.body(rect -> {
				RectNode
				.create(70, 60, 180, 80)
				.color(UIDemoOverlayLayer.INK)
				.hoveredColor(UIDemoOverlayLayer.PANEL)
				.cursor(Cursor.POINTER)
				.body(button -> {
					TextNode.create(90, 40).text(Text.create(() -> "Clicks " + this.clicks, info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
				})
				.onClick((_, _, _, _) -> this.clicks++)
				.attach(rect);
				TextNode.create(160, 200).text(Text.create("Interactive", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(this);
		}

	}

	@UIData(background = false)
	@UIDataOverlay(active = true, render = @UIDataOverlayRender(always = true))
	public static class Always extends UI {

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlayLayer.INK);

			RectNode
			.create(1520, 420, 320, 240)
			.color(UIDemoOverlayLayer.PLACEHOLDER)
			.body(rect -> {
				TextNode.create(160, 100).text(Text.create("F1", caption, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(rect);
				TextNode.create(160, 200).text(Text.create("Always", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(this);
		}

	}

}