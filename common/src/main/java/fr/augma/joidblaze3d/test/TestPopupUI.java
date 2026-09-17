package fr.augma.joidblaze3d.test;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.core.data.popup.UIDataPopup;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.utils.align.Align;

@UIDataPopup(active = true, transition = UIDataPopup.PopupTransition.IN_OUT)
public final class TestPopupUI extends UI {

	@Override
	public void init() {
		RectNode
		.create(960, 540, 460, 240)
		.color(new Color(28, 28, 38, 245))
		.border(Color.ORANGE, 4D)
		.anchorX(Align.CENTER)
		.anchorY(Align.CENTER)
		.body(popup -> {
			RectNode
			.create(popup.dw(2D) - 340D / 2D, 50, 340, 60)
			.color(Color.ORANGE.copyAlpha(0.35F))
			.attach(popup);
			RectNode
			.create(popup.dw(2D) - 200D / 2D, 150, 200, 50)
			.color(Color.ORANGE)
			.hoveredColor(Color.RED)
			.onClick((node, x, y, click) -> JOID.close(this))
			.attach(popup);
		})
		.attach(this);
	}

}