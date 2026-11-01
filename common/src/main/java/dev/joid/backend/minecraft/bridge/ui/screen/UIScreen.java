package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.lib.input.mouse.MouseButton;
import lombok.NonNull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class UIScreen extends Screen {

	private final ScreenUIBridge bridge;

	public UIScreen(final ScreenUIBridge bridge) {
		super(Component.empty());
		this.bridge = bridge;
	}

	@Override
	protected void init() {
		this.bridge.load();
	}

	@Override
	public void removed() {
		this.bridge.removed(this);
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
		if (this.hasBackground()) {
			super.renderBackground(graphics, mouseX, mouseY, partialTick);
		}

		this.bridge.frame(graphics, mouseX, mouseY);
	}

	@Override
	public boolean isPauseScreen() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.pause();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public @NonNull Component getTitle() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.title().isEmpty() ? super.getTitle() : Component.translatable(data.title());
	}

	@Override
	public void mouseMoved(final double x, final double y) {
		this.bridge.mouseMoved();
	}

	@Override
	public boolean mouseClicked(final double x, final double y, final int button) {
		return this.bridge.mousePressed(MouseButton.of(button));
	}

	@Override
	public boolean mouseReleased(final double x, final double y, final int button) {
		return this.bridge.mouseReleased(MouseButton.of(button));
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		return this.bridge.mouseScroll(scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final int key, final int scanCode, final int modifiers) {
		return this.bridge.keyPressed(GlfwKeys.getPhysicalKey(key), GlfwKeys.getKey(key));
	}

	@Override
	public boolean keyReleased(final int key, final int scanCode, final int modifiers) {
		return this.bridge.keyReleased(GlfwKeys.getPhysicalKey(key), GlfwKeys.getKey(key));
	}

	@Override
	public boolean charTyped(final char character, final int modifiers) {
		return StringUtil.isAllowedChatCharacter(character) && this.bridge.charTyped(character);
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.background();
	}

}