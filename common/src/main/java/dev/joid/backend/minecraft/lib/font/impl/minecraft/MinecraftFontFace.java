package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.impl.glyph.IFontFace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftFontFace implements IFontFace {

	private final FontDescription.Resource description;
	private final FontWeight               weight;

	public static @NonNull MinecraftFontFace create(final @NonNull Identifier identifier, final @NonNull FontWeight weight) {
		return new MinecraftFontFace(new FontDescription.Resource(identifier), weight);
	}

	@Override
	public boolean isItalic() {
		return false;
	}

	public boolean isBold() {
		return this.weight == FontWeight.BOLD;
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
		return this.description.id().toString();
	}

	@Override
	public float getAdvance(final int codepoint) {
		return this.getGlyph(codepoint).info().getAdvance(this.isBold()) / MinecraftFont.SIZE;
	}

	@Override
	public float getKerning(final int previous, final int current) {
		return 0F;
	}

	@Override
	public boolean hasGlyph(final int codepoint) {
		return true;
	}

	public @NonNull GlyphSource getSource() {
		return Minecraft.getInstance().font.provider.glyphs(this.description);
	}

	public @NonNull BakedGlyph getGlyph(final int codepoint) {
		return this.getSource().getGlyph(codepoint);
	}

}