package dev.joid.backend.minecraft.showcase.ui;

import java.util.Locale;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.design.resource.ResourceNode.StretchType;
import dev.joid.lib.ui.node.impl.design.resource.ResourcePlayerNode;
import dev.joid.lib.ui.node.impl.design.shape.CircleNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;
import dev.joid.lib.utils.point.Point;
import lombok.Getter;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

@UIDataMinecraft(title = "Video", pause = false)
public class ShowVideo extends UI implements IShowFocus {

	private static final Resource CLIP = Resource.of("assets/demo/videos/video.mp4");

	@Getter
	private ResourcePlayerNode player;

	private Node   button;
	private double start;

	@Override
	public void init() {
		this.start = ShowTheme.now();

		RectNode
		.create(240, 96, 1440, 810)
		.color(ShowTheme.INK)
		.y(() -> 96D + 40D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.6D)))
		.effect(ShadowNodeEffect.create(Color.BLACK.withAlpha(0.6F), 70F, 0D, 34D))
		.effect(RoundedNodeEffect.create(34F).scope(NodeEffectScope.CHILDREN))
		.body(card -> {
			this.player = ResourcePlayerNode
			.create(0, 0, 1440, 810)
			.resource(ShowVideo.CLIP)
			.stretch(StretchType.COVER)
			.audioGroup(SoundSource.MUSIC)
			.loop(true)
			.attach(card);
			RectNode.create(0, 0, 1440, 220).color(ShowTheme.vertical(Color.BLACK.withAlpha(0.6F), Color.BLACK.withAlpha(0F))).attach(card);
			RectNode.create(0, 500, 1440, 310).color(ShowTheme.vertical(Color.BLACK.withAlpha(0F), Color.BLACK.withAlpha(0.75F))).attach(card);
			TextNode.create(56, 48).text(Text.create("NOW PLAYING", ShowTheme.label(14F, ShowTheme.GOLD))).attach(card);
			TextNode.create(54, 72).text(Text.create("Golden Hour", ShowTheme.font(FontWeight.EXTRA_BOLD, 54F, ShowTheme.TEXT))).attach(card);
			TextNode.create(58, 144).text(Text.create("Decoded off the render thread, drawn like any node", ShowTheme.font(FontWeight.MEDIUM, 21F, ShowTheme.MUTED))).attach(card);
			this.volume(card);
			this.controls(card);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.button;
	}

	private void volume(final Node card) {
		RectNode
		.create(1084, 52, 300, 52)
		.color(ShowTheme.INK.withAlpha(0.72F))
		.effect(RoundedNodeEffect.create(26F))
		.body(chip -> {
			CircleNode.create(22, 19, 14).color(ShowTheme.GRASS).effect(ShadowNodeEffect.create(ShowTheme.GRASS, 10F)).attach(chip);
			TextNode.create(48, 26).text(Text.create(() -> String.format(Locale.ROOT, "Music volume  %d %%", Math.round(Minecraft.getInstance().options.getFinalSoundSourceVolume(SoundSource.MUSIC) * 100F)), ShowTheme.font(FontWeight.BOLD, 18F, ShowTheme.TEXT))).anchorY(Align.CENTER).attach(chip);
		})
		.attach(card);
	}

	private void controls(final Node card) {
		this.button = CircleNode
		.create(56, 690, 84)
		.color(Color.WHITE)
		.hoveredColor(ShowTheme.GOLD)
		.cursor(Cursor.POINTER)
		.effect(ShadowNodeEffect.create(ShowTheme.GOLD.withAlpha(0.6F), 26F))
		.onClick(_ -> {
			if (this.player.isPlaying()) {
				this.player.pause();
			} else {
				this.player.resume();
			}
		})
		.layer((_, _) -> this.icon(98D, 732D))
		.attach(card);
		RectNode.create(176, 712, 1060, 40).color(Color.TRANSPARENT).layer((_, _) -> this.bar(176D, 732D, 1060D)).attach(card);
		TextNode.create(1384, 732).text(Text.create(() -> ShowVideo.clock(this.player.getProgress() * this.player.getDuration()) + " / " + ShowVideo.clock(this.player.getDuration()), ShowTheme.font(FontWeight.SEMI_BOLD, 22F, ShowTheme.TEXT), Align.END)).anchorX(Align.END).anchorY(Align.CENTER).attach(card);
	}

	private void icon(final double x, final double y) {
		if (this.player.isPlaying()) {
			DrawUtils.SHAPE.drawRoundedRect(x - 13D, y - 16D, 9D, 32D, ShowTheme.INK, 3F);
			DrawUtils.SHAPE.drawRoundedRect(x + 4D, y - 16D, 9D, 32D, ShowTheme.INK, 3F);
		} else {
			DrawUtils.SHAPE.drawPolygon(ShowTheme.INK, Point.create(x - 9D, y - 17D), Point.create(x - 9D, y + 17D), Point.create(x + 17D, y));
		}
	}

	private void bar(final double x, final double y, final double width) {
		final double progress = this.player.getProgress();
		DrawUtils.SHAPE.drawRoundedRect(x, y - 4D, width, 8D, Color.WHITE.withAlpha(0.22F), 4F);
		DrawUtils.SHAPE.drawShadow(x, y - 4D, width * progress, 8D, ShowTheme.GOLD.withAlpha(0.6F), 4F, 16F);
		DrawUtils.SHAPE.drawRoundedRect(x, y - 4D, width * progress, 8D, ShowTheme.horizontal(ShowTheme.GOLD, ShowTheme.GRASS), 4F);
		DrawUtils.SHAPE.drawCircle(x + width * progress, y, Color.WHITE, 10D);
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

	private static String clock(final double seconds) {
		final int total = (int) Math.floor(seconds);
		return String.format(Locale.ROOT, "%d:%02d", total / 60, total % 60);
	}

}