package dev.joid.backend.minecraft.lib.font.impl.minecraft;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.dto.GlyphVertexCapture;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.dto.MinecraftFontFace;
import dev.joid.backend.minecraft.render.texture.BorrowedTexture;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.bridge.render.IRenderBridge;
import dev.joid.lib.bridge.render.matrix.PixelGrid;
import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderStage;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import dev.joid.lib.color.Color;
import dev.joid.lib.color.ColorGradient;
import dev.joid.lib.font.impl.glyph.GlyphFontProvider;
import dev.joid.lib.font.impl.glyph.dto.TextGlyph;
import dev.joid.lib.render.tessellator.DrawMode;
import dev.joid.lib.render.tessellator.Tessellator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftFontProvider extends GlyphFontProvider<MinecraftFontFace> {

	private static final Matrix4fc             IDENTITY = new Matrix4f();
	private static final MinecraftFontProvider INSTANCE = new MinecraftFontProvider();

	private final GlyphVertexCapture capture = new GlyphVertexCapture();

	private Color          color;
	private double         runX;
	private double         runY;
	private PixelGrid      grid;
	private double         runWidth;
	private double         runHeight;
	private GpuTextureView view;

	public static @NonNull MinecraftFontProvider inst() {
		return MinecraftFontProvider.INSTANCE;
	}

	@Override
	protected void end() {
		final IRenderBridge render = BridgeHandler.RENDER.get();
		try {
			GlyphShader.SHADER.unbind();
		} finally {
			render.popState();
		}
	}

	@Override
	protected void begin(final double runX, final double runY, final double runWidth, final double runHeight) {
		if (!GlyphShader.SHADER.isActive()) {
			throw new IllegalStateException("The Minecraft glyph shader is not usable");
		}

		final IRenderBridge render = BridgeHandler.RENDER.get();
		this.view = null;
		this.color = null;
		this.runX = runX;
		this.runY = runY;
		this.runWidth = runWidth;
		this.runHeight = runHeight;
		this.grid = render.getPixelGrid();

		render.pushState();
		Color.reset();
		GlyphShader.SHADER.bind();
	}

	@Override
	protected void drawGlyph(final @NonNull TextGlyph<MinecraftFontFace> glyph) {
		final MinecraftFontFace face = glyph.getFace();
		final BakedGlyph baked = face.getGlyph(glyph.getCodepoint());
		final Style style = Style.EMPTY.withBold(face.isBold()).withItalic(glyph.isSlanted());
		final TextRenderable.Styled renderable = baked.createGlyph(0F, 0F, -1, 0, style, face.isBold() ? baked.info().getBoldOffset() : 0F, 0F);
		if (renderable == null) {
			return;
		}

		if (renderable.textureView() != this.view) {
			this.bindView(renderable.textureView());
		}

		if (glyph.getColor() != this.color) {
			this.bindColor(glyph.getColor());
		}

		renderable.render(MinecraftFontProvider.IDENTITY, this.capture.reset(), 0, true);
		final double scale = glyph.getSize() / MinecraftFont.SIZE;
		final double left = this.grid.snapX(glyph.getX()) + glyph.getOffsetX();
		final double top = this.grid.snapY(glyph.getBaseline()) + glyph.getOffsetY() - glyph.getAscender();
		final Tessellator tessellator = Tessellator.inst();
		tessellator.start(DrawMode.QUADS);
		for (int vertex = 0; vertex < this.capture.getCount(); vertex++) {
			tessellator.addVertexWithUV(left + this.capture.getX(vertex) * scale, top + this.capture.getY(vertex) * scale, 0D, this.capture.getU(vertex), this.capture.getV(vertex));
		}
		tessellator.draw();
	}

	private void bindView(final @NonNull GpuTextureView view) {
		this.view = view;
		BridgeHandler.RENDER.get().texture(BorrowedTexture.of(view), TextureFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE);
		GlyphShader.SHADER.uniform("grayscale", view.texture().getFormat() == GpuFormat.R8_UNORM);
	}

	private void bindColor(final @NonNull Color color) {
		this.color = color;
		final Color current = color.update();
		GlyphShader.SHADER.uniform("color", current.r, current.g, current.b, current.a);
		if (!current.isGradient()) {
			GlyphShader.SHADER.uniform("u_HasGradient", 0);
			return;
		}

		final ColorGradient gradient = current.gradient;
		GlyphShader.SHADER
		.uniform("u_HasGradient", 1)
		.uniform("u_GradientStart", gradient.getStartColor().r, gradient.getStartColor().g, gradient.getStartColor().b, gradient.getStartColor().a)
		.uniform("u_GradientEnd", gradient.getEndColor().r, gradient.getEndColor().g, gradient.getEndColor().b, gradient.getEndColor().a)
		.uniform("u_GradientStartPos", gradient.getDirection().x, gradient.getDirection().y)
		.uniform("u_GradientEndPos", gradient.getDirection().z, gradient.getDirection().w)
		.uniform("u_GradientCanvas", (float) this.runX, (float) this.runY, (float) (this.runX + this.runWidth), (float) (this.runY + this.runHeight));
	}

	private static final class GlyphShader {

		private static final IShader SHADER = BridgeHandler.RENDER.get().createShader(ShaderSource.read(ShaderStage.VERTEX, MinecraftFontProvider.class.getResourceAsStream("/assets/joid/shaders/font/glyph.vsh")), ShaderSource.read(ShaderStage.FRAGMENT, MinecraftFontProvider.class.getResourceAsStream("/assets/joid/shaders/font/glyph.fsh")), BlendState.NORMAL);

	}

}