package dev.joid.backend.minecraft.showcase.ui;

import java.util.Locale;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.internal.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.design.shape.CircleNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@UIData(background = false)
@UIDataMinecraft(title = "menu.game")
public class ShowPause extends UI implements IShowFocus {

	private final Node[] buttons;

	private double start;

	public ShowPause() {
		this.buttons = new Node[6];
	}

	@Override
	public void init() {
		this.start = ShowTheme.now();

		ShowTheme
		.panel(120, 120, 520, 840)
		.x(() -> 120D - 80D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.7D)))
		.body(panel -> {
			TextNode.create(48, 52).text(Text.create("SINGLEPLAYER", ShowTheme.label(13F, ShowTheme.GRASS))).attach(panel);
			TextNode.create(46, 76).text(Text.create(Component.translatable("menu.game").getString(), ShowTheme.font(FontWeight.EXTRA_BOLD, 48F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(48, 142).text(Text.create(this::place, ShowTheme.font(FontWeight.MEDIUM, 17F, ShowTheme.MUTED))).attach(panel);
			this.button(panel, 0, "menu.returnToGame", null, true, () -> JOID.close(this));
			this.button(panel, 1, "gui.advancements", new ItemStack(Items.KNOWLEDGE_BOOK), false, null);
			this.button(panel, 2, "gui.stats", new ItemStack(Items.WRITABLE_BOOK), false, null);
			this.button(panel, 3, "menu.options", new ItemStack(Items.COMPARATOR), false, () -> Minecraft.getInstance().gui.setScreen(new OptionsScreen(Minecraft.getInstance().gui.screen(), Minecraft.getInstance().options, true)));
			this.button(panel, 4, "menu.multiplayerOptions.button", new ItemStack(Items.ENDER_EYE), false, null);
			this.button(panel, 5, "menu.returnToMenu", new ItemStack(Items.OAK_DOOR), false, null);
			RectNode.create(48, 760, 424, 1).color(Color.WHITE.copyAlpha(0.08F)).attach(panel);
			TextNode.create(48, 784).text(Text.create("Press Esc to resume", ShowTheme.font(FontWeight.MEDIUM, 15F, ShowTheme.FAINT))).attach(panel);
		})
		.attach(this);

		CircleNode.create(1230, 240, 520).color(ShowTheme.GRASS.copyAlpha(0.16F)).effect(BlurNodeEffect.create(90F)).interactive(false).attach(this);

		EntityNode.create(1290, 230, 400, 720).entity(Minecraft.getInstance().player).rotationYaw(() -> 20D * Math.sin(this.elapsed() * 0.8D)).y(() -> 230D + 30D * (1D - ShowTheme.ease(this.elapsed(), 0.2D, 0.9D))).interactive(false).attach(this);

		RectNode
		.create(1365, 120, 250, 52)
		.color(ShowTheme.INK.copyAlpha(0.72F))
		.effect(RoundedNodeEffect.create(26F))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.12F), 1F, BorderMode.IN))
		.body(tag -> {
			CircleNode.create(22, 19, 14).color(ShowTheme.GRASS).effect(ShadowNodeEffect.create(ShowTheme.GRASS, 10F)).attach(tag);
			TextNode.create(140, 26).text(Text.create(Minecraft.getInstance().player.getName().getString(), ShowTheme.font(FontWeight.BOLD, 20F, ShowTheme.TEXT), Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(tag);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.buttons[0];
	}

	public Node getButton(final int index) {
		return this.buttons[index];
	}

	private void button(final Node panel, final int index, final String key, final ItemStack icon, final boolean primary, final Runnable action) {
		final double delay = 0.1D + index * 0.05D;
		this.buttons[index] = RectNode
		.create(48, 196 + index * 88, 424, 72)
		.color(primary ? ShowTheme.horizontal(ShowTheme.GRASS, ShowTheme.EMERALD) : Color.WHITE.copyAlpha(0.06F))
		.hoveredColor(primary ? ShowTheme.horizontal(ShowTheme.GRASS.copyAlpha(0.9F), ShowTheme.EMERALD.copyAlpha(0.9F)) : Color.WHITE.copyAlpha(0.13F))
		.x(() -> 48D - 40D * (1D - ShowTheme.ease(this.elapsed(), delay, 0.6D)))
		.cursor(Cursor.POINTER)
		.effect(RoundedNodeEffect.create(20F))
		.effect(primary ? ShadowNodeEffect.create(ShowTheme.EMERALD.copyAlpha(0.45F), 26F, 0D, 8D) : BorderNodeEffect.create(Color.WHITE.copyAlpha(0.07F), 1F, BorderMode.IN))
		.onClick((_, _, _, _) -> {
			if (action != null) {
				action.run();
			}
		})
		.body(button -> {
			if (icon != null) {
				ItemNode.create(24, 20, 32, 32).stack(icon).interactive(false).attach(button);
			}

			TextNode.create(icon != null ? 76 : 28, 36).text(Text.create(Component.translatable(key).getString(), ShowTheme.font(primary ? FontWeight.EXTRA_BOLD : FontWeight.SEMI_BOLD, 21F, primary ? ShowTheme.INK : ShowTheme.TEXT))).anchorY(Align.CENTER).attach(button);
		})
		.attach(panel);
	}

	private String place() {
		final Minecraft minecraft = Minecraft.getInstance();
		final BlockPos position = minecraft.player.blockPosition();
		final String biome = minecraft.level.getBiome(position).unwrapKey().map(key -> Component.translatable("biome." + key.identifier().getNamespace() + "." + key.identifier().getPath()).getString()).orElse("");
		return String.format(Locale.ROOT, "%s · %d, %d, %d", biome, position.getX(), position.getY(), position.getZ());
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

}