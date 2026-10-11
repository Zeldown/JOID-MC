package dev.joid.backend.minecraft.showcase.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.impl.design.shape.CircleNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ChestMenu;

@UIData(background = false)
@UIDataMinecraft(title = "container.chest")
public class ShowChest extends ContainerUI<ChestMenu> implements IShowFocus {

	private final List<SlotNode> slotNodes;

	private double start;

	public ShowChest(final ChestMenu container) {
		super(container);

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
		.body(panel -> {
			ShowTheme
			.tile(40, 40, 300, 720, 22F)
			.body(card -> {
				CircleNode.create(30, 90, 240).color(ShowTheme.GRASS.copyAlpha(0.20F)).effect(BlurNodeEffect.create(40F)).interactive(false).attach(card);
				EntityNode.create(30, 50, 240, 420).entity(Minecraft.getInstance().player).rotationYaw(() -> -25D + this.elapsed() * 12D).scale(0.9D).attach(card);
				TextNode.create(150, 520).text(Text.create(Minecraft.getInstance().player.getName().getString(), ShowTheme.font(FontWeight.EXTRA_BOLD, 28F, ShowTheme.TEXT), Align.CENTER)).anchorX(Align.CENTER).attach(card);
				TextNode.create(150, 562).text(Text.create(() -> "Survival · Level " + Minecraft.getInstance().player.experienceLevel, ShowTheme.font(FontWeight.MEDIUM, 16F, ShowTheme.MUTED), Align.CENTER)).anchorX(Align.CENTER).attach(card);
				this.stat(card, 612, "Health", ShowTheme.REDSTONE, () -> Minecraft.getInstance().player.getHealth() / 20D);
				this.stat(card, 666, "Hunger", ShowTheme.GOLD, () -> Minecraft.getInstance().player.getFoodData().getFoodLevel() / 20D);
			})
			.attach(panel);
			TextNode.create(380, 44).text(Text.create("Chest", ShowTheme.font(FontWeight.EXTRA_BOLD, 34F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(1160, 62).text(Text.create(() -> this.countItems() + " / 27 slots", ShowTheme.font(FontWeight.SEMI_BOLD, 16F, ShowTheme.MUTED), Align.END)).anchorX(Align.END).attach(panel);
			this.grid(panel, 0, 100, 3);
			TextNode.create(380, 388).text(Text.create("INVENTORY", ShowTheme.label(14F, ShowTheme.FAINT))).attach(panel);
			RectNode.create(500, 396, 660, 1).color(Color.WHITE.copyAlpha(0.08F)).attach(panel);
			this.grid(panel, 27, 420, 3);
			this.grid(panel, 54, 686, 1);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.getSlotNode(0);
	}

	public SlotNode getSlotNode(final int index) {
		return this.slotNodes.stream().filter(node -> node.getSlot().index == index).findFirst().orElseThrow();
	}

	private void grid(final Node panel, final int first, final double y, final int rows) {
		for (int index = 0; index < rows * 9; index++) {
			final int slot = first + index;
			final boolean hotbar = first == 54;
			final double x = 380D + index % 9 * 87D;
			final double top = y + index / 9 * 87D;
			ShowTheme
			.tile(x, top, 81, 81, 16F)
			.hoveredColor(Color.WHITE.copyAlpha(0.16F))
			.body(tile -> {
				if (hotbar) {
					RectNode.create(0, 0, 81, 81).color(ShowTheme.GRASS.copyAlpha(0.14F)).visible(() -> Minecraft.getInstance().player.getInventory().getSelectedSlot() == slot - 54).effect(RoundedNodeEffect.create(16F)).interactive(false).attach(tile);
				}

				final SlotNode node = SlotNode.create(4, 4, 73, 73).slot(super.getContainer().getSlot(slot)).hoveredColor((Color) null);
				this.slotNodes.add(node);
				node.attach(tile);
			})
			.attach(panel);
		}
	}

	private void stat(final Node card, final double y, final String name, final Color color, final Supplier<Double> ratio) {
		TextNode.create(28, y).text(Text.create(name, ShowTheme.font(FontWeight.SEMI_BOLD, 15F, ShowTheme.MUTED))).attach(card);
		RectNode
		.create(28, y + 26, 244, 8)
		.color(Color.WHITE.copyAlpha(0.08F))
		.effect(RoundedNodeEffect.create(4F))
		.body(track -> {
			RectNode.create(0, 0, 0, 8).color(ShowTheme.horizontal(color, color.copyAlpha(0.6F))).width(() -> 244D * ratio.get() * ShowTheme.ease(this.elapsed(), 0.2D, 1D)).effect(RoundedNodeEffect.create(4F)).attach(track);
		})
		.attach(card);
	}

	private int countItems() {
		int count = 0;
		for (int index = 0; index < 27; index++) {
			if (!super.getContainer().getSlot(index).getItem().isEmpty()) {
				count++;
			}
		}
		return count;
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

}