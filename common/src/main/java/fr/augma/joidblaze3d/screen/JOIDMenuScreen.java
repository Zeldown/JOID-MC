package fr.augma.joidblaze3d.screen;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class JOIDMenuScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	@Getter private final UI ui;

	protected JOIDMenuScreen(final @NonNull T menu, final @NonNull Inventory inventory, final @NonNull Component title, final int imageWidth, final int imageHeight, final @NonNull UI ui) {
		super(menu, inventory, title, imageWidth, imageHeight);
		this.ui = ui;
	}

	@Override
	public void added() {
		super.added();
		ScreenBridge.inst().setHost(this);
	}

	@Override
	public void removed() {
		if (ScreenBridge.inst().getHost() == this) {
			ScreenBridge.inst().setHost(null);
			ScreenBridge.inst().closeAll();
		}
		super.removed();
	}

	@Override
	protected void init() {
		super.init();
		if (ScreenBridge.inst().isOpened(this.ui)) {
			ScreenBridge.inst().load();
		} else {
			JOID.open(this.ui);
		}
	}

	@Override
	protected void containerTick() {
		if (ScreenBridge.inst().getUiList().isEmpty()) {
			this.onClose();
		}
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		ScreenBridge.inst().render(graphics, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		ScreenBridge.inst().mousePressed(event.button());
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		ScreenBridge.inst().mouseReleased();
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
		ScreenBridge.inst().mouseDragged();
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		ScreenBridge.inst().mouseScrolled(scrollY);
		return super.mouseScrolled(x, y, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		ScreenBridge.inst().keyPressed(event.key(), event.modifiers());
		return event.isEscape() || super.keyPressed(event);
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		ScreenBridge.inst().charTyped(event.codepoint());
		return super.charTyped(event);
	}

}