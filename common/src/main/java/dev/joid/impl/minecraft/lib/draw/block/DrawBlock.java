package dev.joid.impl.minecraft.lib.draw.block;

import org.joml.Quaternionf;

import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.lib.bridge.BridgeHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
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
		final BlockModelRenderState model = new BlockModelRenderState();
		new BlockModelResolver(Minecraft.getInstance().getModelManager()).update(model, state, DisplayRenderer.BLOCK_DISPLAY_CONTEXT);
		if (model.isEmpty()) {
			return;
		}

		((RenderBridge) BridgeHandler.RENDER.get()).getRasterizer().draw(x, y, size, size, Lighting.Entry.ITEMS_3D, (pose, collector) -> {
			pose.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(rotationPitch), (float) Math.toRadians(rotationYaw), 0F));
			pose.scale(0.625F, 0.625F, 0.625F);
			pose.translate(-0.5F, -0.5F, -0.5F);
			model.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
		});
	}

}