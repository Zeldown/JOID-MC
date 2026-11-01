package dev.joid.backend.minecraft.showcase.ui;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.shape.CircleNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.utils.align.Align;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@UIDataMinecraft(pause = false)
public class ShowQuests extends UI implements IShowFocus {

	private Node claim;

	@Override
	public void init() {
		RectNode
		.create(480, 200, 960, 680)
		.color(ShowTheme.vertical(ShowTheme.PANEL_TOP.withAlpha(0.86F), ShowTheme.PANEL_BOTTOM.withAlpha(0.92F)))
		.effect(RoundedNodeEffect.create(32F))
		.effect(BorderNodeEffect.create(Color.WHITE.withAlpha(0.1F), 1.5F).mode(BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.withAlpha(0.55F), 60F).offsetY(24D))
		.body(panel -> {
			TextNode.create(56, 48).text(Text.create("Daily Quests", ShowTheme.font(FontWeight.BOLD, 40F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(58, 104).text(Text.create("Complete quests to earn emeralds", ShowTheme.font(FontWeight.REGULAR, 18F, ShowTheme.MUTED))).attach(panel);
			RectNode
			.create(724, 50, 180, 44)
			.color(Color.WHITE.withAlpha(0.06F))
			.effect(RoundedNodeEffect.create(22F))
			.body(timer -> {
				TextNode.create(90, 22).text(Text.create("Resets in 04:12", ShowTheme.font(FontWeight.BOLD, 16F, ShowTheme.MUTED)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(timer);
			})
			.attach(panel);
			this.quest(panel, 160, Items.diamond, ShowTheme.DIAMOND, "Mine 16 Diamonds", 11, 16, 12);
			this.quest(panel, 292, Items.bread, ShowTheme.GOLD, "Bake 32 Bread", 32, 32, 6);
			this.quest(panel, 424, Items.iron_pickaxe, ShowTheme.AMETHYST, "Craft an Iron Pickaxe", 0, 1, 4);
			TextNode.create(56, 576).text(Text.create("WEEKLY STREAK", ShowTheme.font(FontWeight.BOLD, 13F, ShowTheme.FAINT))).attach(panel);
			for (int day = 0; day < 7; day++) {
				final boolean done = day < 5;
				CircleNode
				.create(56 + day * 40, 606, 28)
				.color(done ? ShowTheme.vertical(ShowTheme.GRASS, ShowTheme.EMERALD) : Color.WHITE.withAlpha(0.08F))
				.effect(done ? ShadowNodeEffect.create(ShowTheme.EMERALD.withAlpha(0.5F), 10F) : BorderNodeEffect.create(Color.WHITE.withAlpha(0.06F), 1F).mode(BorderMode.IN))
				.attach(panel);
			}
			TextNode.create(904, 610).text(Text.create("5 / 7 days", ShowTheme.font(FontWeight.BOLD, 16F, ShowTheme.MUTED)).alignX(Align.END)).anchorX(Align.END).attach(panel);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.claim;
	}

	private void quest(final Node panel, final double y, final Item item, final Color color, final String title, final int progress, final int goal, final int reward) {
		final boolean done = progress >= goal;
		RectNode
		.create(56, y, 848, 112)
		.color(Color.WHITE.withAlpha(0.05F))
		.effect(RoundedNodeEffect.create(20F))
		.effect(BorderNodeEffect.create(Color.WHITE.withAlpha(0.06F), 1F).mode(BorderMode.IN))
		.body(row -> {
			RectNode.create(20, 16, 80, 80).color(color.withAlpha(0.16F)).effect(RoundedNodeEffect.create(16F)).attach(row);
			ItemNode.create(28, 24, 64, 64).stack(new ItemStack(item)).attach(row);
			TextNode.create(124, 22).text(Text.create(title, ShowTheme.font(FontWeight.BOLD, 22F, ShowTheme.TEXT))).attach(row);
			RectNode
			.create(124, 68, 400, 10)
			.color(Color.WHITE.withAlpha(0.08F))
			.effect(RoundedNodeEffect.create(5F))
			.body(track -> {
				RectNode.create(0, 0, 400D * progress / goal, 10).color(ShowTheme.horizontal(ShowTheme.EMERALD, ShowTheme.GRASS)).visible(progress > 0).effect(RoundedNodeEffect.create(5F)).attach(track);
			})
			.attach(row);
			TextNode.create(540, 63).text(Text.create(progress + " / " + goal, ShowTheme.font(FontWeight.BOLD, 15F, ShowTheme.MUTED))).attach(row);
			RectNode
			.create(620, 32, 96, 48)
			.color(ShowTheme.EMERALD.withAlpha(0.14F))
			.effect(RoundedNodeEffect.create(24F))
			.body(chip -> {
				ItemNode.create(12, 8, 32, 32).stack(new ItemStack(Items.emerald)).attach(chip);
				TextNode.create(52, 24).text(Text.create(String.valueOf(reward), ShowTheme.font(FontWeight.BOLD, 20F, ShowTheme.TEXT))).anchorY(Align.CENTER).attach(chip);
			})
			.attach(row);
			final RectNode button = RectNode
			.create(732, 32, 96, 48)
			.color(done ? ShowTheme.horizontal(ShowTheme.GRASS, ShowTheme.EMERALD) : Color.WHITE.withAlpha(0.06F))
			.effect(RoundedNodeEffect.create(14F))
			.effect(done ? ShadowNodeEffect.create(ShowTheme.EMERALD.withAlpha(0.45F), 18F).offsetY(4D) : BorderNodeEffect.create(Color.WHITE.withAlpha(0.07F), 1F).mode(BorderMode.IN))
			.body(action -> {
				TextNode.create(48, 24).text(Text.create(done ? "Claim" : "Track", ShowTheme.font(FontWeight.BOLD, 17F, done ? ShowTheme.INK : ShowTheme.MUTED)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(action);
			})
			.attach(row);
			if (done) {
				this.claim = button;
			}
		})
		.attach(panel);
	}

}