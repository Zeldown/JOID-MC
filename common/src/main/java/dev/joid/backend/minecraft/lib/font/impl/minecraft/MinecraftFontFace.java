package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.impl.glyph.IFontFace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftFontFace implements IFontFace {

	private final FontWeight       weight;
	private final ResourceLocation id;

	public static @NonNull MinecraftFontFace of(final @NonNull ResourceLocation id, final @NonNull FontWeight weight) {
		return new MinecraftFontFace(weight, id);
	}

	public boolean isBold() {
		return this.weight == FontWeight.BOLD;
	}

	@Override
	public boolean isItalic() {
		return false;
	}

	@Override
	public float getAscender() {
		return 7F / MinecraftFont.SIZE;
	}

	@Override
	public float getDescender() {
		return -2F / MinecraftFont.SIZE;
	}

	@Override
	public float getLineHeight() {
		return 9F / MinecraftFont.SIZE;
	}

	@Override
	public float getUnderlineY() {
		return -1F / MinecraftFont.SIZE;
	}

	@Override
	public float getUnderlineThickness() {
		return 1F / MinecraftFont.SIZE;
	}

	@Override
	public @NonNull String getName() {
		return this.id.toString();
	}

	@Override
	public float getAdvance(final int codepoint) {
		return this.getFontSet().getGlyphInfo(codepoint).getAdvance(this.isBold()) / MinecraftFont.SIZE;
	}

	@Override
	public float getKerning(final int previous, final int current) {
		return 0F;
	}

	@Override
	public boolean hasGlyph(final int codepoint) {
		return true;
	}

	public @NonNull FontSet getFontSet() {
		return Minecraft.getInstance().font.getFontSet(this.id);
	}

	public @NonNull BakedGlyph getGlyph(final int codepoint) {
		return this.getFontSet().getGlyph(codepoint);
	}

}