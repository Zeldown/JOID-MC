package dev.joid.impl.joidmc.lib.bridge.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import dev.joid.lib.bridge.ui.IUIBridge;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.utils.click.ClickType;
import dev.joid.lib.utils.key.Key;
import dev.joid.impl.joidmc.lib.ui.core.data.overlay.UIMCOverlay;
import dev.joid.impl.joidmc.lib.ui.core.data.overlay.interaction.UIMCOverlayInteraction;
import dev.joid.impl.joidmc.lib.ui.core.data.overlay.render.ElementType;
import dev.joid.impl.joidmc.lib.ui.core.data.overlay.render.UIMCOverlayRender;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class OverlayBridge extends MCUIBridge {

	private static final OverlayBridge INSTANCE = new OverlayBridge();

	private ClickType clickType;
	private long      pressTime;
	private long      frameTime;
	private int       width;
	private int       height;

	private OverlayBridge() {}

	public static @NonNull OverlayBridge inst() {
		return OverlayBridge.INSTANCE;
	}

	@Override
	public void open(final @NonNull UI ui) {
		this.add(ui);
	}

	@Override
	public void close(final @NonNull UI ui) {
		this.remove(ui);
	}

	@Override
	public void add(final @NonNull UI ui) {
		super.getUiList().add(ui);
		final Window window = Minecraft.getInstance().getWindow();
		ui.load(window.getWidth(), window.getHeight());
	}

	@Override
	public void remove(final @NonNull UI ui) {
		super.getUiList().remove(ui);
	}

	@Override
	public boolean isOnTop(final @NonNull UI ui) {
		return !Minecraft.getInstance().mouseHandler.isMouseGrabbed() && ui.getData().active() && ui.getData().visible() && OverlayBridge.getOverlay(ui).interaction().active();
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> ui) {
		return ui.isAnnotationPresent(UIMCOverlay.class);
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return this.canHandle(ui.getClass());
	}

	public void extract(final @NonNull GuiGraphicsExtractor graphics, final @NonNull ElementType type, final boolean post, final boolean cancelled) {
		if (!type.isActive()) {
			return;
		}

		this.extract(graphics, render -> render.type() == type && render.post() == post && (!cancelled || render.always()));
	}

	public void extractScreen(final @NonNull GuiGraphicsExtractor graphics, final boolean post) {
		this.extract(graphics, render -> render.gui() && render.post() == post);
	}

	public boolean isCancelled(final @NonNull ElementType type) {
		for (final UI ui : super.getUiList().ordered()) {
			final UIMCOverlayRender render = OverlayBridge.getOverlay(ui).render();
			if (ui.getData().visible() && (render.cancel() && render.type() == type || List.of(render.hide()).contains(type))) {
				return true;
			}
		}

		return false;
	}

	public boolean mousePressed(final int button) {
		final ClickType clickType = ClickType.from(button);
		this.clickType = clickType;
		this.pressTime = System.currentTimeMillis();
		return this.dispatch(ui -> ui.onMousePressed(clickType), UIMCOverlayInteraction::cancelClick);
	}

	public boolean mouseReleased(final int button) {
		this.clickType = null;
		return this.dispatch(ui -> ui.onMouseReleased(ClickType.from(button)), UIMCOverlayInteraction::cancelClick);
	}

	public boolean mouseDragged() {
		final ClickType clickType = this.clickType;
		if (clickType == null) {
			return false;
		}

		final long time = System.currentTimeMillis() - this.pressTime;
		return this.dispatch(ui -> ui.onMouseDragged(clickType, time), UIMCOverlayInteraction::cancelClick);
	}

	public boolean mouseScrolled(final double amount) {
		return this.dispatch(ui -> ui.onMouseScroll((int) (amount * 120D)), UIMCOverlayInteraction::cancelScroll);
	}

	public boolean keyPressed(final int code) {
		return this.dispatch(ui -> ui.onKeyPressed((char) 0, ScreenBridge.getKey(code)), UIMCOverlayInteraction::cancelKeyboard);
	}

	public boolean charTyped(final int codepoint) {
		return this.dispatch(ui -> ui.onKeyPressed((char) codepoint, Key.UNKNOWN), UIMCOverlayInteraction::cancelKeyboard);
	}

	private void extract(final GuiGraphicsExtractor graphics, final Predicate<UIMCOverlayRender> filter) {
		if (super.getUiList().isEmpty()) {
			return;
		}

		this.refresh();
		final List<UI> uiList = new ArrayList<>();
		for (final UI ui : this.getOrderedList()) {
			if (ui.getData().visible() && filter.test(OverlayBridge.getOverlay(ui).render())) {
				uiList.add(ui);
			}
		}

		if (uiList.isEmpty()) {
			return;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		final int mouseX = (int) (minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
		final int mouseY = (int) (minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());
		super.extract(graphics, uiList, mouseX, mouseY);
	}

	private void refresh() {
		final Minecraft minecraft = Minecraft.getInstance();
		final long frameTime = minecraft.getFrameTimeNs();
		if (frameTime == this.frameTime) {
			return;
		}

		this.frameTime = frameTime;
		final Window window = minecraft.getWindow();
		if (window.getWidth() != this.width || window.getHeight() != this.height) {
			this.width  = window.getWidth();
			this.height = window.getHeight();
			for (final UI ui : super.getUiList().copy()) {
				ui.load(this.width, this.height);
			}
		}

		super.update();
	}

	private boolean dispatch(final Predicate<UI> handler, final Predicate<UIMCOverlayInteraction> cancel) {
		final List<UI> uiList = this.getOrderedList();
		for (int index = uiList.size() - 1; index >= 0; index--) {
			final UI ui = uiList.get(index);
			final UIMCOverlayInteraction interaction = OverlayBridge.getOverlay(ui).interaction();
			if (interaction.active() && ui.getData().active() && ui.getData().visible() && (handler.test(ui) || ui.getPopup().active())) {
				return cancel.test(interaction);
			}
		}

		return false;
	}

	private List<UI> getOrderedList() {
		final List<UI> uiList = new ArrayList<>(super.getUiList().ordered());
		uiList.sort(Comparator.comparingInt(ui -> OverlayBridge.getOverlay(ui).render().zindex()));
		return uiList;
	}

	private static UIMCOverlay getOverlay(final UI ui) {
		return ui.getClass().getAnnotation(UIMCOverlay.class);
	}

}