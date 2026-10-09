package dev.joid.backend.minecraft.lib.draw.entity;

import org.joml.Quaternionf;

import dev.joid.backend.minecraft.lib.draw.raster.FeatureExternalRasterDrawable;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerSkin;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawEntity {

	private static final DrawEntity INSTANCE = new DrawEntity();

	public static @NonNull DrawEntity inst() {
		return DrawEntity.INSTANCE;
	}

	public void drawEntity(final double x, final double y, final double width, final double height, final @NonNull Entity entity) {
		this.drawEntity(x, y, width, height, entity, 0D, 0D);
	}

	public void drawEntity(final double x, final double y, final double width, final double height, final @NonNull Entity entity, final double rotationYaw, final double rotationPitch) {
		this.drawEntity(x, y, width, height, entity, 1D, rotationYaw, rotationPitch, x + width / 2D, y + height / 2D);
	}

	public void drawEntity(final double x, final double y, final double width, final double height, final @NonNull Entity entity, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY) {
		final EntityRenderState state = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).createRenderState(entity, 1F);
		state.shadowPieces.clear();
		state.outlineColor = EntityRenderState.NO_OUTLINE;
		this.drawState(x, y, width, height, state, scale, rotationYaw, rotationPitch, lookX, lookY);
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull PlayerSkin skin) {
		this.drawPlayer(x, y, width, height, skin, 0D, 0D);
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull PlayerSkin skin, final double rotationYaw, final double rotationPitch) {
		this.drawPlayer(x, y, width, height, skin, 1D, rotationYaw, rotationPitch, x + width / 2D, y + height / 2D);
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull PlayerSkin skin, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY) {
		final AvatarRenderState state = new AvatarRenderState();
		state.entityType        = EntityTypes.PLAYER;
		state.boundingBoxWidth  = EntityTypes.PLAYER.getWidth();
		state.boundingBoxHeight = EntityTypes.PLAYER.getHeight();
		state.eyeHeight         = EntityTypes.PLAYER.getDimensions().eyeHeight();
		state.skin              = skin;
		this.drawState(x, y, width, height, state, scale, rotationYaw, rotationPitch, lookX, lookY);
	}

	private void drawState(final double x, final double y, final double width, final double height, final EntityRenderState state, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY) {
		if (state instanceof final LivingEntityRenderState living) {
			living.boundingBoxWidth  = living.boundingBoxWidth / living.scale;
			living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
			living.scale             = 1F;
		}

		if (state.boundingBoxWidth <= 0F || state.boundingBoxHeight <= 0F || scale <= 0D) {
			return;
		}

		final double unit = Math.min(width / state.boundingBoxWidth, height / state.boundingBoxHeight) * scale;
		final float lookYaw = (float) Math.atan((x + width / 2D - lookX) * 0.75D / unit);
		final float lookPitch = (float) Math.atan((y + height / 2D - lookY) * 0.75D / unit);
		if (state instanceof final LivingEntityRenderState living) {
			living.bodyRot = 180F + lookYaw * 20F;
			living.yRot    = lookYaw * 20F;
			living.xRot    = living.pose == Pose.FALL_FLYING ? 0F : -lookPitch * 20F;
		}

		state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
		final Quaternionf tilt = new Quaternionf().rotationX((float) Math.toRadians(lookPitch * 20F));
		final CameraRenderState camera = new CameraRenderState();
		camera.orientation = tilt.conjugate(new Quaternionf()).rotateY((float) Math.PI);
		final float size = (float) (unit / Math.min(width, height) / 1.5D);
		final float center = state.boundingBoxHeight / 2F;
		DrawUtils.RASTER.drawRaster(x - width / 4D, y - height / 4D, width * 1.5D, height * 1.5D, FeatureExternalRasterDrawable.create(Lighting.Entry.ENTITY_IN_UI, (pose, collector) -> {
			pose.scale(size, -size, -size);
			pose.translate(0F, center, 0F);
			pose.mulPose(new Quaternionf().rotationZ((float) Math.PI).mul(tilt));
			pose.translate(0F, center, 0F);
			pose.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(-rotationPitch), (float) Math.toRadians(rotationYaw), 0F));
			pose.translate(0F, -center, 0F);
			Minecraft.getInstance().getEntityRenderDispatcher().submit(state, camera, 0D, 0D, 0D, pose, collector);
		}));
	}

}