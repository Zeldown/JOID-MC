package dev.joid.backend.minecraft.lib.font.dto.effect;

import java.util.concurrent.ThreadLocalRandom;

import dev.joid.lib.font.dto.effect.ITextEffect;
import dev.joid.lib.font.dto.effect.ITextGlyph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObfuscatedTextEffect implements ITextEffect {

	private static final ObfuscatedTextEffect INSTANCE = new ObfuscatedTextEffect();

	public static @NonNull ObfuscatedTextEffect inst() {
		return ObfuscatedTextEffect.INSTANCE;
	}

	@Override
	public void apply(final @NonNull ITextGlyph glyph) {
		if (glyph.getCodepoint() == ' ') {
			return;
		}

		final double advance = glyph.getAdvance(glyph.getCodepoint());
		int candidates = 0;
		for (int codepoint = '!'; codepoint <= '~'; codepoint++) {
			if (glyph.hasGlyph(codepoint) && glyph.getAdvance(codepoint) == advance) {
				candidates++;
			}
		}

		int remaining = candidates == 0 ? -1 : ThreadLocalRandom.current().nextInt(candidates);
		for (int codepoint = '!'; codepoint <= '~' && remaining >= 0; codepoint++) {
			if (glyph.hasGlyph(codepoint) && glyph.getAdvance(codepoint) == advance && remaining-- == 0) {
				glyph.codepoint(codepoint);
			}
		}
	}

}