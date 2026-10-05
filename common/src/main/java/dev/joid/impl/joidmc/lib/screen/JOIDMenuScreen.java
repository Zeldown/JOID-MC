package dev.joid.impl.joidmc.lib.screen;

import java.util.HashMap;
import java.util.Map;

import be.zeldown.joid.internal.JOID;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.node.Node;
import dev.joid.impl.joidmc.lib.bridge.ui.ScreenBridge;
import dev.joid.impl.joidmc.lib.ui.node.impl.structure.slot.SlotNode;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class JOIDMenuScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	@Getter private final UI ui;

	private final Map<Long, SlotNode> nodeMap;

	@Getter private double boundsX;
	@Getter private double boundsY;
	@Getter private double boundsWidth;
	@Getter private double boundsHeight;

	private Node    boundsNode;
	private boolean bounded;
	private boolean forwarded;
	private int     craftType;

	protected JOIDMenuScreen(final @NonNull T menu, final @NonNull Inventory inventory, final @NonNull Component title, final int imageWidth, final int imageHeight, final @NonNull UI ui) {
		super(menu, inventory, title, imageWidth, imageHeight);
		this.ui      = ui;
		this.nodeMap = new HashMap<>();
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
			ScreenBridge.inst().reload();
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
		this.refresh();
		super.hoveredSlot = this.getHovered(mouseX, mouseY);
		ScreenBridge.inst().render(graphics, mouseX, mouseY);
	}

	@Override
	public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

	@Override
	protected boolean isHovering(final int x, final int y, final int width, final int height, final double mouseX, final double mouseY) {
		final SlotNode node = this.nodeMap.get(JOIDMenuScreen.key(x, y));
		return node != null && node.isHovered(this.toDesignX(mouseX), this.toDesignY(mouseY));
	}

	public @NonNull JOIDMenuScreen<T> bounds(final @NonNull Node node) {
		this.bounded    = true;
		this.boundsNode = node;
		return this;
	}

	public @NonNull JOIDMenuScreen<T> bounds(final double x, final double y, final double width, final double height) {
		this.bounded      = true;
		this.boundsNode   = null;
		this.boundsX      = x;
		this.boundsY      = y;
		this.boundsWidth  = width;
		this.boundsHeight = height;
		return this;
	}

	@Override
	protected boolean hasClickedOutside(final double mouseX, final double mouseY, final int leftPos, final int topPos) {
		if (!this.bounded) {
			return false;
		}

		final double left   = this.boundsNode == null ? this.boundsX : this.boundsNode.getAbsoluteX();
		final double top    = this.boundsNode == null ? this.boundsY : this.boundsNode.getAbsoluteY();
		final double width  = this.boundsNode == null ? this.boundsWidth : this.boundsNode.getWidth();
		final double height = this.boundsNode == null ? this.boundsHeight : this.boundsNode.getHeight();
		final double x = this.toDesignX(mouseX);
		final double y = this.toDesignY(mouseY);
		return x < left || y < top || x >= left + width || y >= top + height;
	}

	public @NonNull ItemStack getRenderStack(final @NonNull Slot slot) {
		final ItemStack carried = super.menu.getCarried();
		if (!super.isQuickCrafting || carried.isEmpty() || !super.quickCraftSlots.contains(slot)) {
			return slot.getItem();
		}

		if (super.quickCraftSlots.size() == 1) {
			return ItemStack.EMPTY;
		}

		if (!AbstractContainerMenu.canItemQuickReplace(slot, carried, true) || !super.menu.canDragTo(slot)) {
			return slot.getItem();
		}

		final int maximum = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
		final int current = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
		return carried.copyWithCount(Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(super.quickCraftSlots.size(), this.craftType, carried) + current, maximum));
	}

	private double toDesignX(final double guiX) {
		final Window window = Minecraft.getInstance().getWindow();
		return this.ui.getRelativeX(guiX * window.getGuiScale() * this.getViewportWidth() / window.getWidth());
	}

	private double toDesignY(final double guiY) {
		final Window window = Minecraft.getInstance().getWindow();
		return this.ui.getRelativeY(guiY * window.getGuiScale() * this.getViewportHeight() / window.getHeight());
	}

	private double getViewportWidth() {
		return this.ui.getScaledWidth().getOrDefault() * this.ui.getZoomLevel().getOrDefault();
	}

	private double getViewportHeight() {
		return this.ui.getScaledHeight().getOrDefault() * this.ui.getZoomLevel().getOrDefault();
	}

	private Slot getHovered(final double mouseX, final double mouseY) {
		for (final Slot slot : super.menu.slots) {
			if (slot.isActive() && this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
				return slot;
			}
		}

		return null;
	}

	private void refresh() {
		this.nodeMap.clear();
		for (final Node node : this.ui.getNodeList().recursive()) {
			if (node instanceof final SlotNode slot) {
				this.nodeMap.put(JOIDMenuScreen.key(slot.getSlot().x, slot.getSlot().y), slot);
			}
		}
	}

	private static long key(final int x, final int y) {
		return (long) x << 32 | y & 0xFFFFFFFFL;
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		ScreenBridge.inst().mousePressed(event.button());
		this.forwarded = this.getHovered(event.x(), event.y()) != null || this.hasClickedOutside(event.x(), event.y(), super.leftPos, super.topPos);
		if (!this.forwarded) {
			return true;
		}

		if (!super.isQuickCrafting && !super.menu.getCarried().isEmpty()) {
			this.craftType = event.button() == 1 ? 1 : event.button() == 0 ? 0 : 2;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		ScreenBridge.inst().mouseReleased();
		if (!this.forwarded) {
			return true;
		}

		this.forwarded = false;
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
		ScreenBridge.inst().mouseDragged();
		if (!this.forwarded) {
			return true;
		}

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