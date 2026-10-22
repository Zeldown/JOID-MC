package dev.joid.backend.minecraft.showcase.ui;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.BlendState.Equation;
import dev.joid.lib.bridge.render.state.BlendState.Factor;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.signal.impl.primitive.DoubleSignal;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.BlurNodeEffect;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.shape.CircleNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.utils.align.Align;
import lombok.Getter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.clock.ClockNetworkState;

@UIDataMinecraft(title = "Daylight", pause = false, background = false)
public class ShowDaylight extends UI implements IShowFocus {

	private static final String[] PHASES = {"Full moon", "Waning gibbous", "Third quarter", "Waning crescent", "New moon", "Waxing crescent", "First quarter", "Waxing gibbous"};

	private static final BlendState ADDITIVE = BlendState.of(Equation.ADD, Factor.SRC_ALPHA, Factor.ONE);

	private static final Resource       SUN   = Resource.of("minecraft:textures/environment/celestial/sun.png");
	private static final List<Resource> MOONS = Stream.of("full_moon", "waning_gibbous", "third_quarter", "waning_crescent", "new_moon", "waxing_crescent", "first_quarter", "waxing_gibbous").map(name -> Resource.of("minecraft:textures/environment/celestial/moon/" + name + ".png")).toList();

	@Getter
	private final DoubleSignal time;

	private double         start;
	private ShowSliderNode slider;

	public ShowDaylight() {
		this.time = new DoubleSignal(4000D);
	}

	@Override
	public void init() {
		this.start = ShowTheme.now();
		this.time.onChange(ticks -> ShowDaylight.world(ticks.longValue()));

		ShowTheme
		.panel(100, 100, 600, 880)
		.x(() -> 100D - 80D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.7D)))
		.body(panel -> {
			TextNode.create(48, 52).text(Text.create("WORLD TIME", ShowTheme.label(13F, ShowTheme.GOLD))).attach(panel);
			TextNode.create(46, 76).text(Text.create("Daylight", ShowTheme.font(FontWeight.EXTRA_BOLD, 48F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(48, 142).text(Text.create("One signal. The panel and the world follow it.", ShowTheme.font(FontWeight.MEDIUM, 17F, ShowTheme.MUTED))).attach(panel);
			this.dial(panel);
			TextNode.create(48, 500).text(Text.create(this::clock, ShowTheme.font(FontWeight.BLACK, 72F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(52, 590).text(Text.create(() -> "Day " + (this.day() + 1) + "  \u00B7  " + ShowDaylight.PHASES[this.day() % 8], ShowTheme.font(FontWeight.SEMI_BOLD, 18F, ShowTheme.MUTED))).attach(panel);
			RectNode
			.create(48, 640, 504, 44)
			.color(() -> ShowTheme.horizontal(this.sky(0D), this.sky(0.5D)))
			.effect(RoundedNodeEffect.create(22F))
			.effect(BorderNodeEffect.create(Color.WHITE.withAlpha(0.12F), 1F).mode(BorderMode.IN))
			.body(band -> {
				TextNode.create(24, 22).text(Text.create("SKY", ShowTheme.label(12F, Color.WHITE.withAlpha(0.8F)))).anchorY(Align.CENTER).attach(band);
			})
			.attach(panel);
			RectNode
			.create(48, 708, 504, 48)
			.color(ShowTheme.REDSTONE.withAlpha(0.16F))
			.visible(this::isNight)
			.effect(RoundedNodeEffect.create(24F))
			.effect(BorderNodeEffect.create(ShowTheme.REDSTONE.withAlpha(0.5F), 1F).mode(BorderMode.IN))
			.body(warning -> {
				CircleNode.create(22, 18, 12).color(ShowTheme.REDSTONE).effect(ShadowNodeEffect.create(ShowTheme.REDSTONE, 10F)).attach(warning);
				TextNode.create(48, 24).text(Text.create("Monsters spawn", ShowTheme.font(FontWeight.BOLD, 18F, ShowTheme.REDSTONE))).anchorY(Align.CENTER).attach(warning);
			})
			.attach(panel);
			this.slider = ShowSliderNode.create(48, 790, 504, ShowTheme.GOLD, ShowTheme.DIAMOND).range(0D, 48000D, 50D).value(this.time);
			this.slider.attach(panel);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.slider;
	}

	private void dial(final Node panel) {
		CircleNode
		.create(160, 180, 280)
		.color(() -> ShowTheme.vertical(this.sky(0D), this.sky(0.5D)))
		.effect(BorderNodeEffect.create(Color.WHITE.withAlpha(0.14F), 2F).mode(BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.withAlpha(0.5F), 30F).offsetY(10D))
		.body(dial -> {
			CircleNode.create(70, 70, 140).color(() -> this.sky(0D).withAlpha(0.6F)).effect(BlurNodeEffect.create(30F)).interactive(false).attach(dial);
			RectNode.create(0, 139, 280, 2).color(Color.WHITE.withAlpha(0.25F)).attach(dial);
			RectNode.create(0, 141, 280, 139).color(Color.BLACK.withAlpha(0.25F)).attach(dial);
			RectNode.create(0, 0, 280, 280).color(Color.TRANSPARENT).interactive(false).layer((_, _, _) -> {
				ShowDaylight.celestial(ShowDaylight.SUN, this.orbitX(0D), this.orbitY(0D));
				ShowDaylight.celestial(ShowDaylight.MOONS.get(this.day() % 8), this.orbitX(Math.PI), this.orbitY(Math.PI));
			}).attach(dial);
		})
		.attach(panel);
	}

	private double angle() {
		return this.time.get() / 24000D * Math.PI * 2D;
	}

	private double orbitX(final double offset) {
		return 116D + 104D * Math.cos(this.angle() + offset);
	}

	private double orbitY(final double offset) {
		return 116D - 104D * Math.sin(this.angle() + offset);
	}

	private int day() {
		return (int) (this.time.get() / 24000D);
	}

	private boolean isNight() {
		final double ticks = this.time.get() % 24000D;
		return ticks > 13000D && ticks < 23000D;
	}

	private String clock() {
		final int minutes = (int) ((this.time.get() / 1000D + 6D) * 60D) % 1440;
		return String.format(Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60);
	}

	private Color sky(final double shift) {
		final double light = Math.clamp(0.5D + Math.sin(this.angle()) * 2D, 0D, 1D);
		final double dusk = Math.max(0D, 1D - Math.abs(Math.sin(this.angle())) * 4D);
		final Color day = Color.decode("#78A7FF").to(Color.decode("#C6DBFF"), (float) shift);
		final Color night = Color.decode("#0B1026").to(Color.decode("#1B2244"), (float) shift);
		return night.to(day, (float) light).to(Color.decode("#FF8A5C"), (float) (dusk * 0.6D * (1D - shift)));
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

	private static void celestial(final Resource resource, final double x, final double y) {
		resource.request(48, 48);
		resource.bind(TextureWrap.CLAMP_TO_EDGE, () -> {
			final ITexture texture = resource.getTexture();
			if (texture != null) {
				DrawUtils.RESOURCE.drawTexture(x, y, 48D, 48D, texture, 0D, 0D, 1D, 1D, TextureFilter.NEAREST, ShowDaylight.ADDITIVE);
			}
		});
	}

	private static void world(final long ticks) {
		final ClientLevel level = Minecraft.getInstance().level;
		level.dimensionType().defaultClock().ifPresent(clock -> level.clockManager().handleUpdates(level.getGameTime(), Map.of(clock, new ClockNetworkState(ticks, 0F, 0F))));
	}

}