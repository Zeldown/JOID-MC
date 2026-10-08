package dev.joid.impl.minecraft.ui.screen;

import dev.joid.impl.glfw.WindowBridge;
import dev.joid.impl.minecraft.input.KeyCharacterMerger;
import dev.joid.impl.minecraft.input.MouseButtonTracker;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.utils.click.ClickType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScreenInputForwarder {

	private final UIBridge           bridge;
	private final KeyCharacterMerger merger;
	private final MouseButtonTracker tracker;

	public static @NonNull ScreenInputForwarder create(final @NonNull UIBridge bridge) {
		return new ScreenInputForwarder(bridge, KeyCharacterMerger.create(bridge::keyTyped), MouseButtonTracker.create());
	}

	public void mousePressed(final @NonNull MouseButtonEvent event) {
		final ClickType clickType = ClickType.from(event.button());
		this.merger.flush();
		this.tracker.press(clickType, BridgeHandler.CLOCK.get().currentTimeMillis());
		this.bridge.mousePressed(clickType);
	}

	public void mouseReleased(final @NonNull MouseButtonEvent event) {
		final ClickType clickType = ClickType.from(event.button());
		if (!this.tracker.isPressed(clickType)) {
			return;
		}

		this.tracker.release(clickType);
		this.bridge.mouseReleased(clickType);
	}

	public void mouseDragged(final @NonNull MouseButtonEvent event) {
		final ClickType clickType = ClickType.from(event.button());
		if (this.tracker.isPressed(clickType)) {
			this.bridge.mouseDragged(clickType, this.tracker.getDragTime(clickType, BridgeHandler.CLOCK.get().currentTimeMillis()));
		}
	}

	public void mouseScrolled(final double amount) {
		this.bridge.mouseScroll((int) (amount * 120D));
	}

	public void keyPressed(final @NonNull KeyEvent event) {
		this.merger.keyPressed(WindowBridge.getKey(event.key()), event.key(), event.modifiers());
	}

	public void charTyped(final @NonNull CharacterEvent event) {
		this.merger.charTyped(event.codepoint());
	}

	public void flush() {
		this.merger.flush();
	}

}