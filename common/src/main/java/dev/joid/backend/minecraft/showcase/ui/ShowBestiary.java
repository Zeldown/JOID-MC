package dev.joid.backend.minecraft.showcase.ui;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.TextMode;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.design.shape.CircleNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.ui.node.impl.structure.container.ContainerNode;
import dev.joid.lib.utils.align.Align;
import lombok.Getter;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@UIDataMinecraft(title = "Bestiary", pause = false)
public class ShowBestiary extends UI implements IShowFocus {

	private static final List<Entry> ENTRIES = List.of(
		new Entry("Fox", "MOB", ShowTheme.GRASS, "Taiga", "Sleeps by day, hunts chickens at night and steals whatever you drop.", "10", size -> EntityNode.create(0, 10 * size, 300 * size, 210 * size).type(EntityTypes.FOX)),
		new Entry("Axolotl", "MOB", ShowTheme.GRASS, "Lush caves", "Plays dead to heal and helps you fight the drowned underwater.", "14", size -> EntityNode.create(0, 10 * size, 300 * size, 210 * size).type(EntityTypes.AXOLOTL)),
		new Entry("Allay", "MOB", ShowTheme.GRASS, "Pillager outposts", "Brings you every copy of the item you give it, and dances to music.", "20", size -> EntityNode.create(0, 10 * size, 300 * size, 210 * size).type(EntityTypes.ALLAY)),
		new Entry("Creeper", "MOB", ShowTheme.GRASS, "Everywhere at night", "Silent, patient and explosive. Keep your distance.", "20", size -> EntityNode.create(0, 10 * size, 300 * size, 210 * size).type(EntityTypes.CREEPER)),
		new Entry("Diamond Ore", "BLOCK", ShowTheme.DIAMOND, "Deep slate layers", "The rarest ore of the Overworld, mined with an iron pickaxe or better.", "3", size -> BlockNode.create(80 * size, 45 * size, 140 * size, 140 * size).block(Blocks.DIAMOND_ORE.defaultBlockState())),
		new Entry("Bookshelf", "BLOCK", ShowTheme.DIAMOND, "Villages, strongholds", "Fifteen of them around an enchanting table unlock its best offers.", "1.5", size -> BlockNode.create(80 * size, 45 * size, 140 * size, 140 * size).block(Blocks.BOOKSHELF.defaultBlockState())),
		new Entry("Trident", "ITEM", ShowTheme.AMETHYST, "Drowned", "Thrown like a spear, it returns with Loyalty and calls lightning with Channeling.", "9", size -> ItemNode.create(80 * size, 45 * size, 140 * size, 140 * size).stack(new ItemStack(Items.TRIDENT)).glint(true)),
		new Entry("Totem", "ITEM", ShowTheme.AMETHYST, "Evokers", "Held in hand, it saves you from death once.", "1", size -> ItemNode.create(86 * size, 51 * size, 128 * size, 128 * size).stack(new ItemStack(Items.TOTEM_OF_UNDYING)))
	);

	private final Node[] cards;

	private double start;

	@Getter
	private int selected;

	public ShowBestiary() {
		this.cards = new Node[ShowBestiary.ENTRIES.size()];
	}

	@Override
	public void init() {
		this.start    = ShowTheme.now();
		this.selected = 0;

		TextNode.create(120, 96).text(Text.create("Bestiary", ShowTheme.font(FontWeight.EXTRA_BOLD, 64F, ShowTheme.TEXT))).attach(this);

		TextNode.create(124, 180).text(Text.create("Creatures, blocks and treasures of the Overworld", ShowTheme.font(FontWeight.MEDIUM, 22F, ShowTheme.MUTED))).attach(this);

		this.tabs();

		for (int index = 0; index < ShowBestiary.ENTRIES.size(); index++) {
			this.card(index);
		}

		this.detail();
	}

	@Override
	public Node getFocus() {
		return this.cards[0];
	}

	public Node getCard(final int index) {
		return this.cards[index];
	}

