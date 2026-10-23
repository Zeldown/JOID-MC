package dev.joid.backend.minecraft.showcase.ui;

import dev.joid.lib.color.Color;
import dev.joid.lib.font.effect.ITextEffect;
import dev.joid.lib.font.effect.ITextGlyph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowDecodeTextEffect implements ITextEffect {

	private final double start;
	private final Color  accent;
	private final double stagger;

	public static @NonNull ShowDecodeTextEffect of(final double start, final @NonNull Color accent, final double stagger) {
		return new ShowDecodeTextEffect(start, accent, stagger);
	}

	@Override
	public void apply(final @NonNull ITextGlyph glyph) {
		if (Character.isWhitespace(glyph.getCodepoint())) {
			return;
		}

		final double now = ShowTheme.now();
		final double reveal = this.start + glyph.getIndex() * this.stagger;
		if (now >= reveal) {
			final float flash = (float) Math.max(0D, 1D - (now - reveal) / 0.4D);
			glyph.color(glyph.getColor().to(Color.WHITE.withAlpha(glyph.getColor().getAlphaF()), flash));
			return;
		}

		final double advance = glyph.getAdvance(glyph.getCodepoint());
		final long seed = (long) (now * 15D) * 31L + glyph.getIndex() * 17L;
		for (int attempt = 0; attempt < 94; attempt++) {
			final int codepoint = '!' + (int) ((seed + attempt * 7L) % 94L);
			if (glyph.hasGlyph(codepoint) && glyph.getAdvance(codepoint) == advance) {
				glyph.codepoint(codepoint);
				break;
			}
		}

		glyph.color(this.accent.withAlpha(glyph.getColor().getAlphaF() * (now < this.start ? 0.35F : 0.85F)));
	}

}