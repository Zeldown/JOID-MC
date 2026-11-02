package dev.joid.backend.minecraft.bridge.ui.container;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.input.mouse.MouseButton;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerUIScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {

	private final ContainerUIBridge bridge;
	private final Set<Integer>      consumedButtons;

	@Getter
	private final ContainerUI<M> ui;

	public ContainerUIScreen(final M container, final Inventory inventory, final ContainerUI<M> ui) {
		super(container, inventory, ContainerUIScreen.toTitle(ui));
		this.bridge          = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		this.consumedButtons = new HashSet<>();
		this.ui              = ui;
	}

	@Override
	public void removed() {
		super.removed();
		this.bridge.removed(this);
	}

	@Override
	protected void init() {
		super.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		if (this.bridge.getScreen() != this) {
			this.bridge.added(this);
		}

		super.init();
		this.bridge.load();
		this.updateBounds();
	}

	@Override
	public void render(final PoseStack pose, final int mouseX, final int mouseY, final float partialTick) {
		if (this.hasBackground()) {
			super.renderBackground(pose);
		}

		if (!this.ui.getNodeList().contains(this.ui.getCarriedNode())) {
			this.ui.getCarriedNode().attach(this.ui);
		}

		this.updateBounds();
		this.updateQuickCraft();
		super.hoveredSlot = this.findSlot(mouseX, mouseY);
		this.bridge.frame(pose, mouseX, mouseY);
		super.renderTooltip(pose, mouseX, mouseY);
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
	public boolean mouseClicked(final double x, final double y, final int button) {
		if (this.bridge.mousePressed(MouseButton.of(button))) {
			this.consumedButtons.add(button);
			return true;
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseReleased(final double x, final double y, final int button) {
		final boolean consumed = this.bridge.mouseReleased(MouseButton.of(button));
		if (this.consumedButtons.remove(button)) {
			return true;
		}
		return super.mouseReleased(x, y, button) || consumed;
	}

	@Override
	public boolean mouseDragged(final double x, final double y, final int button, final double dragX, final double dragY) {
		return this.consumedButtons.contains(button) || super.mouseDragged(x, y, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(final double x, final double y, final double scroll) {
		return this.bridge.mouseScroll(0D, scroll) || super.mouseScrolled(x, y, scroll);
	}

	@Override
	public boolean keyPressed(final int key, final int scanCode, final int modifiers) {
		return this.bridge.keyPressed(GlfwKeys.getPhysicalKey(key), GlfwKeys.getKey(key)) || super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(final int key, final int scanCode, final int modifiers) {
		return this.bridge.keyReleased(GlfwKeys.getPhysicalKey(key), GlfwKeys.getKey(key)) || super.keyReleased(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(final char character, final int modifiers) {
		return SharedConstants.isAllowedChatCharacter(character) && this.bridge.charTyped(character) || super.charTyped(character, modifiers);
	}

	public Slot getHoveredSlot() {
		return super.hoveredSlot;
	}

	@Override
	protected Slot findSlot(final double x, final double y) {
		final SlotNode node = this.getSlotNode(x, y);
		return node != null && node.getSlot().isActive() ? node.getSlot() : null;
	}

	public SlotNode getHoveredSlotNode() {
		final Minecraft minecraft = Minecraft.getInstance();
		return this.getSlotNode(minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth(), minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
	}

	public boolean isQuickCrafting() {
		return super.isQuickCrafting;
	}

	public int getQuickCraftType() {
		return super.quickCraftingType;
	}

	public int getQuickCraftRemainder() {
		return super.quickCraftingRemainder;
	}

	public @NonNull Set<@NonNull Slot> getQuickCraftSlots() {
		return Collections.unmodifiableSet(super.quickCraftSlots);
	}

	@Override
	protected void renderBg(final PoseStack pose, final float partialTick, final int mouseX, final int mouseY) {}

	private UIDataMinecraft getMinecraftData() {
		return this.ui.getClass().getAnnotation(UIDataMinecraft.class);
	}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.getMinecraftData();
		return data == null || data.background();
	}

	private void updateBounds() {
		final double[] bounds = this.ui.getBounds();
		final double scale = Minecraft.getInstance().getWindow().getGuiScale();
		final int left = (int) Math.floor(this.ui.getView().toScreenX(bounds[0]) / scale);
		final int top = (int) Math.floor(this.ui.getView().toScreenY(bounds[1]) / scale);
		super.leftPos     = left;
		super.topPos      = top;
		super.imageWidth  = (int) Math.ceil(this.ui.getView().toScreenX(bounds[0] + bounds[2]) / scale) - left;
		super.imageHeight = (int) Math.ceil(this.ui.getView().toScreenY(bounds[1] + bounds[3]) / scale) - top;
	}

	private void updateQuickCraft() {
		final ItemStack carried = super.inventory.getCarried();
		if (!super.isQuickCrafting || carried.isEmpty() || super.quickCraftSlots.size() <= 1) {
			return;
		}

		if (super.quickCraftSlots.removeIf(slot -> slot.isActive() && (!AbstractContainerMenu.canItemQuickReplace(slot, carried, true) || !super.menu.canDragTo(slot)))) {
			super.recalculateQuickCraftRemaining();
		}
	}

	private SlotNode getSlotNode(final double x, final double y) {
		final double scale = Minecraft.getInstance().getWindow().getGuiScale();
		return this.bridge.getNodeAt(this.ui, x * scale, y * scale, SlotNode.class);
	}

	private static Component toTitle(final ContainerUI<?> ui) {
		final UIDataMinecraft data = ui.getClass().getAnnotation(UIDataMinecraft.class);
		return data == null || data.title().isEmpty() ? TextComponent.EMPTY : new TranslatableComponent(data.title());
	}

}