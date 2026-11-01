package dev.joid.backend.minecraft.bridge.window;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import dev.joid.base.lwjgl3.glfw.GlfwWindowBridge;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;

public final class MinecraftWindowBridge implements IWindowBridge {

	private final GlfwWindowBridge window;

	@Getter
	private Cursor cursor;

	private Cursor requested;
	private Cursor applied;

	public MinecraftWindowBridge() {
		this.window  = new GlfwWindowBridge(Minecraft.getInstance().getWindow().getWindow());
		this.cursor  = Cursor.DEFAULT;
		this.applied = Cursor.DEFAULT;
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
		if (this.isMouseGrabbed()) {
			return -1D;
		}

		final Window window = Minecraft.getInstance().getWindow();
		return MinecraftWindowBridge.toFramebuffer(Minecraft.getInstance().mouseHandler.xpos(), window.getScreenWidth(), window.getWidth());
	}

	@Override
	public double getMouseY() {
		if (this.isMouseGrabbed()) {
			return -1D;
		}

		final Window window = Minecraft.getInstance().getWindow();
		return MinecraftWindowBridge.toFramebuffer(Minecraft.getInstance().mouseHandler.ypos(), window.getScreenHeight(), window.getHeight());
	}

	@Override
	public boolean isMouseGrabbed() {
		return Minecraft.getInstance().mouseHandler.isMouseGrabbed();
	}

	@Override
	public boolean isKeyDown(final @NonNull Key physicalKey) {
		return GlfwKeys.isKeyDown(physicalKey, code -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), code));
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

	public double getInterfaceScale() {
		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		return window.getGuiScale() / (double) window.calculateScale(0, minecraft.isEnforceUnicode());
	}

	public void requestCursor(final Cursor cursor) {
		if (cursor != Cursor.DEFAULT) {
			System.out.println("[JOIDDIAG] request " + cursor + " grabbed=" + this.isMouseGrabbed() + " from " + Thread.currentThread().getStackTrace()[2]);
		}
		if (!this.isMouseGrabbed()) {
			this.requested = cursor;
		}
	}

	public void applyCursor() {
		final Cursor cursor = this.requested != null ? this.requested : Cursor.DEFAULT;
		if (cursor != Cursor.DEFAULT || this.applied != Cursor.DEFAULT) {
			final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println("[JOIDDIAG] apply " + cursor + " applied=" + this.applied + " grabbed=" + this.isMouseGrabbed() + " from " + trace[3] + " / " + trace[4] + " / " + trace[5]);
		}
		this.requested = null;
		if (cursor != this.applied && !this.isMouseGrabbed()) {
			this.applied = cursor;
			this.window.setCursor(cursor);
		}
	}

	private static double toFramebuffer(final double position, final int windowSize, final int framebufferSize) {
		return windowSize == 0 ? position : position * framebufferSize / windowSize;
	}

}