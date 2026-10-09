package dev.joid.backend.minecraft.bridge.ui.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.utils.click.ClickType;
import dev.joid.lib.utils.key.Key;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerUIScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {

	private final ContainerUIBridge bridge;
	private final Set<Integer>      consumedButtons;

	@Getter
	private final ContainerUI<M> ui;

	protected ContainerUIScreen(final M container, final Inventory inventory, final Component title, final ContainerUI<M> ui) {
		super(container, inventory, title);
		this.bridge          = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		this.consumedButtons = new HashSet<>();
		this.ui              = ui;
	}

	public static <M extends AbstractContainerMenu> @NonNull ContainerUIScreen<M> create(final @NonNull M container, final @NonNull Inventory inventory, final @NonNull Component title, final @NonNull ContainerUI<M> ui) {
		return new ContainerUIScreen<>(container, inventory, title, ui);
	}

	public static <M extends AbstractContainerMenu> @NonNull ScreenConstructor<M, ContainerUIScreen<M>> constructor(final @NonNull Function<M, ? extends ContainerUI<M>> ui) {
		return (container, inventory, title) -> ContainerUIScreen.create(container, inventory, title, ui.apply(container));
	}

	@Override
	public void added() {
		this.bridge.added(this);
	}

	@Override
	public void removed() {
		super.removed();
		this.bridge.removed(this);
	}

	@Override
	protected void init() {
		super.init();
		this.bridge.load();
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		this.updateQuickCraft();
		final Slot previous = super.hoveredSlot;
		super.hoveredSlot = this.getHoveredSlot(mouseX, mouseY);
		if (previous != null && previous != super.hoveredSlot) {
			super.onStopHovering(previous);
		}

		this.bridge.frame(graphics, mouseX, mouseY);
		super.extractTooltip(graphics, mouseX, mouseY);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void mouseMoved(final double x, final double y) {
		this.bridge.mouseMoved();
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		if (this.bridge.mousePressed(ClickType.from(event.button()))) {
			this.consumedButtons.add(event.button());
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
		return this.consumedButtons.contains(event.button()) || super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(final MouseButtonEvent event) {
		final boolean consumed = this.bridge.mouseReleased(ClickType.from(event.button()));
		if (this.consumedButtons.remove(event.button())) {
			return true;
		}
		return super.mouseReleased(event) || consumed;
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		return this.bridge.mouseScroll(scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		return this.bridge.keyTyped((char) 0, GlfwKeys.getKey(event.key())) || super.keyPressed(event);
	}

	@Override
	public boolean charTyped(final CharacterEvent event) {
		boolean consumed = false;
		for (final char c : Character.toChars(event.codepoint())) {
			consumed |= this.bridge.keyTyped(c, Key.UNKNOWN);
		}
		return consumed;
	}

	public boolean isQuickCrafting() {
		return super.isQuickCrafting;
	}

	public Slot getHoveredSlot() {
		return super.hoveredSlot;
	}

	public SlotNode getHoveredSlotNode() {
		final Minecraft minecraft = Minecraft.getInstance();
		return this.getSlotNode(minecraft.mouseHandler.getScaledXPos(minecraft.getWindow()), minecraft.mouseHandler.getScaledYPos(minecraft.getWindow()));
	}

	public @NonNull Set<@NonNull Slot> getQuickCraftSlots() {
		return Collections.unmodifiableSet(super.quickCraftSlots);
	}

	public int getQuickCraftType() {
		return super.quickCraftingType;
	}

	public int getQuickCraftRemainder() {
		return super.quickCraftingRemainder;
	}

	@Override
	protected Slot getHoveredSlot(final double x, final double y) {
		final SlotNode node = this.getSlotNode(x, y);
		return node != null && node.getSlot() != null && node.getSlot().isActive() ? node.getSlot() : null;
	}

	@Override
	protected boolean hasClickedOutside(final double x, final double y, final int left, final int top) {
		return this.getNode(x, y) == null;
	}

	private void updateQuickCraft() {
		final ItemStack carried = super.menu.getCarried();
		if (!super.isQuickCrafting || carried.isEmpty() || super.quickCraftSlots.size() <= 1) {
			return;
		}

		if (super.quickCraftSlots.removeIf(slot -> slot.isActive() && (!AbstractContainerMenu.canItemQuickReplace(slot, carried, true) || !super.menu.canDragTo(slot)))) {
			super.recalculateQuickCraftRemaining();
		}
	}

	private SlotNode getSlotNode(final double x, final double y) {
		for (Node node = this.getNode(x, y); node != null; node = node.getParent()) {
			if (node instanceof final SlotNode slotNode) {
				return slotNode;
			}
		}
		return null;
	}

	private Node getNode(final double x, final double y) {
		final Window window = Minecraft.getInstance().getWindow();
		final List<UI> uiList = new ArrayList<>(this.bridge.getUiList().ordered());
		for (int index = uiList.size() - 1; index >= 0; index--) {
			final UI ui = uiList.get(index);
			if (ui != this.ui && (!ui.getData().active() || !ui.getData().visible())) {
				continue;
			}

			final Node hovered = ContainerUIScreen.getNode(ui, x * window.getGuiScale(), y * window.getGuiScale());
			if (hovered != null || ui == this.ui || ui.getPopup().active()) {
				return hovered;
			}
		}
		return null;
	}

	private static Node getNode(final UI ui, final double x, final double y) {
		final double uiX = ui.getView().toUiX(x);
		final double uiY = ui.getView().toUiY(y);
		for (final Node node : ui.getNodeList().reversed()) {
			final Node hovered = node.getHoveredNode(uiX, uiY);
			if (hovered != null) {
				return hovered;
			}
		}
		return null;
	}

}