package fr.augma.joidblaze3d.test;

import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.utils.align.Align;
import fr.augma.joidblaze3d.screen.data.UIMCData;

@UIMCData
public final class TestMenuUI extends UI {

	@Override
	public void init() {
		RectNode.create(960, 540, 704, 664)
		.color(new Color(18, 18, 24, 200))
		.border(Color.CYAN, 4D)
		.anchorX(Align.CENTER)
		.anchorY(Align.CENTER)
		.body(panel -> {
			RectNode.create(352, 40, 640, 40).color(Color.CYAN.copyAlpha(0.35F)).anchorX(Align.CENTER).attach(panel);
			RectNode.create(352, 624, 640, 8).color(Color.CYAN).anchorX(Align.CENTER).attach(panel);
		})
		.attach(this);
	}

}