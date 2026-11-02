package dev.joid.backend.minecraft.lib.draw.block;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Quaternion;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawBlock {

	private static final DrawBlock INSTANCE = new DrawBlock();

	public static @NonNull DrawBlock inst() {
		return DrawBlock.INSTANCE;
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull BlockState state) {
		this.drawBlock(x, y, size, state, 225D, 30D);
	}

	public void drawBlock(final double x, final double y, final double size, final @NonNull BlockState state, final double rotationYaw, final double rotationPitch) {
		if (state.getRenderShape() == RenderShape.INVISIBLE) {
			return;
		}

		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of(Lighting::setupFor3DItems, (pose, buffer) -> {
			pose.mulPose(Quaternion.fromXYZ((float) Math.toRadians(rotationPitch), (float) Math.toRadians(rotationYaw), 0F));
			pose.scale(0.625F, 0.625F, 0.625F);
			pose.translate(-0.5F, -0.5F, -0.5F);
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
		}));
	}

}