package dev.joid.backend.minecraft.bridge.ui.container;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dev.joid.backend.minecraft.bridge.input.KeyRepeatFilter;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.base.lwjgl3.glfw.input.GlfwKeys;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.mouse.MouseButton;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerUIScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {

	private final ContainerUIBridge bridge;
	private final KeyRepeatFilter   keyRepeats;
	private final Set<Integer>      consumedButtons;

	@Getter
	private final ContainerUI<M> ui;

	public ContainerUIScreen(final M container, final Inventory inventory, final ContainerUI<M> ui) {
		super(container, inventory, ContainerUIScreen.toTitle(ui));
		this.bridge          = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		this.keyRepeats      = new KeyRepeatFilter();
		this.consumedButtons = new HashSet<>();
		this.ui              = ui;
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
		this.keyRepeats.reset();
		this.bridge.load();
		this.updateBounds();
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
		if (this.hasBackground()) {
			super.renderBackground(graphics, mouseX, mouseY, partialTick);
		}

		if (!this.ui.getNodeList().contains(this.ui.getCarriedNode())) {
			this.ui.getCarriedNode().attach(this.ui);
		}

		this.updateBounds();
		this.updateQuickCraft();
		super.hoveredSlot = this.findSlot(mouseX, mouseY);
		this.bridge.frame(graphics, mouseX, mouseY);
		super.renderTooltip(graphics, mouseX, mouseY);
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
	public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
		return this.bridge.mouseScroll(scrollX, scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(final int code, final int scanCode, final int modifiers) {
		final Key physicalKey = GlfwKeys.getPhysicalKey(code);
		final Key key = GlfwKeys.getKey(code);
		return (this.keyRepeats.press(physicalKey) ? this.bridge.keyPressed(physicalKey, key) : this.bridge.isKeyConsumed(key)) || super.keyPressed(code, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(final int code, final int scanCode, final int modifiers) {
		final Key physicalKey = GlfwKeys.getPhysicalKey(code);
		this.keyRepeats.release(physicalKey);
		return this.bridge.keyReleased(physicalKey, GlfwKeys.getKey(code)) || super.keyReleased(code, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(final char character, final int modifiers) {
		return StringUtil.isAllowedChatCharacter(character) && this.bridge.charTyped(character) || super.charTyped(character, modifiers);
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
	protected void renderBg(final GuiGraphics graphics, final float partialTick, final int mouseX, final int mouseY) {}

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
		final ItemStack carried = super.menu.getCarried();
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
		return data == null || data.title().isEmpty() ? Component.empty() : Component.translatable(data.title());
	}

}