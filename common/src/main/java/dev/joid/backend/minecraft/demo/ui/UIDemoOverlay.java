package dev.joid.backend.minecraft.demo.ui;

import java.util.ArrayList;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.internal.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.core.data.overlay.interaction.UIDataOverlayInteraction;
import dev.joid.lib.ui.core.data.overlay.render.UIDataOverlayRender;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;
import dev.joid.lib.utils.cursor.Cursor;

import net.minecraft.client.Minecraft;

@UIData(background = false)
@UIDataOverlay(active = true, render = @UIDataOverlayRender(screens = true))
public class UIDemoOverlay extends UI {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PANEL       = new Color(48, 48, 48);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	@Override
	public void init() {
		final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlay.INK);

		for (final UI ui : new ArrayList<>(super.getBridge().getUiList().ordered())) {
			if (ui != this && ui instanceof UIDemoOverlay) {
				JOID.close(ui, true);
			}
		}

		if (JOID.isOpen(Hotbar.class)) {
			JOID.close(JOID.getUI(Hotbar.class));
			JOID.close(JOID.getUI(Experience.class));
			JOID.close(JOID.getUI(Interactive.class));
		} else {
			JOID.open(new Hotbar());
			JOID.open(new Experience());
			JOID.open(new Interactive());
		}

		RectNode
		.create(0, 0, 80, 40)
		.color(UIDemoOverlay.PLACEHOLDER)
		.x(() -> UIDemoOverlay.getEntry() != null ? UIDemoOverlay.getEntry().getAbsoluteX() + UIDemoOverlay.getEntry().getWidth() - 95D : 0D)
		.y(() -> UIDemoOverlay.getEntry() != null ? UIDemoOverlay.getEntry().getAbsoluteY() + 15D : 0D)
		.visible(() -> UIDemoOverlay.getEntry() != null)
		.body(rect -> {
			TextNode.create(40, 20).text(Text.create(() -> JOID.isOpen(Hotbar.class) ? "On" : "Off", info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(rect);
		})
		.attach(this);
	}

	private static Node getEntry() {
		final UIDemoChoice choice = JOID.getUI(UIDemoChoice.class);
		if (choice == null || choice.getNodeList().isEmpty()) {
			return null;
		}
		return choice.getNodeList().ordered().getFirst().getChild(new ArrayList<>(UIDemoChoice.LIST).indexOf(UIDemoOverlay.class), RectNode.class);
	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.HOTBAR, post = true)
	public static class Hotbar extends UI {

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlay.INK);

			RectNode
			.create(596, 840, 728, 60)
			.color(UIDemoOverlay.PLACEHOLDER)
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
			.color(UIDemoOverlay.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(0, 0, 0, 20).color(UIDemoOverlay.INK).width(() -> Minecraft.getInstance().player != null ? 728D * Minecraft.getInstance().player.experienceProgress : 0D).attach(rect);
			})
			.attach(this);
		}

	}

	@UIData(background = false)
	@UIDataOverlay(active = true, interaction = @UIDataOverlayInteraction(active = true), render = @UIDataOverlayRender(screens = true))
	public static class Interactive extends UI {

		private int clicks;

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlay.INK);
			final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoOverlay.PLACEHOLDER);

			RectNode
			.create(80, 420, 320, 240)
			.color(UIDemoOverlay.PLACEHOLDER)
			.body(rect -> {
				RectNode
				.create(70, 60, 180, 80)
				.color(UIDemoOverlay.INK)
				.hoveredColor(UIDemoOverlay.PANEL)
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

}