package dev.joid.backend.minecraft.lib.draw.entity;

import org.lwjgl.opengl.GL11;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawEntity {

	private static final DrawEntity INSTANCE = new DrawEntity();

	private static final ModelBiped PLAYER = new ModelBiped(0F);

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
		this.drawModel(x, y, width, height, entity.width, entity.height, scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
			if (!(entity instanceof EntityLivingBase)) {
				RenderManager.instance.renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, 1F);
				return;
			}

			final EntityLivingBase living = (EntityLivingBase) entity;
			final float bodyYaw = living.renderYawOffset;
			final float yaw = living.rotationYaw;
			final float pitch = living.rotationPitch;
			final float previousHeadYaw = living.prevRotationYawHead;
			final float headYaw = living.rotationYawHead;
			living.renderYawOffset     = lookYaw;
			living.rotationYaw         = lookYaw * 2F;
			living.rotationPitch       = lookPitch;
			living.rotationYawHead     = living.rotationYaw;
			living.prevRotationYawHead = living.rotationYaw;
			try {
				GL11.glTranslatef(0F, living.yOffset, 0F);
				RenderManager.instance.playerViewY = 180F;
				RenderManager.instance.renderEntityWithPosYaw(living, 0D, 0D, 0D, 0F, 1F);
			} finally {
				living.renderYawOffset     = bodyYaw;
				living.rotationYaw         = yaw;
				living.rotationPitch       = pitch;
				living.prevRotationYawHead = previousHeadYaw;
				living.rotationYawHead     = headYaw;
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
		this.drawModel(x, y, width, height, 0.6F, 1.8F, scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			GL11.glRotatef(180F - lookYaw, 0F, 1F, 0F);
			GL11.glScalef(-0.9375F, -0.9375F, 0.9375F);
			GL11.glTranslatef(0F, -1.5078125F, 0F);
			DrawEntity.PLAYER.render(null, 0F, 0F, 0F, lookYaw, lookPitch, 0.0625F);
		});
	}

	private void drawModel(final double x, final double y, final double width, final double height, final float modelWidth, final float modelHeight, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY, final ModelDraw draw) {
		if (modelWidth <= 0F || modelHeight <= 0F || scale <= 0D) {
			return;
		}

		final double unit = Math.min(width / (1.75D * modelWidth), height / modelHeight) * scale;
		final float lookYaw = (float) Math.atan((x + width / 2D - lookX) * 0.75D / unit) * 20F;
		final float lookPitch = -(float) Math.atan((y + height / 2D - lookY) * 0.75D / unit) * 20F;
		DrawUtils.RASTER.drawRaster(x - width / 4D, y - height / 4D, width * 1.5D, height * 1.5D, GuiDrawableRaster.of((rasterWidth, rasterHeight) -> {
			final float size = (float) (unit * rasterWidth / (width * 1.5D));
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glTranslatef(rasterWidth / 2F, rasterHeight / 2F, 0F);
			GL11.glScalef(-size, size, size);
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(135F, 0F, 1F, 0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glRotatef(-135F, 0F, 1F, 0F);
			GL11.glRotatef(lookPitch, 1F, 0F, 0F);
			GL11.glRotatef((float) rotationPitch, 1F, 0F, 0F);
			GL11.glRotatef((float) rotationYaw, 0F, 1F, 0F);
			GL11.glTranslatef(0F, -modelHeight / 2F, 0F);
			draw.draw(lookYaw, lookPitch);
		}));
	}

	@FunctionalInterface
	private interface ModelDraw {

		public void draw(final float lookYaw, final float lookPitch);

	}

}