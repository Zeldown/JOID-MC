package dev.joid.impl.joidmc.lib.screen;

import dev.joid.impl.joidmc.lib.bridge.ui.ScreenBridge;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class JOIDScreen extends Screen {

	protected JOIDScreen() {
		super(Component.empty());
	}

	public static @NonNull JOIDScreen create() {
		return new JOIDScreen();
	}

	@Override
	public void added() {
		ScreenBridge.inst().setHost(this);
	}

	@Override
	public void removed() {
		if (ScreenBridge.inst().getHost() == this) {
			ScreenBridge.inst().setHost(null);
			ScreenBridge.inst().closeAll();
		}
	}

	@Override
	protected void init() {
		ScreenBridge.inst().reload();
	}

	@Override
	public void tick() {
		if (ScreenBridge.inst().getUiList().isEmpty()) {
			this.onClose();
		}
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		ScreenBridge.inst().render(graphics, mouseX, mouseY);
	}

	@Override
	public boolean isPauseScreen() {
		return ScreenBridge.inst().getUiList().ordered().stream().anyMatch(ui -> ui.getData().pause());
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		ScreenBridge.inst().mousePressed(event.button());
		return true;
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		ScreenBridge.inst().mouseReleased();
		return true;
	}

	@Override
	public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
		ScreenBridge.inst().mouseDragged();
		return true;
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		ScreenBridge.inst().mouseScrolled(scrollY);
		return true;
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		ScreenBridge.inst().keyPressed(event.key(), event.modifiers());
		return true;
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		ScreenBridge.inst().charTyped(event.codepoint());
		return true;
	}

}