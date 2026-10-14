package dev.joid.backend.minecraft.bridge.render;

import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiFrameCounter {

	private static final GuiFrameCounter INSTANCE = new GuiFrameCounter();

	private long                 frame;
	private GuiGraphicsExtractor graphics;

	public static @NonNull GuiFrameCounter inst() {
		return GuiFrameCounter.INSTANCE;
	}

	public long getFrame(final @NonNull GuiGraphicsExtractor graphics) {
		if (graphics != this.graphics) {
			this.graphics = graphics;
			this.frame++;
			BridgeHandler.UI.fireFrame();
		}
		return this.frame;
	}

}