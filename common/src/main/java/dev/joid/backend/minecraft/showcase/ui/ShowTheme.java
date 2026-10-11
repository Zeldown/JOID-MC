package dev.joid.backend.minecraft.showcase.ui;

import javax.vecmath.Vector4f;

import dev.joid.demo.DemoFont;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.shader.impl.BorderShader.BorderMode;
import dev.joid.lib.ui.node.effect.impl.BorderNodeEffect;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowTheme {

	public static final Color INK      = Color.decode("#0D1017");
	public static final Color GOLD     = Color.decode("#FFC93C");
	public static final Color TEXT     = Color.decode("#F4F6FB");
	public static final Color GRASS    = Color.decode("#7ED957");
	public static final Color MUTED    = Color.decode("#A3ACBD");
	public static final Color FAINT    = Color.decode("#6E7789");
	public static final Color DIAMOND  = Color.decode("#4FE3E6");
	public static final Color EMERALD  = Color.decode("#2BD67B");
	public static final Color REDSTONE = Color.decode("#FF4D5E");
	public static final Color AMETHYST = Color.decode("#B284FF");

	private static final Color PANEL_TOP    = Color.decode("#1B2130");
	private static final Color PANEL_BOTTOM = Color.decode("#11151E");

	public static RectNode panel(final double x, final double y, final double width, final double height) {
		return RectNode
		.create(x, y, width, height)
		.color(ShowTheme.vertical(ShowTheme.PANEL_TOP.copyAlpha(0.86F), ShowTheme.PANEL_BOTTOM.copyAlpha(0.92F)))
		.effect(RoundedNodeEffect.create(28F))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.10F), 1.5F, BorderMode.IN))
		.effect(ShadowNodeEffect.create(Color.BLACK.copyAlpha(0.55F), 60F, 0D, 24D));
	}

	public static RectNode tile(final double x, final double y, final double width, final double height, final float radius) {
		return RectNode
		.create(x, y, width, height)
		.color(Color.WHITE.copyAlpha(0.05F))
		.effect(RoundedNodeEffect.create(radius))
		.effect(BorderNodeEffect.create(Color.WHITE.copyAlpha(0.06F), 1F, BorderMode.IN));
	}

	public static TextInfo font(final FontWeight weight, final float size, final Color color) {
		return TextInfo.create(DemoFont.MONTSERRAT, weight, size, color);
	}

	public static TextInfo label(final float size, final Color color) {
		return ShowTheme.font(FontWeight.BOLD, size, color).letterSpacing(0.16F);
	}

	public static Color vertical(final Color top, final Color bottom) {
		return top.toGradient(bottom, new Vector4f(0F, 0F, 0F, 1F));
	}

	public static Color horizontal(final Color left, final Color right) {
		return left.toGradient(right, new Vector4f(0F, 0F, 1F, 0F));
	}

	public static double now() {
		return BridgeHandler.CLOCK.get().currentTimeMillis() / 1000D;
	}

	public static double ease(final double elapsed, final double delay, final double duration) {
		final double progress = Math.clamp((elapsed - delay) / duration, 0D, 1D);
		return 1D - Math.pow(1D - progress, 3D);
	}

}