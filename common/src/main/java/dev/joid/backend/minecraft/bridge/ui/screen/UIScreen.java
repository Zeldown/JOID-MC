package dev.joid.backend.minecraft.bridge.ui.screen;

import dev.joid.backend.minecraft.bridge.input.SdlKeys;
import dev.joid.backend.minecraft.bridge.input.SdlMouseButtons;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import lombok.NonNull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
		super.minecraft.textInputManager().startTextInput(this);
		this.bridge.load();
	}

	@Override
	public void removed() {
		super.minecraft.textInputManager().stopTextInput(this);
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
	public boolean isInGameUi() {
		return !this.hasBackground() || super.isInGameUi();
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
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		return this.bridge.mousePressed(SdlMouseButtons.getButton(event.button()));
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		return this.bridge.mouseReleased(SdlMouseButtons.getButton(event.button()));
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		return this.bridge.mouseScroll(scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		return this.bridge.keyPressed(SdlKeys.getPhysicalKey(event.key()), SdlKeys.getKey(event.key()));
	}

	@Override
	public boolean keyReleased(final KeyEvent event) {
		return this.bridge.keyReleased(SdlKeys.getPhysicalKey(event.key()), SdlKeys.getKey(event.key()));
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		return StringUtil.isAllowedChatCharacter(event.codepoint()) && this.bridge.charTyped(event.codepoint());
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.bridge.getMinecraftData();
		return data == null || data.background();
	}

}