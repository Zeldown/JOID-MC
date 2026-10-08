package dev.joid.backend.minecraft.window;

import dev.joid.base.glfw.GlfwWindows;
import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.utils.key.Key;
import lombok.NonNull;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

public final class WindowBridge implements IWindowBridge {

	@Override
	public int getWidth() {
		return Minecraft.getInstance().getWindow().getWidth();
	}

	@Override
	public int getHeight() {
		return Minecraft.getInstance().getWindow().getHeight();
	}

	@Override
	public double getMouseX() {
		final Window window = Minecraft.getInstance().getWindow();
		return GlfwWindows.toFramebuffer(Minecraft.getInstance().mouseHandler.xpos(), window.getScreenWidth(), window.getWidth());
	}

	@Override
	public double getMouseY() {
		final Window window = Minecraft.getInstance().getWindow();
		return GlfwWindows.toFramebuffer(Minecraft.getInstance().mouseHandler.ypos(), window.getScreenHeight(), window.getHeight());
	}

	@Override
	public boolean isMouseGrabbed() {
		return Minecraft.getInstance().mouseHandler.isMouseGrabbed();
	}

	@Override
	public boolean isKeyDown(final @NonNull Key key) {
		return GlfwKeys.isKeyDown(key, code -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), code));
	}

	@Override
	public boolean isPhysicalKeyDown(final @NonNull Key key) {
		return GlfwKeys.isPhysicalKeyDown(key, code -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), code));
	}

	@Override
	public @NonNull String getClipboard() {
		final String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
		return clipboard == null ? "" : clipboard;
	}

	@Override
	public void setClipboard(final @NonNull String text) {
		Minecraft.getInstance().keyboardHandler.setClipboard(text);
	}

}