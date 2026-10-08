package dev.joid.impl.minecraft.input;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.utils.click.ClickType;

public class MouseButtonTrackerTest {

	@Test
	public void measuresTheDragTimeFromThePress() {
		final MouseButtonTracker tracker = MouseButtonTracker.create();
		tracker.press(ClickType.LEFT, 1000L);
		Assert.assertTrue(tracker.isPressed(ClickType.LEFT));
		Assert.assertEquals(250L, tracker.getDragTime(ClickType.LEFT, 1250L));
	}

	@Test
	public void tracksEveryButtonOnItsOwn() {
		final MouseButtonTracker tracker = MouseButtonTracker.create();
		tracker.press(ClickType.LEFT, 1000L);
		tracker.press(ClickType.RIGHT, 1100L);
		tracker.release(ClickType.LEFT);
		Assert.assertFalse(tracker.isPressed(ClickType.LEFT));
		Assert.assertEquals(100L, tracker.getDragTime(ClickType.RIGHT, 1200L));
	}

	@Test
	public void restartsTheDragTimeOnANewPress() {
		final MouseButtonTracker tracker = MouseButtonTracker.create();
		tracker.press(ClickType.MIDDLE, 1000L);
		tracker.release(ClickType.MIDDLE);
		tracker.press(ClickType.MIDDLE, 2000L);
		Assert.assertEquals(10L, tracker.getDragTime(ClickType.MIDDLE, 2010L));
	}

	@Test
	public void namesAButtonThatIsNotPressed() {
		try {
			MouseButtonTracker.create().getDragTime(ClickType.LEFT, 0L);
			Assert.fail("A button that is not pressed has no drag time");
		} catch (final IllegalStateException expected) {
			Assert.assertEquals("The LEFT button is not pressed", expected.getMessage());
		}
	}

}