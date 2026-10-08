package dev.joid.backend.minecraft.window;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.utils.key.Key;
import dev.joid.lib.utils.key.KeyLayout;
import lombok.NonNull;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

public final class WindowBridge implements IWindowBridge {

	private static final Map<Key, Integer> CODE_MAP = new EnumMap<>(Key.class);
	private static final KeyLayout         LAYOUT   = KeyLayout.create(key -> GLFW.glfwGetKeyName(WindowBridge.CODE_MAP.get(key), 0));

	static {
		for (int code = GLFW.GLFW_KEY_SPACE; code <= GLFW.GLFW_KEY_LAST; code++) {
			final Key key = dev.joid.impl.glfw.WindowBridge.getPhysicalKey(code);
			if (key != Key.UNKNOWN) {
				WindowBridge.CODE_MAP.put(key, code);
			}
		}
	}

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
		final double x = Minecraft.getInstance().mouseHandler.xpos();
		return window.getScreenWidth() == 0 ? x : x * window.getWidth() / window.getScreenWidth();
	}

	@Override
	public double getMouseY() {
		final Window window = Minecraft.getInstance().getWindow();
		final double y = Minecraft.getInstance().mouseHandler.ypos();
		return window.getScreenHeight() == 0 ? y : y * window.getHeight() / window.getScreenHeight();
	}

	@Override
	public boolean isMouseGrabbed() {
		return Minecraft.getInstance().mouseHandler.isMouseGrabbed();
	}

	@Override
	public boolean isKeyDown(final @NonNull Key key) {
		return WindowBridge.LAYOUT.isDown(key, this::isPhysicalKeyDown);
	}

	@Override
	public boolean isPhysicalKeyDown(final @NonNull Key key) {
		final Integer code = WindowBridge.CODE_MAP.get(key);
		return code != null && InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), code);
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