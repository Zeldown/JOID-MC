package dev.joid.backend.minecraft.bridge.ui.container;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.ReflectionHelper;
import dev.joid.backend.minecraft.bridge.ui.TooltipRenderer;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.base.lwjgl2.input.Lwjgl2InputForwarder;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerUIScreen<C extends Container> extends GuiContainer {

	private static final Field HOVERED_SLOT   = ReflectionHelper.findField(GuiContainer.class, "theSlot", "field_147006_u");
	private static final Field DRAG_TYPE      = ReflectionHelper.findField(GuiContainer.class, "field_146987_F");
	private static final Field DRAG_REMAINDER = ReflectionHelper.findField(GuiContainer.class, "field_146996_I");

	private final ContainerUIBridge bridge;
	private final Set<Integer>      consumedButtons;

	@Getter
	private final ContainerUI<C> ui;

	@Getter
	private Slot hoveredSlot;

	public ContainerUIScreen(final ContainerUI<C> ui) {
		super(ui.getContainer());
		this.bridge          = BridgeHandler.UI.getBridge(ContainerUIBridge.class);
		this.consumedButtons = new HashSet<>();
		this.ui              = ui;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (this.bridge.getScreen() != this) {
			this.bridge.added(this);
		}

		this.bridge.load();
		this.updateBounds();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		this.bridge.removed(this);
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		if (!this.ui.getNodeList().contains(this.ui.getCarriedNode())) {
			this.ui.getCarriedNode().attach(this.ui);
		}

		this.updateBounds();
		if (this.hasBackground()) {
			super.drawDefaultBackground();
		}

		final SlotNode node = this.getHoveredSlotNode();
		this.hoveredSlot = node != null && node.getSlot().func_111238_b() ? node.getSlot() : null;
		ContainerUIScreen.write(ContainerUIScreen.HOVERED_SLOT, this, this.hoveredSlot);
		this.bridge.frame(null, mouseX, mouseY);
		if (super.mc.thePlayer.inventory.getItemStack() == null && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
			TooltipRenderer.draw(this.hoveredSlot.getStack(), mouseX, mouseY);
		}
	}

	@Override
	public void handleMouseInput() {
		final int button = Mouse.getEventButton();
		final boolean pressed = Mouse.getEventButtonState();
		final boolean moved = button == -1 && Mouse.getEventDWheel() == 0;
		final boolean consumed = Lwjgl2InputForwarder.forwardMouseEvent();
		if (button != -1 && !pressed && this.consumedButtons.remove(button)) {
			return;
		}

		if (button != -1 && pressed && consumed) {
			this.consumedButtons.add(button);
			return;
		}

		if (!consumed || moved || button != -1 && !pressed) {
			super.handleMouseInput();
		}
	}

	@Override
	public void handleKeyboardInput() {
		if (!Lwjgl2InputForwarder.forwardKeyboardEvent()) {
			super.handleKeyboardInput();
		}
	}

	public SlotNode getHoveredSlotNode() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		return this.bridge.getNodeAt(this.ui, Mouse.getX(), minecraft.displayHeight - Mouse.getY() - 1, SlotNode.class);
	}

	public boolean isQuickCrafting() {
		return super.field_147007_t;
	}

	public int getQuickCraftType() {
		return ContainerUIScreen.read(ContainerUIScreen.DRAG_TYPE, this);
	}

	public int getQuickCraftRemainder() {
		return ContainerUIScreen.read(ContainerUIScreen.DRAG_REMAINDER, this);
	}

	public @NonNull Set<@NonNull Slot> getQuickCraftSlots() {
		return Collections.unmodifiableSet(super.field_147008_s);
	}

	@Override
	protected void keyTyped(final char character, final int code) {
		if (code != Keyboard.KEY_ESCAPE) {
			super.keyTyped(character, code);
		}
	}

	@Override
	protected boolean func_146978_c(final int left, final int top, final int width, final int height, final int mouseX, final int mouseY) {
		final SlotNode node = this.getHoveredSlotNode();
		return node != null && node.getSlot().xDisplayPosition == left && node.getSlot().yDisplayPosition == top;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {}

	private boolean hasBackground() {
		final UIDataMinecraft data = this.ui.getClass().getAnnotation(UIDataMinecraft.class);
		return data == null || data.background();
	}

	private void updateBounds() {
		final double[] bounds = this.ui.getBounds();
		final Minecraft minecraft = Minecraft.getMinecraft();
		final int scale = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor();
		final int left = (int) Math.floor(this.ui.getView().toScreenX(bounds[0]) / scale);
		final int top = (int) Math.floor(this.ui.getView().toScreenY(bounds[1]) / scale);
		super.guiLeft = left;
		super.guiTop  = top;
		super.xSize   = (int) Math.ceil(this.ui.getView().toScreenX(bounds[0] + bounds[2]) / scale) - left;
		super.ySize   = (int) Math.ceil(this.ui.getView().toScreenY(bounds[1] + bounds[3]) / scale) - top;
	}

	private static int read(final Field field, final Object target) {
		try {
			return field.getInt(target);
		} catch (final IllegalAccessException exception) {
			throw new IllegalStateException("The field " + field.getName() + " of GuiContainer cannot be read", exception);
		}
	}

	private static void write(final Field field, final Object target, final Object value) {
		try {
			field.set(target, value);
		} catch (final IllegalAccessException exception) {
			throw new IllegalStateException("The field " + field.getName() + " of GuiContainer cannot be written", exception);
		}
	}

}