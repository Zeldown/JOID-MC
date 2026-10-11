package dev.joid.backend.minecraft.showcase.ui;

import java.util.function.Supplier;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.UIDataOverlayLayer;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.core.data.overlay.UIDataOverlay;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.design.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.design.shape.CircleNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

@UIDataOverlay(active = true)
@UIData(background = false, anchorY = Align.END)
@UIDataOverlayLayer(layer = OverlayLayer.HEALTH, cancel = true)
public class ShowHud extends UI {

	private static final Resource FOOD  = Resource.of(new SpriteId(Sheets.GUI_SHEET, Identifier.withDefaultNamespace("hud/food_full")));
	private static final Resource HEART = Resource.of(new SpriteId(Sheets.GUI_SHEET, Identifier.withDefaultNamespace("hud/heart/full")));

	private double start;

	@Override
	public void init() {
		this.start = ShowTheme.now();

		this.capsule(596, ShowTheme.REDSTONE, Color.decode("#FF8A5C"), ShowHud.HEART, () -> (double) (ShowHud.player().getHealth() / ShowHud.player().getMaxHealth()), () -> (int) Math.ceil(ShowHud.player().getHealth()), () -> (int) ShowHud.player().getMaxHealth()).attach(this);

		this.capsule(988, ShowTheme.GOLD, Color.decode("#FF9F1C"), ShowHud.FOOD, () -> ShowHud.player().getFoodData().getFoodLevel() / 20D, () -> ShowHud.player().getFoodData().getFoodLevel(), () -> 20).attach(this);

		RectNode
		.create(596, 974, 728, 10)
		.color(ShowTheme.INK.copyAlpha(0.78F))
		.effect(RoundedNodeEffect.create(5F))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.10F), 1F, BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.copyAlpha(0.45F), 12F, 0D, 2D))
		.body(bar -> {
			RectNode
			.create(2, 2, 0, 6)
			.color(ShowTheme.horizontal(ShowTheme.EMERALD, ShowTheme.GRASS))
			.width(() -> 724D * ShowHud.player().experienceProgress * ShowTheme.ease(this.elapsed(), 0.3D, 1.2D))
			.effect(RoundedNodeEffect.create(3F))
			.effect(ShadowNodeEffect.create(ShowTheme.EMERALD.copyAlpha(0.7F), 8F))
			.attach(bar);
		})
		.attach(this);

		CircleNode
		.create(936, 922, 48)
		.color(ShowTheme.vertical(Color.decode("#1B2130"), ShowTheme.INK))
		.effect(BorderNodeEffect.create(ShowTheme.GRASS.copyAlpha(0.9F), 2F, BorderMode.IN))
		.effect(ShadowNodeEffect.create(ShowTheme.EMERALD.copyAlpha(0.5F), 16F))
		.body(badge -> {
			TextNode.create(24, 25).text(Text.create(() -> String.valueOf(ShowHud.player().experienceLevel), ShowTheme.font(FontWeight.EXTRA_BOLD, 18F, ShowTheme.GRASS), Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(badge);
		})
		.attach(this);
	}

	private RectNode capsule(final double x, final Color from, final Color to, final Resource icon, final Supplier<Double> ratio, final Supplier<Integer> value, final Supplier<Integer> total) {
		final TextInfo bold = ShowTheme.font(FontWeight.BOLD, 16F, ShowTheme.TEXT);
		final TextInfo muted = ShowTheme.font(FontWeight.SEMI_BOLD, 13F, ShowTheme.MUTED);
		return RectNode
		.create(x, 928, 336, 36)
		.color(ShowTheme.INK.copyAlpha(0.78F))
		.effect(RoundedNodeEffect.create(18F))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.10F), 1F, BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.copyAlpha(0.45F), 18F, 0D, 4D))
		.body(capsule -> {
			ResourceNode
			.create(0, 0, 20, 20)
			.resource(icon)
			.width(() -> 20D * this.pulse())
			.height(() -> 20D * this.pulse())
			.x(() -> 26D - 10D * this.pulse())
			.y(() -> 18D - 10D * this.pulse())
			.attach(capsule);
			RectNode
			.create(48, 15, 200, 6)
			.color(Color.WHITE.copyAlpha(0.10F))
			.effect(RoundedNodeEffect.create(3F))
			.body(track -> {
				RectNode.create(0, 0, 0, 6).color(ShowTheme.horizontal(from, to)).width(() -> 200D * ratio.get() * ShowTheme.ease(this.elapsed(), 0.15D, 1D)).effect(RoundedNodeEffect.create(3F)).effect(ShadowNodeEffect.create(from.copyAlpha(0.6F), 6F)).attach(track);
			})
			.attach(capsule);
			TextNode.create(290, 19).text(Text.create(() -> String.valueOf(value.get()), bold, Align.END)).anchorX(Align.END).anchorY(Align.CENTER).attach(capsule);
			TextNode.create(294, 20).text(Text.create(() -> "/ " + total.get(), muted)).anchorY(Align.CENTER).attach(capsule);
		});
	}

	private double pulse() {
		final double beat = this.elapsed() % 1.2D;
		return 1D + 0.14D * Math.max(0D, Math.sin(beat / 0.3D * Math.PI)) * (beat < 0.3D ? 1D : 0D);
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

	private static LocalPlayer player() {
		return Minecraft.getInstance().player;
	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.FOOD, cancel = true)
	public static class Food extends UI {

		@Override
		public void init() {}

	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.CONTEXTUAL_BAR, cancel = true)
	public static class Experience extends UI {

		@Override
		public void init() {}

	}

	@UIDataOverlay(active = true)
	@UIData(background = false, anchorY = Align.END)
	@UIDataOverlayLayer(layer = OverlayLayer.EXPERIENCE_LEVEL, cancel = true)
	public static class Level extends UI {

		@Override
		public void init() {}

	}

}