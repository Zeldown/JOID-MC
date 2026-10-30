package dev.joid.backend.minecraft.showcase.ui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.signal.impl.primitive.StringSignal;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.impl.shape.CircleNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.utils.align.Align;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;

@UIDataMinecraft
public class ShowAnvil extends ContainerUI<ContainerRepair> implements IShowFocus {

	public static final int INPUT_SLOT      = 0;
	public static final int ADDITIONAL_SLOT = 1;
	public static final int RESULT_SLOT     = 2;

	private static final ItemStack EMPTY = new ItemStack((Item) null);

	private final StringSignal   name;
	private final List<SlotNode> slotNodes;

	private ShowFieldNode field;
	private ItemStack     input;

	private double start;

	public ShowAnvil(final ContainerRepair container) {
		super(container);

		this.name      = new StringSignal("");
		this.slotNodes = new ArrayList<>();
	}

	@Override
	public void init() {
		this.start = ShowTheme.now();
		this.slotNodes.clear();

		ShowTheme
		.panel(360, 140, 1200, 800)
		.y(() -> 140D + 40D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.6D)))
		.self(super::bounds)
		.onUpdate(event -> this.follow())
		.body(panel -> {
			this.preview(panel);
			TextNode.create(380, 44).text(Text.create("Anvil", ShowTheme.font(FontWeight.EXTRA_BOLD, 34F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(1160, 62).text(Text.create(() -> "Level " + Minecraft.getMinecraft().thePlayer.experienceLevel, ShowTheme.font(FontWeight.SEMI_BOLD, 16F, ShowTheme.GRASS)).alignX(Align.END)).anchorX(Align.END).attach(panel);
			TextNode.create(380, 104).text(Text.create("ITEM NAME", ShowTheme.label(14F, ShowTheme.FAINT))).attach(panel);
			this.field = ShowFieldNode
			.create(380, 128, 780, ShowTheme.GRASS)
			.info(TextInfo.create(MinecraftFont.DEFAULT, 24F).color(ShowTheme.TEXT).shadow(3F, 3F))
			.marginHorizontal(24D)
			.maxTextLength(30)
			.focused(true)
			.text(this.name)
			.onChange(event -> this.rename(event.getText()))
			.attach(panel);
			TextNode.create(382, 210).text(() -> Text.create(this.hint(), ShowTheme.font(FontWeight.SEMI_BOLD, 15F, this.hintColor()))).attach(panel);
			this.tile(panel, ShowAnvil.INPUT_SLOT, 380);
			TextNode.create(531, 296).text(Text.create("+", TextInfo.create(MinecraftFont.DEFAULT, 32F).color(ShowTheme.FAINT)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(panel);
			this.tile(panel, ShowAnvil.ADDITIONAL_SLOT, 578);
			TextNode.create(729, 296).text(Text.create("\u2192", TextInfo.create(MinecraftFont.DEFAULT, 32F).color(ShowTheme.FAINT)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(panel);
			this.tile(panel, ShowAnvil.RESULT_SLOT, 776);
			TextNode.create(380, 388).text(Text.create("INVENTORY", ShowTheme.label(14F, ShowTheme.FAINT))).attach(panel);
			RectNode.create(500, 396, 660, 1).color(Color.WHITE.withAlpha(0.08F)).attach(panel);
			this.grid(panel, 3, 420, 3);
			this.grid(panel, 30, 686, 1);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.field;
	}

	public SlotNode getSlotNode(final int index) {
		return this.slotNodes.stream().filter(node -> node.getSlot().slotNumber == index).findFirst().orElseThrow(() -> new IllegalStateException("The anvil has no slot " + index));
	}

	private void preview(final Node panel) {
		ShowTheme
		.tile(40, 40, 300, 720, 22F)
		.body(card -> {
			TextNode.create(28, 32).text(Text.create("PREVIEW", ShowTheme.label(13F, ShowTheme.FAINT))).attach(card);
			CircleNode.create(40, 100, 220).color(ShowTheme.DIAMOND.withAlpha(0.20F)).effect(BlurNodeEffect.create(40F)).interactive(false).attach(card);
			ItemNode.create(70, 130, 160, 160).stack(this::item).glint(true).interactive(false).y(() -> 130D + 6D * Math.sin(this.elapsed() * 2D)).attach(card);
			TextNode.create(150, 360).text(Text.create(this::title, TextInfo.create(MinecraftFont.DEFAULT, 16F).color(ShowTheme.TEXT).shadow(2F, 2F)).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(card);
			TextNode.create(150, 400).text(Text.create("\u00A77Sharpness V", TextInfo.create(MinecraftFont.DEFAULT, 16F).color(ShowTheme.TEXT).shadow(2F, 2F)).alignX(Align.CENTER)).anchorX(Align.CENTER).visible(() -> this.item().getItem() != null).attach(card);
			RectNode.create(28, 480, 244, 1).color(Color.WHITE.withAlpha(0.08F)).attach(card);
			TextNode.create(28, 504).text(Text.create("COST", ShowTheme.label(12F, ShowTheme.FAINT))).attach(card);
			TextNode.create(28, 528).text(() -> Text.create(this.cost(), ShowTheme.font(FontWeight.EXTRA_BOLD, 26F, super.getContainer().maximumCost >= 40 ? ShowTheme.REDSTONE : ShowTheme.TEXT))).attach(card);
			RectNode
			.create(28, 640, 244, 60)
			.color(() -> this.isReady() ? ShowTheme.horizontal(ShowTheme.GRASS, ShowTheme.EMERALD) : Color.WHITE.withAlpha(0.07F))
			.effect(RoundedNodeEffect.create(18F))
			.body(button -> {
				TextNode.create(122, 30).text(() -> Text.create(this.isReady() ? "Take the result" : "Rename to forge", ShowTheme.font(FontWeight.EXTRA_BOLD, 18F, this.isReady() ? ShowTheme.INK : ShowTheme.FAINT)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
			})
			.attach(card);
		})
		.attach(panel);
	}

	private void tile(final Node panel, final int slot, final double x) {
		ShowTheme
		.tile(x, 244, 104, 104, 20F)
		.hoveredColor(Color.WHITE.withAlpha(0.16F))
		.body(tile -> {
			final SlotNode node = SlotNode.create(6, 6, 92, 92).slot(super.getContainer().getSlot(slot)).hoveredColor((Color) null);
			this.slotNodes.add(node);
			node.attach(tile);
		})
		.attach(panel);
	}

	private void grid(final Node panel, final int first, final double y, final int rows) {
		for (int index = 0; index < rows * 9; index++) {
			final int slot = first + index;
			final boolean hotbar = first == 30;
			final double x = 380D + index % 9 * 87D;
			final double top = y + index / 9 * 87D;
			ShowTheme
			.tile(x, top, 81, 81, 16F)
			.hoveredColor(Color.WHITE.withAlpha(0.16F))
			.body(tile -> {
				if (hotbar) {
					RectNode.create(0, 0, 81, 81).color(ShowTheme.GRASS.withAlpha(0.14F)).visible(() -> Minecraft.getMinecraft().thePlayer.inventory.currentItem == slot - 30).effect(RoundedNodeEffect.create(16F)).interactive(false).attach(tile);
				}

				final SlotNode node = SlotNode.create(4, 4, 73, 73).slot(super.getContainer().getSlot(slot)).hoveredColor((Color) null);
				this.slotNodes.add(node);
				node.attach(tile);
			})
			.attach(panel);
		}
	}

	private void follow() {
		final ItemStack stack = super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack();
		if (stack != this.input) {
			this.input = stack;
			this.name.set(stack == null ? "" : stack.getDisplayName());
		}
	}

	private void rename(final String text) {
		final Slot slot = super.getContainer().getSlot(ShowAnvil.INPUT_SLOT);
		if (!slot.getHasStack()) {
			return;
		}

		final String name = !slot.getStack().hasDisplayName() && text.equals(slot.getStack().getDisplayName()) ? "" : text;
		super.getContainer().updateItemName(name);
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", name.getBytes(StandardCharsets.UTF_8)));
	}

	private ItemStack item() {
		final ItemStack result = super.getContainer().getSlot(ShowAnvil.RESULT_SLOT).getStack();
		final ItemStack input = super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack();
		return result != null ? result : input != null ? input : ShowAnvil.EMPTY;
	}

	private String title() {
		final ItemStack input = super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack();
		if (input == null) {
			return "\u00A77No item";
		}

		return this.name.get().trim().isEmpty() ? "\u00A77\u00A7o" + input.getDisplayName() : "\u00A7b\u00A7o" + this.name.get();
	}

	private String hint() {
		if (super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack() == null) {
			return "Place an item to rename it";
		}

		if (this.name.get().trim().isEmpty()) {
			return "A name can't be empty";
		}

		return this.isRenamed() ? this.name.get().length() + " / 30 characters, ready to forge" : "Double-click a word to rename it";
	}

	private Color hintColor() {
		if (super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack() == null) {
			return ShowTheme.MUTED;
		}

		return this.name.get().trim().isEmpty() ? ShowTheme.REDSTONE : this.isRenamed() ? ShowTheme.GRASS : ShowTheme.MUTED;
	}

	private String cost() {
		final int cost = super.getContainer().maximumCost;
		if (cost >= 40) {
			return "Too expensive!";
		}

		return cost <= 0 ? "None" : cost + (cost == 1 ? " level" : " levels");
	}

	private boolean isReady() {
		return super.getContainer().getSlot(ShowAnvil.RESULT_SLOT).getStack() != null;
	}

	private boolean isRenamed() {
		return !this.name.get().equals(super.getContainer().getSlot(ShowAnvil.INPUT_SLOT).getStack().getDisplayName());
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

}