package dev.joid.backend.minecraft.ui.screen;

import dev.joid.impl.glfw.WindowBridge;
import dev.joid.impl.glfw.input.KeyCharacterMerger;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.utils.click.ClickType;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScreenInputForwarder {

	private final UIBridge           bridge;
	private final KeyCharacterMerger merger;

	private long      pressTime;
	private ClickType clickType;

	public static @NonNull ScreenInputForwarder create(final @NonNull UIBridge bridge) {
		return new ScreenInputForwarder(bridge, KeyCharacterMerger.create(bridge::keyTyped));
	}

	public void mousePressed(final @NonNull MouseButtonEvent event) {
		this.merger.flush();
		this.clickType = ClickType.from(event.button());
		this.pressTime = System.currentTimeMillis();
		this.bridge.mousePressed(this.clickType);
	}

	public void mouseReleased() {
		if (this.clickType == null) {
			return;
		}

		this.bridge.mouseReleased(this.clickType);
		this.clickType = null;
	}

	public void mouseDragged() {
		if (this.clickType != null) {
			this.bridge.mouseDragged(this.clickType, System.currentTimeMillis() - this.pressTime);
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