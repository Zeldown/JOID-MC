package dev.joid.backend.minecraft.showcase;

import dev.joid.lib.color.Color;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.core.data.overlay.render.UIDataOverlayRender;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import lombok.Getter;

@UIData(background = false)
@UIDataOverlay(active = true, render = @UIDataOverlayRender(always = true, screens = true))
public final class ShowcaseFrameUI extends UI {

	@Getter
	private static volatile long frame;

	@Override
	public void init() {
		RectNode.create(0, 0, 1, 1).color(Color.TRANSPARENT).interactive(false).onDraw((_, _, _) -> ShowcaseFrameUI.frame = ShowcaseFrameUI.frame + 1).attach(this);
	}

}