package dev.joid.backend.minecraft.demo.ui;

import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.demo.DemoFont;
import dev.joid.internal.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.popup.UIDataPopup;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

@UIData(background = false)
public class UIDemoContainer extends ContainerUI<DemoContainer> {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PANEL       = new Color(48, 48, 48);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	public UIDemoContainer(final DemoContainer container) {
		super(container);
	}

	@Override
	public void init() {
		final TextInfo title = TextInfo.create(DemoFont.MONTSERRAT, 32, UIDemoContainer.INK);
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoContainer.INK);
		final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoContainer.PLACEHOLDER);

		RectNode
		.create(588, 168, 744, 744)
		.color(UIDemoContainer.PLACEHOLDER)
		.self(super::bounds)
		.body(rect -> {
			TextNode.create(48, 48).text(Text.create("Demo storage", title)).anchorY(Align.CENTER).attach(rect);
			RectNode
			.create(552, 24, 144, 48)
			.color(UIDemoContainer.INK)
			.hoveredColor(UIDemoContainer.PANEL)
			.cursor(Cursor.POINTER)
			.body(button -> {
				TextNode.create(72, 24).text(Text.create("Popup", info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
			})
			.onClick((_, _, _, _) -> JOID.open(new Popup()))
			.attach(rect);
			for (int index = 0; index < 27; index++) {
				SlotNode.create(48 + index % 9 * 72, 96 + index / 9 * 72, 68, 68).slot(super.getContainer().getSlot(index)).color(UIDemoContainer.INK).attach(rect);
			}
			TextNode.create(48, 348).text(Text.create("Inventory", caption)).anchorY(Align.CENTER).attach(rect);
			for (int index = 0; index < 27; index++) {
				SlotNode.create(48 + index % 9 * 72, 384 + index / 9 * 72, 68, 68).slot(super.getContainer().getSlot(27 + index)).color(UIDemoContainer.INK).attach(rect);
			}
			for (int index = 0; index < 9; index++) {
				SlotNode.create(48 + index * 72, 624, 68, 68).slot(super.getContainer().getSlot(54 + index)).color(UIDemoContainer.INK).attach(rect);
			}
		})
		.attach(this);

		TextNode.create(960, 944).text(Text.create("Click outside drops", caption, Align.CENTER)).anchorX(Align.CENTER).attach(this);
	}

	@UIData(background = false)
	@UIDataPopup(active = true)
	public static class Popup extends UI {

		@Override
		public void init() {
			final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoContainer.INK);
			final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoContainer.PLACEHOLDER);

			RectNode
			.create(760, 400, 400, 280)
			.color(UIDemoContainer.PLACEHOLDER)
			.body(rect -> {
				RectNode
				.create(110, 80, 180, 80)
				.color(UIDemoContainer.INK)
				.hoveredColor(UIDemoContainer.PANEL)
				.cursor(Cursor.POINTER)
				.body(button -> {
					TextNode.create(90, 40).text(Text.create("Close", info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
				})
				.onClick((_, _, _, _) -> JOID.close(this))
				.attach(rect);
				TextNode.create(200, 240).text(Text.create("Popup above", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(this);
		}

	}

}