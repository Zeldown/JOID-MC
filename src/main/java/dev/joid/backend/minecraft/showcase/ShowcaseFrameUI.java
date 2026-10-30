package dev.joid.backend.minecraft.showcase;

import dev.joid.lib.color.Color;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.core.data.UIDataOverlayRender;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import lombok.Getter;

@UIDataOverlay(active = true, render = @UIDataOverlayRender(always = true, aboveScreens = true))
public final class ShowcaseFrameUI extends UI {

	@Getter
	private static volatile long frame;

	@Override
	public void init() {
		RectNode.create(0, 0, 1, 1).color(Color.TRANSPARENT).interactive(false).onDraw(event -> ShowcaseFrameUI.frame = ShowcaseFrameUI.frame + 1).attach(this);
	}

}