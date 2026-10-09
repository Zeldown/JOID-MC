package dev.joid.backend.minecraft.window;

import org.lwjgl.glfw.GLFW;

import dev.joid.base.glfw.GlfwWindows;
import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.utils.cursor.Cursor;
import dev.joid.lib.utils.key.Key;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class WindowBridge implements IWindowBridge {

	@Getter
	private Cursor cursor;

	public WindowBridge() {
		this.cursor = Cursor.DEFAULT;
	}

	@Override
	public int getWidth() {
		return Minecraft.getInstance().getWindow().getWidth();
	}

	@Override
	public int getHeight() {
		return Minecraft.getInstance().getWindow().getHeight();
	}

	public double getInterfaceScale() {
		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		return window.getGuiScale() / (double) window.calculateScale(0, minecraft.isEnforceUnicode());
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

	@Override
	public void setCursor(final @NonNull Cursor cursor) {
		this.cursor = cursor;
	}

	public void requestCursor(final @NonNull GuiGraphicsExtractor graphics, final @NonNull Cursor cursor) {
		if (!this.isMouseGrabbed()) {
			graphics.requestCursor(WindowBridge.getCursorType(cursor));
		}
	}

	private static @NonNull CursorType getCursorType(final @NonNull Cursor cursor) {
		return switch (cursor) {
			case DEFAULT -> CursorType.DEFAULT;
			case POINTER -> CursorTypes.POINTING_HAND;
			case TEXT -> CursorTypes.IBEAM;
			case CROSSHAIR -> CursorTypes.CROSSHAIR;
			case MOVE -> CursorTypes.RESIZE_ALL;
			case NOT_ALLOWED -> CursorTypes.NOT_ALLOWED;
			case RESIZE_EW -> CursorTypes.RESIZE_EW;
			case RESIZE_NS -> CursorTypes.RESIZE_NS;
			case RESIZE_NWSE -> DiagonalCursor.NWSE;
			case RESIZE_NESW -> DiagonalCursor.NESW;
		};
	}

	private static final class DiagonalCursor {

		private static final CursorType NWSE = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", CursorType.DEFAULT);
		private static final CursorType NESW = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", CursorType.DEFAULT);

	}

}