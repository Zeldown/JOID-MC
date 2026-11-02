package dev.joid.backend.minecraft.bridge.render;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.bridge.window.MinecraftWindowBridge;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiFrameCounter {

	private static final GuiFrameCounter INSTANCE = new GuiFrameCounter();

	private long      frame;
	private PoseStack pose;

	public static @NonNull GuiFrameCounter inst() {
		return GuiFrameCounter.INSTANCE;
	}

	public long getFrame(final PoseStack pose) {
		if (pose != this.pose) {
			this.pose = pose;
			this.frame++;
			BridgeHandler.WINDOW.getBridge(MinecraftWindowBridge.class).applyCursor();
			BridgeHandler.UI.fireFrame();
		}
		return this.frame;
	}

}