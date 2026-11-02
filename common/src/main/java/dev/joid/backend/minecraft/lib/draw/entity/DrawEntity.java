package dev.joid.backend.minecraft.lib.draw.entity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawEntity {

	private static final DrawEntity INSTANCE = new DrawEntity();

	private PlayerModel<LivingEntity> player;

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
		final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		final double[] extent = DrawEntity.getExtent(entity.getBbWidth(), entity.getBbHeight(), dispatcher.getRenderer(entity) instanceof final LivingEntityRenderer<?, ?> renderer ? DrawEntity.setupModel(renderer, entity) : null);
		this.drawModel(x, y, width, height, extent, entity.getBbHeight(), scale, rotationYaw, rotationPitch, lookX, lookY, (pose, buffer, lookYaw, lookPitch) -> {
			if (!(entity instanceof final LivingEntity living)) {
				dispatcher.render(entity, 0D, 0D, 0D, 0F, 1F, pose, buffer, LightTexture.FULL_BRIGHT);
				return;
			}

			final float bodyYaw = living.yBodyRot;
			final float yaw = living.getYRot();
			final float pitch = living.getXRot();
			final float previousHeadYaw = living.yHeadRotO;
			final float headYaw = living.yHeadRot;
			living.yBodyRot = 180F + lookYaw;
			living.setYRot(180F + lookYaw * 2F);
			living.setXRot(living.getPose() == Pose.FALL_FLYING ? 0F : -lookPitch);
			living.yHeadRot  = living.getYRot();
			living.yHeadRotO = living.getYRot();
			try {
				dispatcher.render(living, 0D, 0D, 0D, 0F, 1F, pose, buffer, LightTexture.FULL_BRIGHT);
			} finally {
				living.yBodyRot = bodyYaw;
				living.setYRot(yaw);
				living.setXRot(pitch);
				living.yHeadRotO = previousHeadYaw;
				living.yHeadRot  = headYaw;
			}
		});
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull ResourceLocation skin) {
		this.drawPlayer(x, y, width, height, skin, 0D, 0D);
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull ResourceLocation skin, final double rotationYaw, final double rotationPitch) {
		this.drawPlayer(x, y, width, height, skin, 1D, rotationYaw, rotationPitch, x + width / 2D, y + height / 2D);
	}

	public void drawPlayer(final double x, final double y, final double width, final double height, final @NonNull ResourceLocation skin, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY) {
		if (this.player == null) {
			this.player = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
		}

		final PlayerModel<LivingEntity> model = this.player;
		final float playerHeight = EntityType.PLAYER.getHeight();
		this.drawModel(x, y, width, height, DrawEntity.getExtent(EntityType.PLAYER.getWidth(), playerHeight, model), playerHeight, scale, rotationYaw, rotationPitch, lookX, lookY, (pose, buffer, lookYaw, lookPitch) -> {
			model.head.yRot = (float) Math.toRadians(lookYaw);
			model.head.xRot = (float) Math.toRadians(-lookPitch);
			model.hat.copyFrom(model.head);
			pose.mulPose(Vector3f.YP.rotation((float) Math.toRadians(-lookYaw)));
			pose.scale(-1F, -1F, 1F);
			pose.scale(0.9375F, 0.9375F, 0.9375F);
			pose.translate(0F, -1.501F, 0F);
			model.renderToBuffer(pose, buffer.getBuffer(model.renderType(skin)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		});
	}

	private void drawModel(final double x, final double y, final double width, final double height, final double[] extent, final float modelHeight, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY, final ModelDraw draw) {
		if (extent[0] <= 0D || modelHeight <= 0F || scale <= 0D) {
			return;
		}

		final double unit = Math.min(width / (2D * extent[0]), height / (2D * extent[1])) * scale;
		final float lookYaw = (float) Math.atan((x + width / 2D - lookX) * 0.75D / unit);
		final float lookPitch = (float) Math.atan((y + height / 2D - lookY) * 0.75D / unit);
		final Quaternion tilt = Vector3f.XP.rotation((float) Math.toRadians(lookPitch * 20F));
		final float size = (float) (unit / Math.min(width, height) / 1.5D);
		final float center = modelHeight / 2F;
		DrawUtils.RASTER.drawRaster(x - width / 4D, y - height / 4D, width * 1.5D, height * 1.5D, GuiDrawableRaster.of(Lighting::setupForEntityInInventory, (pose, buffer) -> {
			final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
			pose.mulPoseMatrix(Matrix4f.createScaleMatrix(size, -size, -size));
			pose.translate(0F, center, 0F);
			pose.mulPose(Vector3f.ZP.rotation((float) Math.PI));
			pose.mulPose(tilt);
			pose.translate(0F, center, 0F);
			pose.mulPose(Quaternion.fromXYZ((float) Math.toRadians(-rotationPitch), (float) Math.toRadians(rotationYaw), 0F));
			pose.translate(0F, -center, 0F);
			final Quaternion camera = tilt.copy();
			camera.conj();
			camera.mul(Vector3f.YP.rotation((float) Math.PI));
			dispatcher.overrideCameraOrientation(camera);
			dispatcher.setRenderShadow(false);
			try {
				draw.draw(pose, buffer, lookYaw * 20F, lookPitch * 20F);
			} finally {
				dispatcher.setRenderShadow(true);
			}
		}));
	}

	private static double[] getExtent(final float boxWidth, final float boxHeight, final EntityModel<?> model) {
		final double[] extent = {boxWidth / 2D, boxHeight / 2D};
		if (model == null) {
			return extent;
		}

		final BoundsCapture bounds = new BoundsCapture();
		model.renderToBuffer(new PoseStack(), bounds, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		if (bounds.maxY <= bounds.minY) {
			return extent;
		}

		final double factor = Math.max(1D, boxHeight / (bounds.maxY - bounds.minY));
		final double center = boxHeight / 2D;
		extent[0] = Math.max(extent[0], bounds.radius * factor);
		extent[1] = Math.max(extent[1], Math.max((1.501D - bounds.minY) * factor - center, center - (1.501D - bounds.maxY) * factor));
		return extent;
	}

	private static <T extends LivingEntity> EntityModel<T> setupModel(final LivingEntityRenderer<T, ?> renderer, final Entity entity) {
		final EntityModel<T> model = renderer.getModel();
		model.young = entity instanceof final LivingEntity living && living.isBaby();
		model.setupAnim((T) entity, 0F, 0F, entity.tickCount, 0F, 0F);
		return model;
	}

	@FunctionalInterface
	private interface ModelDraw {

		public void draw(final PoseStack pose, final MultiBufferSource buffer, final float lookYaw, final float lookPitch);

	}

	private static final class BoundsCapture implements VertexConsumer {

		private double radius;
		private double minY;
		private double maxY;

		private BoundsCapture() {
			this.minY = Double.POSITIVE_INFINITY;
			this.maxY = Double.NEGATIVE_INFINITY;
		}

		@Override
		public VertexConsumer vertex(final double x, final double y, final double z) {
			this.radius = Math.max(this.radius, Math.sqrt(x * x + z * z));
			this.minY   = Math.min(this.minY, y);
			this.maxY   = Math.max(this.maxY, y);
			return this;
		}

		@Override
		public VertexConsumer color(final int red, final int green, final int blue, final int alpha) {
			return this;
		}

		@Override
		public VertexConsumer uv(final float u, final float v) {
			return this;
		}

		@Override
		public VertexConsumer overlayCoords(final int u, final int v) {
			return this;
		}

		@Override
		public VertexConsumer uv2(final int u, final int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(final float x, final float y, final float z) {
			return this;
		}

		@Override
		public void endVertex() {}

		@Override
		public void defaultColor(final int red, final int green, final int blue, final int alpha) {}

		@Override
		public void unsetDefaultColor() {}

	}

}