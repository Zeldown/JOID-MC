package dev.joid.backend.minecraft.bridge.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.lib.input.mouse.MouseButton;
import lombok.NonNull;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class UIScreen extends Screen {

	private final ScreenUIBridge bridge;

	public UIScreen(final ScreenUIBridge bridge) {
		super(TextComponent.EMPTY);
		this.bridge = bridge;
	}

	@Override
	protected void init() {
		super.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		this.bridge.load();
	}

	@Override
	public void removed() {
		this.bridge.removed(this);
	}

	@Override
	public void render(final PoseStack pose, final int mouseX, final int mouseY, final float partialTick) {
		if (this.hasBackground()) {
			super.renderBackground(pose);
		}

		this.bridge.frame(pose, mouseX, mouseY);
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
		return data == null || data.title().isEmpty() ? super.getTitle() : new TranslatableComponent(data.title());
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
	public boolean mouseScrolled(final double x, final double y, final double scroll) {
		return this.bridge.mouseScroll(0D, scroll);
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
		return SharedConstants.isAllowedChatCharacter(character) && this.bridge.charTyped(character);
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.background();
	}

}