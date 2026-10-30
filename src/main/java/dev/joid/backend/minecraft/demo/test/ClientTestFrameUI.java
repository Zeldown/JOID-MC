package dev.joid.backend.minecraft.demo.test;

import java.util.concurrent.atomic.AtomicLong;

import dev.joid.lib.color.Color;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIDataOverlay;
import dev.joid.lib.ui.core.data.UIDataOverlayRender;
import dev.joid.lib.ui.node.impl.shape.RectNode;

@UIDataOverlay(active = true, render = @UIDataOverlayRender(always = true, aboveScreens = true))
public final class ClientTestFrameUI extends UI {

	private static final AtomicLong FRAMES = new AtomicLong();

	public static long getFrames() {
		return ClientTestFrameUI.FRAMES.get();
	}

	@Override
	public void init() {
		RectNode.create(0, 0, 1, 1).color(Color.TRANSPARENT).interactive(false).onDraw(event -> ClientTestFrameUI.FRAMES.incrementAndGet()).attach(this);
	}

}