	private void tabs() {
		final String[] names = {"All  8", "Mobs  4", "Blocks  2", "Items  2"};
		double x = 120D;
		for (int index = 0; index < names.length; index++) {
			final String name = names[index];
			final TextInfo info = ShowTheme.font(FontWeight.BOLD, 17F, index == 0 ? ShowTheme.INK : ShowTheme.MUTED);
			final double width = info.getWidth(name) + 44D;
			RectNode
			.create(x, 238, width, 44)
			.color(index == 0 ? ShowTheme.horizontal(ShowTheme.GRASS, ShowTheme.EMERALD) : Color.WHITE.copyAlpha(0.07F))
			.effect(RoundedNodeEffect.create(22F))
			.body(tab -> {
				TextNode.create(width / 2D, 22).text(Text.create(name, info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(tab);
			})
			.attach(this);
			x += width + 12D;
		}
	}

	private void card(final int index) {
		final Entry entry = ShowBestiary.ENTRIES.get(index);
		final double x = 120D + index % 4 * 318D;
		final double y = 318D + index / 4 * 344D;
		final double delay = 0.15D + index * 0.07D;
		this.cards[index] = RectNode
		.create(x, y, 300, 326)
		.color(ShowTheme.vertical(Color.decode("#1B2130").copyAlpha(0.88F), ShowTheme.INK.copyAlpha(0.92F)))
		.hoveredColor(ShowTheme.vertical(Color.decode("#232B3D").copyAlpha(0.92F), ShowTheme.INK.copyAlpha(0.94F)))
		.y(() -> y + 48D * (1D - ShowTheme.ease(this.elapsed(), delay, 0.7D)) - (this.selected == index ? 8D : 0D))
		.effect(RoundedNodeEffect.create(24F))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.09F), 1.5F, BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.copyAlpha(0.5F), 36F, 0D, 16D))
		.onHoverStart((_, _, _) -> this.selected = index)
		.body(card -> {
			CircleNode.create(70, 34, 160).color(entry.color().copyAlpha(0.22F)).effect(BlurNodeEffect.create(36F)).interactive(false).attach(card);
			final Node render = entry.render().apply(1D).interactive(false);
			if (render instanceof final EntityNode entity) {
				entity.rotationYaw(() -> -35D + this.elapsed() * 20D + index * 15D);
			} else if (render instanceof final BlockNode block) {
				block.rotationYaw(() -> 30D + this.elapsed() * 30D + index * 25D);
			}
			render.attach(card);
			TextNode.create(24, 234).text(Text.create(entry.name(), ShowTheme.font(FontWeight.BOLD, 24F, ShowTheme.TEXT))).attach(card);
			TextNode.create(24, 272).text(Text.create(entry.habitat(), ShowTheme.font(FontWeight.MEDIUM, 15F, ShowTheme.MUTED))).attach(card);
			final TextInfo chip = ShowTheme.label(12F, entry.color());
			final double width = chip.getWidth(entry.kind()) + 24D;
			RectNode
			.create(276 - width, 236, width, 28)
			.color(entry.color().copyAlpha(0.14F))
			.effect(RoundedNodeEffect.create(14F))
			.body(tag -> {
				TextNode.create(width / 2D, 14).text(Text.create(entry.kind(), chip, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(tag);
			})
			.attach(card);
		})
		.attach(this);
	}

	private void detail() {
		ShowTheme
		.panel(1410, 96, 390, 892)
		.x(() -> 1410D + 60D * (1D - ShowTheme.ease(this.elapsed(), 0.3D, 0.8D)))
		.body(panel -> {
			CircleNode.create(55, 70, 280).color(() -> this.entry().color().copyAlpha(0.24F)).effect(BlurNodeEffect.create(50F)).interactive(false).attach(panel);
			for (int index = 0; index < ShowBestiary.ENTRIES.size(); index++) {
				final int entry = index;
				final Node render = ShowBestiary.ENTRIES.get(index).render().apply(1.3D).interactive(false);
				if (render instanceof final EntityNode entity) {
					entity.rotationYaw(() -> -40D + this.elapsed() * 20D);
				} else if (render instanceof final BlockNode block) {
					block.rotationYaw(() -> 30D + this.elapsed() * 30D);
				}
				ContainerNode.create(0, 40, 390, 290).visible(() -> this.selected == entry).interactive(false).body(box -> render.attach(box)).attach(panel);
			}
			TextNode.create(36, 420).text(Text.create(() -> this.entry().kind(), ShowTheme.label(13F, ShowTheme.FAINT))).attach(panel);
			TextNode.create(36, 446).text(Text.create(() -> this.entry().name(), ShowTheme.font(FontWeight.EXTRA_BOLD, 40F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(36, 508, 318, 0).text(Text.create(() -> this.entry().description(), ShowTheme.font(FontWeight.MEDIUM, 18F, ShowTheme.MUTED).lineHeight(1.5F))).mode(TextMode.SPLIT).attach(panel);
			this.fact(panel, 640, "HABITAT", () -> this.entry().habitat());
			this.fact(panel, 712, "HEALTH", () -> this.entry().health() + " hearts");
			RectNode
			.create(36, 800, 318, 60)
			.color(ShowTheme.horizontal(ShowTheme.GRASS, ShowTheme.EMERALD))
			.effect(RoundedNodeEffect.create(18F))
			.effect(ShadowNodeEffect.create(ShowTheme.EMERALD.copyAlpha(0.45F), 24F, 0D, 6D))
			.body(button -> {
				TextNode.create(159, 30).text(Text.create("Track", ShowTheme.font(FontWeight.EXTRA_BOLD, 20F, ShowTheme.INK), Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
			})
			.attach(panel);
		})
		.attach(this);
	}

	private void fact(final Node panel, final double y, final String label, final Supplier<String> value) {
		TextNode.create(36, y).text(Text.create(label, ShowTheme.label(12F, ShowTheme.FAINT))).attach(panel);
		TextNode.create(36, y + 22).text(Text.create(value, ShowTheme.font(FontWeight.SEMI_BOLD, 20F, ShowTheme.TEXT))).attach(panel);
	}

	private Entry entry() {
		return ShowBestiary.ENTRIES.get(this.selected);
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

	private record Entry(String name, String kind, Color color, String habitat, String description, String health, Function<Double, Node> render) {}

}