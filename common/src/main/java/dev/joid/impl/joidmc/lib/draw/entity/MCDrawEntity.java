package dev.joid.impl.joidmc.lib.draw.entity;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.render.raster.RasterLayer;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

public final class MCDrawEntity {

	private static final int   FULL_BRIGHT = 15728880;
	private static final float DEGREE      = (float) Math.PI / 180F;

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double size, final double scale) {
		this.drawEntity(entity, x, y, size, size, scale, 0F, 0F, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale) {
		this.drawEntity(entity, x, y, width, height, scale, 0F, 0F, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale, final float yaw, final float pitch) {
		this.drawEntity(entity, x, y, width, height, scale, yaw, pitch, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale, final float yaw, final float pitch, final float rotationYaw, final float rotationPitch) {
		final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		final EntityRenderState state = dispatcher.getRenderer(entity).createRenderState(entity, 1F);
		state.shadowPieces.clear();
		state.outlineColor = 0;
		state.lightCoords  = MCDrawEntity.FULL_BRIGHT;
		if (state instanceof final LivingEntityRenderState living) {
			living.bodyRot           = 180F + yaw;
			living.yRot              = yaw;
			living.xRot              = pitch;
			living.boundingBoxWidth  = living.boundingBoxWidth / living.scale;
			living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
			living.scale             = 1F;
		}

		final RasterLayer raster = ((RenderBridge) BridgeHandler.RENDER.get()).getRaster();
		final Matrix4f matrix = raster.getMatrix(x + width / 2D, y + height / 2D);
		final Quaternionf camera = new Quaternionf().rotateX(-pitch * MCDrawEntity.DEGREE);
		final Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).mul(camera);
		final Quaternionf orientation = new Quaternionf().rotateY(rotationYaw * MCDrawEntity.DEGREE).rotateX(rotationPitch * MCDrawEntity.DEGREE);
		final float offset = state.boundingBoxHeight / 2F + 0.0625F;
		final ScreenRectangle region = raster.getViewport();
		final GpuTextureView view = raster.render(region, Lighting.Entry.ENTITY_IN_UI, (pose, collector) -> {
			pose.mulPose(matrix);
			pose.scale((float) scale, (float) scale, (float) -scale);
			pose.mulPose(orientation);
			pose.translate(0F, offset, 0F);
			pose.mulPose(rotation);
			final CameraRenderState cameraState = new CameraRenderState();
			cameraState.orientation = camera.conjugate(new Quaternionf()).rotateY((float) Math.PI);
			dispatcher.submit(state, cameraState, 0D, 0D, 0D, pose, collector);
		});

		raster.composite(view, region, Color.WHITE);
	}

}