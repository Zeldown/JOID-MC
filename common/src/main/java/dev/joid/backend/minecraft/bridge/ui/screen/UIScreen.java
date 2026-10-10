package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class UIScreen extends Screen {

	private final ScreenUIBridge bridge;

	protected UIScreen(final ScreenUIBridge bridge) {
		super(Component.empty());
		this.bridge = bridge;
	}

	public static @NonNull UIScreen create(final @NonNull ScreenUIBridge bridge) {
		return new UIScreen(bridge);
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
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		this.bridge.frame(graphics, mouseX, mouseY);
	}

	@Override
	public void extractTransparentBackground(final GuiGraphicsExtractor graphics) {
		if (this.hasBackground()) {
			super.extractTransparentBackground(graphics);
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean isInGameUi() {
		return !this.hasBackground() || super.isInGameUi();
	}

	@Override
	public boolean isPauseScreen() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.pause();
	}

	@Override
	public @NonNull Component getTitle() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.title().isEmpty() ? super.getTitle() : Component.translatable(data.title());
	}

	@Override
	public void mouseMoved(final double x, final double y) {
		this.bridge.getInput().mouseMoved();
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		return this.bridge.getInput().mousePressed(event.button());
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		return this.bridge.getInput().mouseReleased(event.button());
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		return this.bridge.getInput().mouseScrolled(scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		return this.bridge.getInput().keyPressed(event.key());
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		return this.bridge.getInput().charTyped(event.codepoint());
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.background();
	}

}