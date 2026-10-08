package dev.joid.impl.minecraft.input;

import java.util.EnumMap;
import java.util.Map;

import dev.joid.lib.utils.click.ClickType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MouseButtonTracker {

	private final Map<ClickType, Long> pressTimeMap;

	public static @NonNull MouseButtonTracker create() {
		return new MouseButtonTracker(new EnumMap<>(ClickType.class));
	}

	public void press(final @NonNull ClickType clickType, final long time) {
		this.pressTimeMap.put(clickType, time);
	}

	public void release(final @NonNull ClickType clickType) {
		this.pressTimeMap.remove(clickType);
	}

	public boolean isPressed(final @NonNull ClickType clickType) {
		return this.pressTimeMap.containsKey(clickType);
	}

	public long getDragTime(final @NonNull ClickType clickType, final long time) {
		final Long pressTime = this.pressTimeMap.get(clickType);
		if (pressTime == null) {
			throw new IllegalStateException("The " + clickType + " button is not pressed");
		}
		return time - pressTime;
	}

}