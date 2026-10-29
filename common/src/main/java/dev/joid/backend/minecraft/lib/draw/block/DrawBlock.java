package dev.joid.backend.minecraft.lib.draw.block;

import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawBlock {

	private static final DrawBlock INSTANCE = new DrawBlock();

	private static final RenderBlocks RENDER = new RenderBlocks();

	public static @NonNull DrawBlock inst() {
		return DrawBlock.INSTANCE;
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull Block block, final int metadata) {
		this.drawBlock(x, y, size, block, metadata, 225D, 30D);
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull Block block, final int metadata, final double rotationYaw, final double rotationPitch) {
		if (!RenderBlocks.renderItemIn3d(block.getRenderType())) {
			return;
		}

		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of((width, height) -> {
			final float scale = Math.min(width, height) * 0.625F;
			RenderHelper.enableGUIStandardItemLighting();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			if (block.getRenderBlockPass() == 0) {
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
			}

			GL11.glTranslatef(width / 2F, height / 2F, 0F);
			GL11.glScalef(scale, scale, -scale);
			GL11.glRotatef(180F + (float) rotationPitch, 1F, 0F, 0F);
			GL11.glRotatef(180F + (float) rotationYaw, 0F, 1F, 0F);
			DrawBlock.RENDER.renderBlockAsItem(block, metadata, 1F);
		}));
	}

}