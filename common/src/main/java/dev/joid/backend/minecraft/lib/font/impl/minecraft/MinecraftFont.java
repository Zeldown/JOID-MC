package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import java.util.function.Supplier;

import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.ITextRenderer;
import dev.joid.lib.font.impl.bitmap.BitmapFont;
import dev.joid.lib.font.impl.glyph.FontFamily;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

@Getter
public final class MinecraftFont extends BitmapFont<MinecraftFontFace> {

	public static final float SIZE = 8F;

	public static final MinecraftFont ALT     = new MinecraftFont(new ResourceLocation("textures/font/ascii_sga.png"), () -> Minecraft.getMinecraft().standardGalacticFontRenderer);
	public static final MinecraftFont DEFAULT = new MinecraftFont(new ResourceLocation("textures/font/ascii.png"), () -> Minecraft.getMinecraft().fontRenderer);

	private final ResourceLocation texture;

	private MinecraftFont(final ResourceLocation texture, final Supplier<FontRenderer> renderer) {
		super(FontFamily.of(MinecraftFontFace.of(texture, renderer, FontWeight.REGULAR), MinecraftFontFace.of(texture, renderer, FontWeight.BOLD)), 8);
		this.texture = texture;
	}

	@Override
	public @NonNull ITextRenderer getTextRenderer() {
		return MinecraftTextRenderer.inst();
	}

	@Override
	public String toString() {
		return this.texture.toString();
	}

}