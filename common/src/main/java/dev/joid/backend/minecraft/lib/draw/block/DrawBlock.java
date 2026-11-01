package dev.joid.backend.minecraft.lib.draw.block;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawBlock {

	private static final DrawBlock INSTANCE = new DrawBlock();

	public static @NonNull DrawBlock inst() {
		return DrawBlock.INSTANCE;
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull IBlockState state) {
		this.drawBlock(x, y, size, state, 225D, 30D);
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull IBlockState state, final double rotationYaw, final double rotationPitch) {
		if (state.getBlock().getRenderType() == -1) {
			return;
		}

		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of((width, height) -> {
			final float scale = Math.min(width, height);
			RenderHelper.enableGUIStandardItemLighting();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			GlStateManager.translate(width / 2F, height / 2F, 0F);
			GlStateManager.scale(scale, -scale, scale);
			GlStateManager.rotate((float) rotationPitch, 1F, 0F, 0F);
			GlStateManager.rotate((float) rotationYaw, 0F, 1F, 0F);
			GlStateManager.scale(0.625F, 0.625F, 0.625F);
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.5F);
			Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1F);
		}));
	}

}