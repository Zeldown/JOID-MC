package dev.joid.backend.minecraft.lib.font.effect.impl;

import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.font.effect.ITextEffect;
import dev.joid.lib.font.effect.ITextGlyph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnderlineTextEffect implements ITextEffect {

	private static final UnderlineTextEffect INSTANCE = new UnderlineTextEffect();

	public static @NonNull UnderlineTextEffect inst() {
		return UnderlineTextEffect.INSTANCE;
	}

	@Override
	public void decorate(final @NonNull ITextGlyph glyph) {
		final double thickness = glyph.getUnderlineThickness();
		final double extension = glyph.getIndex() == 0 ? thickness : 0D;
		DrawUtils.SHAPE.drawRect(glyph.getX() - extension, glyph.getUnderlineY(), glyph.getAdvance() + extension, thickness, glyph.getColor());
	}

}