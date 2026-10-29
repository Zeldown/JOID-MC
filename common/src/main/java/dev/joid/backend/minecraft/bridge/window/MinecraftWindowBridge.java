package dev.joid.backend.minecraft.bridge.window;

import dev.joid.base.lwjgl2.window.Lwjgl2WindowBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.input.key.Key;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class MinecraftWindowBridge implements IWindowBridge {

	private final Lwjgl2WindowBridge window;

	@Getter
	private Cursor cursor;

	private Cursor requested;
	private Cursor applied;

	public MinecraftWindowBridge() {
		this.window  = new Lwjgl2WindowBridge();
		this.cursor  = Cursor.DEFAULT;
		this.applied = Cursor.DEFAULT;
	}

	@Override
	public int getWidth() {
		return this.window.getWidth();
	}

	@Override
	public int getHeight() {
		return this.window.getHeight();
	}

	@Override
	public double getMouseX() {
		return this.isMouseGrabbed() ? -1D : this.window.getMouseX();
	}

	@Override
	public double getMouseY() {
		return this.isMouseGrabbed() ? -1D : this.window.getMouseY();
	}

	@Override
	public boolean isMouseGrabbed() {
		return this.window.isMouseGrabbed();
	}

	@Override
	public boolean isKeyDown(final @NonNull Key physicalKey) {
		return this.window.isKeyDown(physicalKey);
	}

	@Override
	public @NonNull String getClipboard() {
		return this.window.getClipboard();
	}

	@Override
	public void setClipboard(final @NonNull String text) {
		this.window.setClipboard(text);
	}

	@Override
	public void setCursor(final @NonNull Cursor cursor) {
		this.cursor = cursor;
	}

	public double getInterfaceScale() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		return new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor() / (double) MinecraftWindowBridge.getAutoScale(minecraft.displayWidth, minecraft.displayHeight, minecraft.func_152349_b());
	}

	public void requestCursor(final Cursor cursor) {
		if (!this.isMouseGrabbed()) {
			this.requested = cursor;
		}
	}

	public void applyCursor() {
		final Cursor cursor = this.requested != null ? this.requested : Cursor.DEFAULT;
		this.requested = null;
		if (cursor != this.applied && !this.isMouseGrabbed()) {
			this.applied = cursor;
			this.window.setCursor(cursor);
		}
	}

	private static int getAutoScale(final int width, final int height, final boolean unicode) {
		int scale = 1;
		while (width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
			scale++;
		}
		return unicode && scale % 2 != 0 && scale != 1 ? scale - 1 : scale;
	}

}