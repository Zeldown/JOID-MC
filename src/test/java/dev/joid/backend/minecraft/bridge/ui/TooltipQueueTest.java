package dev.joid.backend.minecraft.bridge.ui;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TooltipQueueTest {

	@Test
	public void drawsTheLastTooltipOfTheFrameOnce() {
		final TooltipQueue queue = new TooltipQueue();
		final List<Object> drawn = new ArrayList<>();
		queue.push("first");
		queue.push("second");
		queue.flush(drawn::add);
		queue.flush(drawn::add);
		Assert.assertEquals(1, drawn.size());
		Assert.assertEquals("second", drawn.get(0));
	}

}