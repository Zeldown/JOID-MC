package dev.joid.backend.minecraft.showcase.ui;

import java.util.Locale;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.render.transform.Scale;
import dev.joid.lib.render.transform.Transformation;
import dev.joid.lib.render.transform.Vector;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.TransformNodeEffect;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.utils.align.Align;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@UIDataMinecraft(title = "Fonts", pause = false)
public class ShowFonts extends UI {

	private static final double PIVOT_X = 168D;
	private static final double PIVOT_Y = 258D;

	private double start;

	@Override
	public void init() {
		this.start = ShowTheme.now();

		ContainerNode
		.create(0, 0, 1920, 1080)
		.effect(TransformNodeEffect.create(Transformation.create().translate(Vector.create(() -> this.zoom() * (270D - ShowFonts.PIVOT_X), () -> this.zoom() * (540D - ShowFonts.PIVOT_Y))).scale(Scale.create(() -> 1D + 5D * this.zoom(), () -> 1D + 5D * this.zoom(), () -> 1D), Vector.create(ShowFonts.PIVOT_X, ShowFonts.PIVOT_Y))).scope(NodeEffectScope.CHILDREN))
		.body(container -> {
			this.sizes(container);
			this.styles(container);
		})
		.attach(this);

		RectNode
		.create(1680, 40, 140, 56)
		.color(ShowTheme.INK.withAlpha(0.72F))
		.effect(RoundedNodeEffect.create(28F))
		.body(pill -> {
			TextNode.create(70, 28).text(Text.create(() -> String.format(Locale.ROOT, "%.1f\u00D7", 1D + 5D * this.zoom()), ShowTheme.font(FontWeight.BOLD, 24F, ShowTheme.TEXT)).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(pill);
		})
		.attach(this);
	}

	private void sizes(final Node container) {
		ShowTheme
		.panel(120, 120, 720, 840)
		.body(panel -> {
			TextNode.create(48, 52).text(Text.create("MINECRAFT FONT", ShowTheme.label(14F, ShowTheme.GRASS))).attach(panel);
			TextNode.create(46, 76).text(Text.create("Sharp at any size", ShowTheme.font(FontWeight.EXTRA_BOLD, 40F, ShowTheme.TEXT))).attach(panel);
			final int[] sizes = {8, 16, 24, 32, 40, 56, 72};
			double y = 148D;
			for (int index = 0; index < sizes.length; index++) {
				final float size = sizes[index];
				final Color color = index == sizes.length - 1 ? ShowTheme.GOLD : ShowTheme.MUTED.to(ShowTheme.TEXT, index / (sizes.length - 1F));
				final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, size).color(color).shadow(size / MinecraftFont.SIZE, size / MinecraftFont.SIZE).effects(ShowRiseTextEffect.of(this.start + 0.15D + index * 0.09D, 0.02D));
				TextNode.create(48, y).text(Text.create("Crisp at " + sizes[index] + " px", info)).attach(panel);
				y += size * 1.25D + 26D;
			}
		})
		.attach(container);
	}

	private void styles(final Node container) {
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, 24F).color(ShowTheme.TEXT).shadow(3F, 3F);
		final Component chat = Component.literal("[").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal("Server").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)).append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("Welcome back, ").withStyle(ChatFormatting.WHITE)).append(Component.literal("Steve").withStyle(ChatFormatting.AQUA)).append(Component.literal("!").withStyle(ChatFormatting.WHITE));

		ShowTheme
		.panel(900, 120, 900, 840)
		.body(panel -> {
			TextNode.create(48, 52).text(Text.create("CHAT TEXT", ShowTheme.label(14F, ShowTheme.FAINT))).attach(panel);
			TextNode.create(48, 82).text(Text.create(chat, info.withEffects(ShowRiseTextEffect.of(this.start + 0.3D, 0.015D)))).attach(panel);
			TextNode.create(48, 172).text(Text.create("\u00A7 COLORS", ShowTheme.label(14F, ShowTheme.FAINT).markups())).attach(panel);
			TextNode.create(48, 202).text(Text.create("\u00A7cRed \u00A76Gold \u00A7eYellow \u00A7aGreen \u00A7bAqua \u00A79Blue \u00A7dPink", info.withEffects(ShowRiseTextEffect.of(this.start + 0.4D, 0.015D)))).attach(panel);
			TextNode.create(48, 292).text(Text.create("\u00A7 FORMATS", ShowTheme.label(14F, ShowTheme.FAINT).markups())).attach(panel);
			TextNode.create(48, 322).text(Text.create("\u00A7lBold\u00A7r \u00A7oitalic\u00A7r \u00A7nunderlined\u00A7r \u00A7mstruck\u00A7r \u00A7e\u00A7kmagic", info.withEffects(ShowRiseTextEffect.of(this.start + 0.5D, 0.015D)))).attach(panel);
			RectNode.create(48, 412, 804, 1).color(Color.WHITE.withAlpha(0.08F)).attach(panel);
			TextNode.create(48, 446).text(Text.create("ENCHANTING TABLE", ShowTheme.label(14F, ShowTheme.AMETHYST))).attach(panel);
			TextNode.create(48, 480).text(Text.create("Sharpness V", TextInfo.create(MinecraftFont.ALT, 40F).color(ShowTheme.AMETHYST).shadow(5F, 5F).effects(ShowRiseTextEffect.of(this.start + 0.6D, 0.03D)))).attach(panel);
			TextNode.create(48, 560).text(Text.create("Sharpness V", TextInfo.create(MinecraftFont.DEFAULT, 40F).color(ShowTheme.TEXT).shadow(5F, 5F).effects(ShowDecodeTextEffect.of(this.start + 1D, ShowTheme.AMETHYST, 0.08D)))).attach(panel);
			TextNode.create(48, 650, 804, 0).text(Text.create("The enchanting table alphabet, then the same line decoded in the default font.", ShowTheme.font(FontWeight.MEDIUM, 18F, ShowTheme.MUTED))).attach(panel);
			RectNode.create(48, 718, 804, 1).color(Color.WHITE.withAlpha(0.08F)).attach(panel);
			TextNode.create(48, 752).text(Text.create("One bitmap atlas, drawn texel for texel at every size.", ShowTheme.font(FontWeight.MEDIUM, 18F, ShowTheme.MUTED))).attach(panel);
		})
		.attach(container);
	}

	private double zoom() {
		final double elapsed = ShowTheme.now() - this.start;
		return ShowFonts.smooth(elapsed, 2.6D, 4D) - ShowFonts.smooth(elapsed, 5.2D, 6.6D);
	}

	private static double smooth(final double elapsed, final double from, final double to) {
		final double progress = Math.clamp((elapsed - from) / (to - from), 0D, 1D);
		return progress < 0.5D ? 16D * Math.pow(progress, 5D) : 1D - Math.pow(-2D * progress + 2D, 5D) / 2D;
	}

}