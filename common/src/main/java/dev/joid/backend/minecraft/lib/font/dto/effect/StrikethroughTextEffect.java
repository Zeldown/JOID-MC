package dev.joid.backend.minecraft.lib.font.dto.effect;

import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.font.dto.effect.ITextEffect;
import dev.joid.lib.font.dto.effect.ITextGlyph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrikethroughTextEffect implements ITextEffect {

	private static final StrikethroughTextEffect INSTANCE = new StrikethroughTextEffect();

	public static @NonNull StrikethroughTextEffect inst() {
		return StrikethroughTextEffect.INSTANCE;
	}

	@Override
	public void decorate(final @NonNull ITextGlyph glyph) {
		final double thickness = glyph.getUnderlineThickness();
		final double extension = glyph.getIndex() == 0 ? thickness : 0D;
		DrawUtils.SHAPE.drawRect(glyph.getX() - extension, glyph.getBaseline() - glyph.getAscender() / 2D, glyph.getAdvance() + extension, thickness, glyph.getColor());
	}

}