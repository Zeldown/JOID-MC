package fr.augma.joidblaze3d.test;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.ui.node.impl.structure.flex.FlexNode;
import be.zeldown.joid.lib.utils.align.Align;
import fr.augma.joidblaze3d.screen.data.UIMCData;

@UIMCData
public final class TestUI extends UI {

	@Override
	public void init() {
		RectNode
		.create(960, 540, 900, 500)
		.color(new Color(18, 18, 24, 220))
		.border(Color.CYAN, 4D)
		.anchorX(Align.CENTER)
		.anchorY(Align.CENTER)
		.body(panel -> {
			FlexNode
			.horizontal(450, 140, 120)
			.margin(20D)
			.align(Align.CENTER)
			.body(flex -> {
				RectNode.create(0, 0, 120, 120).color(Color.RED).attach(flex);
				RectNode.create(0, 0, 120, 120).color(Color.GREEN).attach(flex);
				RectNode.create(0, 0, 120, 120).color(Color.BLUE).attach(flex);
			})
			.anchorX(Align.CENTER)
			.attach(panel);
			FlexNode
			.horizontal(450, 300, 20)
			.margin(10D)
			.align(Align.CENTER)
			.body(flex -> {
				for (int i = 0; i < 12; i++) {
					RectNode.create(0, 0, 20, 20).color(Color.WHITE).attach(flex);
				}
			})
			.anchorX(Align.CENTER)
			.attach(panel);
			RectNode
			.create(panel.dw(2D) - 700D / 2D, 380, 700, 40)
			.color(Color.DARKGRAY)
			.hoveredColor(Color.GRAY)
			.border(Color.WHITE, 2D)
			.onClick((node, x, y, click) -> JOID.open(new TestPopupUI()))
			.attach(panel);
		})
		.attach(this);
	}

}