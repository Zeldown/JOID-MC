package dev.joid.backend.minecraft.showcase.ui;

import dev.joid.lib.font.effect.ITextEffect;
import dev.joid.lib.font.effect.ITextGlyph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowRiseTextEffect implements ITextEffect {

	private final double start;
	private final double stagger;

	public static @NonNull ShowRiseTextEffect of(final double start, final double stagger) {
		return new ShowRiseTextEffect(start, stagger);
	}

	@Override
	public void apply(final @NonNull ITextGlyph glyph) {
		final double progress = ShowTheme.ease(ShowTheme.now(), this.start + glyph.getIndex() * this.stagger, 0.6D);
		glyph.offset(glyph.getOffsetX(), glyph.getOffsetY() + (1D - progress) * glyph.getSize() * 0.6D);
		glyph.color(glyph.getColor().withAlpha(glyph.getColor().getAlphaF() * (float) Math.min(1D, progress * 1.6D)));
	}

}