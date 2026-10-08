package dev.joid.backend.minecraft.ui.screen;

import dev.joid.backend.minecraft.ui.bridge.ScreenUIBridge;
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
		this.bridge.closeAll();
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		this.bridge.extract(graphics, mouseX, mouseY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		this.bridge.getInput().mousePressed(event);
		return true;
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		this.bridge.getInput().mouseReleased();
		return true;
	}

	@Override
	public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
		this.bridge.getInput().mouseDragged();
		return true;
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		this.bridge.getInput().mouseScrolled(scrollY);
		return true;
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		this.bridge.getInput().keyPressed(event);
		return true;
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		this.bridge.getInput().charTyped(event);
		return true;
	}

}