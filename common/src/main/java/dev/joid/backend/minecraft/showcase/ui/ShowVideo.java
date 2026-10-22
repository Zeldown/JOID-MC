package dev.joid.backend.minecraft.showcase.ui;

import java.util.Locale;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.animation.animator.TweenAnimator;
import dev.joid.lib.animation.tween.TweenEquations;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.input.cursor.Cursor;
import dev.joid.lib.render.transform.Scale;
import dev.joid.lib.render.transform.Transformation;
import dev.joid.lib.render.transform.Vector;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.signal.impl.primitive.DoubleSignal;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.effect.impl.TransformNodeEffect;
import dev.joid.lib.ui.node.impl.resource.ResourceNode.StretchType;
import dev.joid.lib.ui.node.impl.resource.ResourcePlayerNode;
import dev.joid.lib.ui.node.impl.shape.CircleNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.utils.align.Align;
import dev.joid.lib.utils.geom.Point;
import lombok.Getter;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

@UIDataMinecraft(title = "Video", pause = false)
public class ShowVideo extends UI implements IShowFocus {

	private static final Resource CLIP = Resource.of("assets/demo/videos/video.mp4");

	private final DoubleSignal  hover;
	private final TweenAnimator reveal;

	@Getter private ResourcePlayerNode player;
	@Getter private Node               timeline;

	private Node   button;
	private double start;

	private long   peekTime;
	private double peeked;

	public ShowVideo() {
		this.hover  = new DoubleSignal(0D);
		this.reveal = TweenAnimator.create(0F);
		this.peeked = -1D;
	}

	@Override
	public void init() {
		this.start = ShowTheme.now();

		RectNode
		.create(240, 96, 1440, 810)
		.color(ShowTheme.INK)
		.y(() -> 96D + 40D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.6D)))
		.effect(ShadowNodeEffect.create(Color.BLACK.withAlpha(0.6F), 70F).offsetY(34D))
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
			final ResourcePlayerNode preview = ResourcePlayerNode
			.create(8, 8, 320, 180)
			.resource(ShowVideo.CLIP)
			.stretch(StretchType.COVER)
			.volume(0F)
			.pause();
			this.volume(card);
			this.controls(card, preview);
			this.preview(card, preview);
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

	private void controls(final Node card, final ResourcePlayerNode preview) {
		this.button = CircleNode
		.create(56, 690, 84)
		.color(Color.WHITE)
		.hoveredColor(ShowTheme.GOLD)
		.cursor(Cursor.POINTER)
		.effect(ShadowNodeEffect.create(ShowTheme.GOLD.withAlpha(0.6F), 26F))
		.onClick(_ -> this.player.toggle())
		.layer((_, _, _) -> this.icon(42D, 42D))
		.attach(card);
		this.timeline = RectNode
		.create(176, 712, 1060, 40)
		.color(Color.TRANSPARENT)
		.onHoverStart(_ -> this.reveal.sequence(220F, 1F, TweenEquations.QUAD_OUT).start())
		.onHoverEnd(_ -> this.reveal.sequence(220F, 0F, TweenEquations.QUAD_OUT).start())
		.onHover(event -> this.peek(ShowVideo.ratio(event.getNode(), event.getMouseX()), preview))
		.layer((_, _, _) -> this.bar(0D, 20D, 1060D))
		.attach(card);
		TextNode.create(1384, 732).text(Text.create(() -> ShowVideo.clock(this.player.getProgress() * this.player.getDuration()) + " / " + ShowVideo.clock(this.player.getDuration()), ShowTheme.font(FontWeight.SEMI_BOLD, 22F, ShowTheme.TEXT)).alignX(Align.END)).anchorX(Align.END).anchorY(Align.CENTER).attach(card);
	}

	private void preview(final Node card, final ResourcePlayerNode preview) {
		RectNode
		.create(0, 456, 336, 236)
		.color(ShowTheme.INK.withAlpha(0.94F))
		.<RectNode>x(() -> Math.clamp(8D + this.hover.get() * 1060D, 16D, 1088D))
		.visible(() -> this.reveal.get() > 0.01F)
		.effect(ShadowNodeEffect.create(Color.BLACK.withAlpha(0.55F), 32F).offsetY(14D))
		.effect(BorderNodeEffect.create(Color.WHITE.withAlpha(0.14F), 2F).mode(BorderMode.IN))
		.effect(RoundedNodeEffect.create(18F).scope(NodeEffectScope.CHILDREN))
		.effect(TransformNodeEffect.create(Transformation.create().translate(Vector.create(() -> 0D, () -> 16D * (1D - this.reveal.get()))).scale(Scale.create(() -> 0.92D + 0.08D * this.reveal.get(), () -> 0.92D + 0.08D * this.reveal.get(), () -> 1D))))
		.body(thumb -> {
			preview.effect(RoundedNodeEffect.create(12F)).attach(thumb);
			TextNode.create(168, 212).text(Text.create(() -> ShowVideo.clock(this.hover.get() * this.player.getDuration()), ShowTheme.font(FontWeight.SEMI_BOLD, 22F, ShowTheme.TEXT))).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(thumb);
		})
		.attach(card);
	}

	private void peek(final double ratio, final ResourcePlayerNode preview) {
		this.hover.set(ratio);
		final long now = BridgeHandler.CLOCK.get().currentTimeMillis();
		if (ratio != this.peeked && now - this.peekTime >= 200L) {
			this.peeked   = ratio;
			this.peekTime = now;
			preview.seek(ratio * this.player.getDuration());
		}
	}

	private void icon(final double x, final double y) {
		if (this.player.isPlaying()) {
			DrawUtils.SHAPE.drawRoundedRect(x - 13D, y - 16D, 9D, 32D, ShowTheme.INK, 3F);
			DrawUtils.SHAPE.drawRoundedRect(x + 4D, y - 16D, 9D, 32D, ShowTheme.INK, 3F);
		} else {
			DrawUtils.SHAPE.drawPolygon(ShowTheme.INK, Point.of(x - 9D, y - 17D), Point.of(x - 9D, y + 17D), Point.of(x + 17D, y));
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

	private static double ratio(final Node node, final double mouseX) {
		return Math.clamp((mouseX - node.getAbsoluteX()) / node.getWidth(), 0D, 1D);
	}

	private static String clock(final double seconds) {
		final int total = (int) Math.floor(seconds);
		return String.format(Locale.ROOT, "%d:%02d", total / 60, total % 60);
	}

}