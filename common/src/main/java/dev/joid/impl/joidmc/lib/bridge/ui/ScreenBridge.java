package dev.joid.impl.joidmc.lib.bridge.ui;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import dev.joid.internal.JOID;
import dev.joid.lib.bridge.ui.IUIBridge;
import dev.joid.lib.bridge.window.IWindowBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.utils.click.ClickType;
import dev.joid.lib.utils.key.Key;
import dev.joid.impl.joidmc.lib.screen.JOIDScreen;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenBridge extends MCUIBridge implements IWindowBridge {

	private static final Map<Key, Integer> CODE_MAP = new EnumMap<>(Key.class);
	private static final Map<Integer, Key> KEY_MAP  = new HashMap<>();
	private static final ScreenBridge      INSTANCE = new ScreenBridge();

	static {
		ScreenBridge.map(Key.A, GLFW.GLFW_KEY_A);
		ScreenBridge.map(Key.B, GLFW.GLFW_KEY_B);
		ScreenBridge.map(Key.C, GLFW.GLFW_KEY_C);
		ScreenBridge.map(Key.D, GLFW.GLFW_KEY_D);
		ScreenBridge.map(Key.E, GLFW.GLFW_KEY_E);
		ScreenBridge.map(Key.F, GLFW.GLFW_KEY_F);
		ScreenBridge.map(Key.G, GLFW.GLFW_KEY_G);
		ScreenBridge.map(Key.H, GLFW.GLFW_KEY_H);
		ScreenBridge.map(Key.I, GLFW.GLFW_KEY_I);
		ScreenBridge.map(Key.J, GLFW.GLFW_KEY_J);
		ScreenBridge.map(Key.K, GLFW.GLFW_KEY_K);
		ScreenBridge.map(Key.L, GLFW.GLFW_KEY_L);
		ScreenBridge.map(Key.M, GLFW.GLFW_KEY_M);
		ScreenBridge.map(Key.N, GLFW.GLFW_KEY_N);
		ScreenBridge.map(Key.O, GLFW.GLFW_KEY_O);
		ScreenBridge.map(Key.P, GLFW.GLFW_KEY_P);
		ScreenBridge.map(Key.Q, GLFW.GLFW_KEY_Q);
		ScreenBridge.map(Key.R, GLFW.GLFW_KEY_R);
		ScreenBridge.map(Key.S, GLFW.GLFW_KEY_S);
		ScreenBridge.map(Key.T, GLFW.GLFW_KEY_T);
		ScreenBridge.map(Key.U, GLFW.GLFW_KEY_U);
		ScreenBridge.map(Key.V, GLFW.GLFW_KEY_V);
		ScreenBridge.map(Key.W, GLFW.GLFW_KEY_W);
		ScreenBridge.map(Key.X, GLFW.GLFW_KEY_X);
		ScreenBridge.map(Key.Y, GLFW.GLFW_KEY_Y);
		ScreenBridge.map(Key.Z, GLFW.GLFW_KEY_Z);
		ScreenBridge.map(Key.DIGIT_0, GLFW.GLFW_KEY_0);
		ScreenBridge.map(Key.DIGIT_1, GLFW.GLFW_KEY_1);
		ScreenBridge.map(Key.DIGIT_2, GLFW.GLFW_KEY_2);
		ScreenBridge.map(Key.DIGIT_3, GLFW.GLFW_KEY_3);
		ScreenBridge.map(Key.DIGIT_4, GLFW.GLFW_KEY_4);
		ScreenBridge.map(Key.DIGIT_5, GLFW.GLFW_KEY_5);
		ScreenBridge.map(Key.DIGIT_6, GLFW.GLFW_KEY_6);
		ScreenBridge.map(Key.DIGIT_7, GLFW.GLFW_KEY_7);
		ScreenBridge.map(Key.DIGIT_8, GLFW.GLFW_KEY_8);
		ScreenBridge.map(Key.DIGIT_9, GLFW.GLFW_KEY_9);
		ScreenBridge.map(Key.F1, GLFW.GLFW_KEY_F1);
		ScreenBridge.map(Key.F2, GLFW.GLFW_KEY_F2);
		ScreenBridge.map(Key.F3, GLFW.GLFW_KEY_F3);
		ScreenBridge.map(Key.F4, GLFW.GLFW_KEY_F4);
		ScreenBridge.map(Key.F5, GLFW.GLFW_KEY_F5);
		ScreenBridge.map(Key.F6, GLFW.GLFW_KEY_F6);
		ScreenBridge.map(Key.F7, GLFW.GLFW_KEY_F7);
		ScreenBridge.map(Key.F8, GLFW.GLFW_KEY_F8);
		ScreenBridge.map(Key.F9, GLFW.GLFW_KEY_F9);
		ScreenBridge.map(Key.F10, GLFW.GLFW_KEY_F10);
		ScreenBridge.map(Key.F11, GLFW.GLFW_KEY_F11);
		ScreenBridge.map(Key.F12, GLFW.GLFW_KEY_F12);
		ScreenBridge.map(Key.F13, GLFW.GLFW_KEY_F13);
		ScreenBridge.map(Key.F14, GLFW.GLFW_KEY_F14);
		ScreenBridge.map(Key.F15, GLFW.GLFW_KEY_F15);
		ScreenBridge.map(Key.F16, GLFW.GLFW_KEY_F16);
		ScreenBridge.map(Key.F17, GLFW.GLFW_KEY_F17);
		ScreenBridge.map(Key.F18, GLFW.GLFW_KEY_F18);
		ScreenBridge.map(Key.F19, GLFW.GLFW_KEY_F19);
		ScreenBridge.map(Key.F20, GLFW.GLFW_KEY_F20);
		ScreenBridge.map(Key.F21, GLFW.GLFW_KEY_F21);
		ScreenBridge.map(Key.F22, GLFW.GLFW_KEY_F22);
		ScreenBridge.map(Key.F23, GLFW.GLFW_KEY_F23);
		ScreenBridge.map(Key.F24, GLFW.GLFW_KEY_F24);
		ScreenBridge.map(Key.F25, GLFW.GLFW_KEY_F25);
		ScreenBridge.map(Key.ESCAPE, GLFW.GLFW_KEY_ESCAPE);
		ScreenBridge.map(Key.ENTER, GLFW.GLFW_KEY_ENTER);
		ScreenBridge.map(Key.TAB, GLFW.GLFW_KEY_TAB);
		ScreenBridge.map(Key.BACKSPACE, GLFW.GLFW_KEY_BACKSPACE);
		ScreenBridge.map(Key.INSERT, GLFW.GLFW_KEY_INSERT);
		ScreenBridge.map(Key.DELETE, GLFW.GLFW_KEY_DELETE);
		ScreenBridge.map(Key.RIGHT, GLFW.GLFW_KEY_RIGHT);
		ScreenBridge.map(Key.LEFT, GLFW.GLFW_KEY_LEFT);
		ScreenBridge.map(Key.DOWN, GLFW.GLFW_KEY_DOWN);
		ScreenBridge.map(Key.UP, GLFW.GLFW_KEY_UP);
		ScreenBridge.map(Key.PAGE_UP, GLFW.GLFW_KEY_PAGE_UP);
		ScreenBridge.map(Key.PAGE_DOWN, GLFW.GLFW_KEY_PAGE_DOWN);
		ScreenBridge.map(Key.HOME, GLFW.GLFW_KEY_HOME);
		ScreenBridge.map(Key.END, GLFW.GLFW_KEY_END);
		ScreenBridge.map(Key.CAPS_LOCK, GLFW.GLFW_KEY_CAPS_LOCK);
		ScreenBridge.map(Key.SCROLL_LOCK, GLFW.GLFW_KEY_SCROLL_LOCK);
		ScreenBridge.map(Key.NUM_LOCK, GLFW.GLFW_KEY_NUM_LOCK);
		ScreenBridge.map(Key.PRINT_SCREEN, GLFW.GLFW_KEY_PRINT_SCREEN);
		ScreenBridge.map(Key.PAUSE, GLFW.GLFW_KEY_PAUSE);
		ScreenBridge.map(Key.SPACE, GLFW.GLFW_KEY_SPACE);
		ScreenBridge.map(Key.APOSTROPHE, GLFW.GLFW_KEY_APOSTROPHE);
		ScreenBridge.map(Key.COMMA, GLFW.GLFW_KEY_COMMA);
		ScreenBridge.map(Key.MINUS, GLFW.GLFW_KEY_MINUS);
		ScreenBridge.map(Key.PERIOD, GLFW.GLFW_KEY_PERIOD);
		ScreenBridge.map(Key.SLASH, GLFW.GLFW_KEY_SLASH);
		ScreenBridge.map(Key.SEMICOLON, GLFW.GLFW_KEY_SEMICOLON);
		ScreenBridge.map(Key.EQUAL, GLFW.GLFW_KEY_EQUAL);
		ScreenBridge.map(Key.LEFT_BRACKET, GLFW.GLFW_KEY_LEFT_BRACKET);
		ScreenBridge.map(Key.BACKSLASH, GLFW.GLFW_KEY_BACKSLASH);
		ScreenBridge.map(Key.RIGHT_BRACKET, GLFW.GLFW_KEY_RIGHT_BRACKET);
		ScreenBridge.map(Key.GRAVE_ACCENT, GLFW.GLFW_KEY_GRAVE_ACCENT);
		ScreenBridge.map(Key.NUMPAD_0, GLFW.GLFW_KEY_KP_0);
		ScreenBridge.map(Key.NUMPAD_1, GLFW.GLFW_KEY_KP_1);
		ScreenBridge.map(Key.NUMPAD_2, GLFW.GLFW_KEY_KP_2);
		ScreenBridge.map(Key.NUMPAD_3, GLFW.GLFW_KEY_KP_3);
		ScreenBridge.map(Key.NUMPAD_4, GLFW.GLFW_KEY_KP_4);
		ScreenBridge.map(Key.NUMPAD_5, GLFW.GLFW_KEY_KP_5);
		ScreenBridge.map(Key.NUMPAD_6, GLFW.GLFW_KEY_KP_6);
		ScreenBridge.map(Key.NUMPAD_7, GLFW.GLFW_KEY_KP_7);
		ScreenBridge.map(Key.NUMPAD_8, GLFW.GLFW_KEY_KP_8);
		ScreenBridge.map(Key.NUMPAD_9, GLFW.GLFW_KEY_KP_9);
		ScreenBridge.map(Key.NUMPAD_DECIMAL, GLFW.GLFW_KEY_KP_DECIMAL);
		ScreenBridge.map(Key.NUMPAD_DIVIDE, GLFW.GLFW_KEY_KP_DIVIDE);
		ScreenBridge.map(Key.NUMPAD_MULTIPLY, GLFW.GLFW_KEY_KP_MULTIPLY);
		ScreenBridge.map(Key.NUMPAD_SUBTRACT, GLFW.GLFW_KEY_KP_SUBTRACT);
		ScreenBridge.map(Key.NUMPAD_ADD, GLFW.GLFW_KEY_KP_ADD);
		ScreenBridge.map(Key.NUMPAD_ENTER, GLFW.GLFW_KEY_KP_ENTER);
		ScreenBridge.map(Key.NUMPAD_EQUAL, GLFW.GLFW_KEY_KP_EQUAL);
		ScreenBridge.map(Key.LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_SHIFT);
		ScreenBridge.map(Key.LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_CONTROL);
		ScreenBridge.map(Key.LEFT_ALT, GLFW.GLFW_KEY_LEFT_ALT);
		ScreenBridge.map(Key.LEFT_SUPER, GLFW.GLFW_KEY_LEFT_SUPER);
		ScreenBridge.map(Key.RIGHT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
		ScreenBridge.map(Key.RIGHT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);
		ScreenBridge.map(Key.RIGHT_ALT, GLFW.GLFW_KEY_RIGHT_ALT);
		ScreenBridge.map(Key.RIGHT_SUPER, GLFW.GLFW_KEY_RIGHT_SUPER);
		ScreenBridge.map(Key.MENU, GLFW.GLFW_KEY_MENU);
	}

	@Getter @Setter private Screen host;

	private ClickType clickType;
	private long      pressTime;
	private Key       pendingKey;

	private ScreenBridge() {}

	public static @NonNull ScreenBridge inst() {
		return ScreenBridge.INSTANCE;
	}

	@Override
	public void open(final @NonNull UI ui) {
		if (!ui.getPopup().active()) {
			for (final UI currentUi : super.getUiList().copy()) {
				final boolean result = currentUi.onClose();
				if (currentUi.getTransition() != null && currentUi.getTransition().getOut() != null && currentUi.getTransition().getOut().isRunning()) {
					currentUi.getTransition().getOut().getAnimator().setCallback(tween -> JOID.open(ui));
					return;
				}

				if (!result) {
					return;
				}

				this.close(currentUi);
			}
		}

		this.add(ui);
		if (this.host == null) {
			Minecraft.getInstance().gui.setScreen(JOIDScreen.create());
		}
	}

	@Override
	public void close(final @NonNull UI ui) {
		this.remove(ui);
	}

	@Override
	public void add(final @NonNull UI ui) {
		super.getUiList().add(ui);
		ui.load(this.getWidth(), this.getHeight());
	}

	@Override
	public void remove(final @NonNull UI ui) {
		super.getUiList().remove(ui);
	}

	@Override
	public boolean isOnTop(final @NonNull UI ui) {
		if (super.getUiList().isEmpty()) {
			return false;
		}
		return super.getUiList().ordered().getLast() == ui && ui.getData().active() && ui.getData().visible();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> ui) {
		return !OverlayBridge.inst().canHandle(ui);
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return !OverlayBridge.inst().canHandle(ui);
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
		final Integer code = ScreenBridge.CODE_MAP.get(key);
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

	public void mousePressed(final int button) {
		this.flushKey();
		this.clickType = ClickType.from(button);
		this.pressTime = System.currentTimeMillis();
		super.mousePressed(this.clickType);
	}

	public void mouseReleased() {
		if (this.clickType != null) {
			super.mouseReleased(this.clickType);
			this.clickType = null;
		}
	}

	public void mouseDragged() {
		if (this.clickType != null && this.pressTime > 0L) {
			super.mouseDragged(this.clickType, System.currentTimeMillis() - this.pressTime);
		}
	}

	public void mouseScrolled(final double amount) {
		super.mouseScroll((int) (amount * 120D));
	}

	public void keyPressed(final int code, final int modifiers) {
		this.flushKey();
		final Key key = ScreenBridge.getKey(code);
		if (ScreenBridge.isTextKey(code) && (modifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT)) == 0) {
			this.pendingKey = key;
			return;
		}

		super.keyTyped((char) 0, key);
	}

	public void charTyped(final int codepoint) {
		final Key key = this.pendingKey == null ? Key.UNKNOWN : this.pendingKey;
		this.pendingKey = null;
		super.keyTyped((char) codepoint, key);
	}

	public void flushKey() {
		if (this.pendingKey == null) {
			return;
		}

		final Key key = this.pendingKey;
		this.pendingKey = null;
		super.keyTyped((char) 0, key);
	}

	public void render(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		this.flushKey();
		super.update();
		super.extract(graphics, super.getUiList().copy().ordered(), mouseX, mouseY);
	}

	public void reload() {
		for (final UI ui : super.getUiList().copy()) {
			ui.load(this.getWidth(), this.getHeight());
		}
	}

	public void closeAll() {
		for (final UI ui : super.getUiList().copy()) {
			ui.properlyClose();
			this.close(ui);
		}
	}

	public static @NonNull Key getKey(final int code) {
		return ScreenBridge.KEY_MAP.getOrDefault(code, Key.UNKNOWN);
	}

	private static boolean isTextKey(final int code) {
		return code >= GLFW.GLFW_KEY_SPACE && code <= GLFW.GLFW_KEY_GRAVE_ACCENT || code >= GLFW.GLFW_KEY_KP_0 && code <= GLFW.GLFW_KEY_KP_ADD;
	}

	private static void map(final Key key, final int code) {
		ScreenBridge.CODE_MAP.put(key, code);
		ScreenBridge.KEY_MAP.put(code, key);
	}

}