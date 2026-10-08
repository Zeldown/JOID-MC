package dev.joid.backend.minecraft.ui.bridge;

import java.util.ArrayList;
import java.util.List;

import dev.joid.backend.minecraft.render.RenderBridge;
import dev.joid.backend.minecraft.render.composite.GuiCompositor;
import dev.joid.backend.minecraft.ui.screen.ScreenInputForwarder;
import dev.joid.backend.minecraft.ui.screen.UIScreen;
import dev.joid.internal.JOID;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.ui.UIBridge;
import dev.joid.lib.ui.core.UI;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ScreenUIBridge extends UIBridge {

	private final GuiCompositor compositor;

	@Getter
	private final ScreenInputForwarder input;

	private List<String> hoverList;
	private ItemStack    hoverStack;

	private ScreenUIBridge(final RenderBridge render) {
		this.compositor = GuiCompositor.create(render);
		this.input      = ScreenInputForwarder.create(this);
	}

	public static @NonNull ScreenUIBridge create(final @NonNull RenderBridge render) {
		return new ScreenUIBridge(render);
	}

	@Override
	public void open(final @NonNull UI ui) {
		if (!ui.getPopup().active()) {
			for (final UI currentUi : new ArrayList<>(super.getUiList().ordered())) {
				final boolean result = currentUi.onClose();
				if (currentUi.getTransition() != null && currentUi.getTransition().getOut() != null && currentUi.getTransition().getOut().isRunning()) {
					currentUi.getTransition().getOut().getAnimator().setCallback(_ -> JOID.open(ui));
					return;
				}

				if (!result) {
					return;
				}

				this.remove(currentUi);
			}
		}

		this.add(ui);
		if (!(Minecraft.getInstance().gui.screen() instanceof UIScreen)) {
			Minecraft.getInstance().gui.setScreen(UIScreen.create(this));
		}
	}

	@Override
	public void close(final @NonNull UI ui) {
		this.remove(ui);
		if (super.getUiList().isEmpty() && Minecraft.getInstance().gui.screen() instanceof UIScreen) {
			Minecraft.getInstance().gui.setScreen(null);
		}
	}

	@Override
	public void add(final @NonNull UI ui) {
		super.getUiList().add(ui);
		ui.load(BridgeHandler.WINDOW.get().getWidth(), BridgeHandler.WINDOW.get().getHeight());
	}

	@Override
	public void remove(final @NonNull UI ui) {
		super.getUiList().remove(ui);
	}

	@Override
	public boolean canHandle(final @NonNull UI ui) {
		return true;
	}

	@Override
	public boolean canHandle(final @NonNull Class<? extends UI> clazz) {
		return true;
	}

	@Override
	public double getInterfaceScale(final @NonNull UI ui) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Window window = minecraft.getWindow();
		return window.getGuiScale() / (double) window.calculateScale(0, minecraft.isEnforceUnicode());
	}

	@Override
	public void drawHover(final @NonNull UI ui, final @NonNull List<@NonNull String> lines, final double mouseX, final double mouseY) {
		this.hoverList = new ArrayList<>(lines);
	}

	public void drawHover(final @NonNull ItemStack stack) {
		this.hoverStack = stack;
	}

	public void extract(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
		this.input.flush();
		super.update();
		this.compositor.composite(graphics, super::draw);

		final List<String> hoverList = this.hoverList;
		this.hoverList = null;
		if (hoverList != null && !hoverList.isEmpty()) {
			graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, hoverList.stream().<Component>map(Component::literal).toList(), mouseX, mouseY);
		}

		final ItemStack hoverStack = this.hoverStack;
		this.hoverStack = null;
		if (hoverStack != null) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, hoverStack, mouseX, mouseY);
		}
	}

	public void closeAll() {
		for (final UI ui : new ArrayList<>(super.getUiList().ordered())) {
			ui.properlyClose();
			this.remove(ui);
		}
	}

}