package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.ITextRenderer;
import dev.joid.lib.font.impl.bitmap.BitmapFont;
import dev.joid.lib.font.impl.glyph.FontFamily;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;

@Getter
public final class MinecraftFont extends BitmapFont<MinecraftFontFace> {

	public static final float SIZE = 8F;

	private static final Map<ResourceLocation, MinecraftFont> FONTS = new ConcurrentHashMap<>();

	public static final MinecraftFont ALT     = MinecraftFont.of(ResourceLocation.parse("alt"));
	public static final MinecraftFont DEFAULT = MinecraftFont.of(ResourceLocation.parse("default"));
	public static final MinecraftFont UNIFORM = MinecraftFont.of(ResourceLocation.parse("uniform"));
	public static final MinecraftFont ILLAGER = MinecraftFont.of(ResourceLocation.parse("illageralt"));

	private final ResourceLocation id;

	private MinecraftFont(final ResourceLocation id) {
		super(FontFamily.of(MinecraftFontFace.of(id, FontWeight.REGULAR), MinecraftFontFace.of(id, FontWeight.BOLD)), 8);
		this.id = id;
	}

	public static @NonNull MinecraftFont of(final @NonNull ResourceLocation id) {
		return MinecraftFont.FONTS.computeIfAbsent(id, MinecraftFont::new);
	}

	@Override
	public @NonNull ITextRenderer getTextRenderer() {
		return MinecraftTextRenderer.inst();
	}

	@Override
	public String toString() {
		return this.id.toString();
	}

}