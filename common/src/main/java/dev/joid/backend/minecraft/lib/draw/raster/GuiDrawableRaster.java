package dev.joid.backend.minecraft.lib.draw.raster;

import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.bridge.render.MinecraftGlState;
import dev.joid.lib.draw.raster.DrawableRaster;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiDrawableRaster implements DrawableRaster {

	private final DrawableRaster draw;

	public static @NonNull GuiDrawableRaster of(final @NonNull DrawableRaster draw) {
		return new GuiDrawableRaster(draw);
	}

	@Override
	public void draw(final int width, final int height) {
		MinecraftGlState.preserve(() -> {
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0D, width, height, 0D, -10000D, 10000D);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			try {
				GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
				GlStateManager.disableTexture2D();
				GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
				GlStateManager.enableTexture2D();
				GlStateManager.enableDepth();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
				GlStateManager.depthMask(true);
				GlStateManager.enableRescaleNormal();
				GlStateManager.enableAlpha();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1F, 1F, 1F, 1F);
				this.draw.draw(width, height);
			} finally {
				RenderHelper.disableStandardItemLighting();
				GlStateManager.matrixMode(GL11.GL_PROJECTION);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.popMatrix();
			}
		});
	}

}