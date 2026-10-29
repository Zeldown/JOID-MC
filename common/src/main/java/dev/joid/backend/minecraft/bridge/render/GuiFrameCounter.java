package dev.joid.backend.minecraft.bridge.render;

import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiFrameCounter {

	private static final GuiFrameCounter INSTANCE = new GuiFrameCounter();

	private long frame;
	private long counted;

	public static @NonNull GuiFrameCounter inst() {
		return GuiFrameCounter.INSTANCE;
	}

	public void nextFrame() {
		this.frame++;
	}

	public long getFrame() {
		if (this.counted != this.frame) {
			this.counted = this.frame;
			BridgeHandler.UI.fireFrame();
		}
		return this.frame;
	}

}