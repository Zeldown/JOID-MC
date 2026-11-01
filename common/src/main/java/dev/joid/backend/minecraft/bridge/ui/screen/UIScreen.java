package dev.joid.backend.minecraft.bridge.ui.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.base.lwjgl2.input.Lwjgl2InputForwarder;
import net.minecraft.client.gui.GuiScreen;

public class UIScreen extends GuiScreen {

	private final ScreenUIBridge bridge;

	public UIScreen(final ScreenUIBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void initGui() {
		this.bridge.load();
	}

	@Override
	public void onGuiClosed() {
		this.bridge.removed(this);
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		if (this.hasBackground()) {
			super.drawDefaultBackground();
		}
		this.bridge.frame(null, mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.pause();
	}

	@Override
	public void handleMouseInput() throws IOException {
		if (!Lwjgl2InputForwarder.forwardMouseEvent()) {
			super.handleMouseInput();
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if (!Lwjgl2InputForwarder.forwardKeyboardEvent()) {
			super.handleKeyboardInput();
		}
	}

	@Override
	protected void keyTyped(final char character, final int code) throws IOException {
		if (code != Keyboard.KEY_ESCAPE) {
			super.keyTyped(character, code);
		}
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.background();
	}

}