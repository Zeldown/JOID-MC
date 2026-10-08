package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.dto.MinecraftFontFace;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.IFontProvider;
import dev.joid.lib.font.impl.glyph.GlyphFont;
import dev.joid.lib.font.impl.glyph.dto.FontFamily;
import lombok.Getter;
import lombok.NonNull;

import net.minecraft.resources.Identifier;

@Getter
public final class MinecraftFont extends GlyphFont<MinecraftFontFace> {

	public static final float SIZE = 8F;

	private static final Map<Identifier, MinecraftFont> FONTS = new ConcurrentHashMap<>();

	public static final MinecraftFont DEFAULT = MinecraftFont.of(Identifier.withDefaultNamespace("default"));
	public static final MinecraftFont ALT     = MinecraftFont.of(Identifier.withDefaultNamespace("alt"));
	public static final MinecraftFont ILLAGER = MinecraftFont.of(Identifier.withDefaultNamespace("illageralt"));
	public static final MinecraftFont UNIFORM = MinecraftFont.of(Identifier.withDefaultNamespace("uniform"));

	private final Identifier identifier;

	private MinecraftFont(final Identifier identifier) {
		super(FontFamily.of(MinecraftFontFace.create(identifier, FontWeight.REGULAR), MinecraftFontFace.create(identifier, FontWeight.BOLD)));
		this.identifier = identifier;
	}

	public static @NonNull MinecraftFont of(final @NonNull Identifier identifier) {
		return MinecraftFont.FONTS.computeIfAbsent(identifier, MinecraftFont::new);
	}

	@Override
	public @NonNull IFontProvider getFontProvider() {
		return MinecraftFontProvider.inst();
	}

	@Override
	public String toString() {
		return this.identifier.toString();
	}

}