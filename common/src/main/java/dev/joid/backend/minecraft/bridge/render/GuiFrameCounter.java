package dev.joid.backend.minecraft.bridge.render;

import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.gui.GuiGraphics;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiFrameCounter {

	private static final GuiFrameCounter INSTANCE = new GuiFrameCounter();

	private long        frame;
	private GuiGraphics graphics;

	public static @NonNull GuiFrameCounter inst() {
		return GuiFrameCounter.INSTANCE;
	}

	public long getFrame(final GuiGraphics graphics) {
		if (graphics != this.graphics) {
			this.graphics = graphics;
			this.frame++;
			BridgeHandler.WINDOW.getBridge(MinecraftWindowBridge.class).applyCursor();
			BridgeHandler.UI.fireFrame();
		}
		return this.frame;
	}

}