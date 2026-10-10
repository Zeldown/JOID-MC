package dev.joid.backend.minecraft.demo.ui;

import dev.joid.backend.minecraft.demo.container.DemoContainer;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.demo.DemoFont;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

@UIData(background = false)
public class UIDemoContainer extends ContainerUI<DemoContainer> {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	public UIDemoContainer(final DemoContainer container) {
		super(container);
	}

	@Override
	public void init() {
		final TextInfo title = TextInfo.create(DemoFont.MONTSERRAT, 32, UIDemoContainer.INK);
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoContainer.INK);

		RectNode
		.create(588, 168, 744, 744)
		.color(UIDemoContainer.PLACEHOLDER)
		.self(super::bounds)
		.body(rect -> {
			TextNode.create(48, 48).text(Text.create("Demo storage", title)).anchorY(Align.CENTER).attach(rect);
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
	}

}