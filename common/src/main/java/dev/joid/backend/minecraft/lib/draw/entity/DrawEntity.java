package dev.joid.backend.minecraft.lib.draw.entity;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawEntity {

	private static final DrawEntity INSTANCE = new DrawEntity();

	private static final ModelPlayer PLAYER     = new ModelPlayer(0F, false);
	private static final Method      PRE_RENDER = ObfuscationReflectionHelper.findMethod(RenderLivingBase.class, "func_77041_b", void.class, EntityLivingBase.class, float.class);

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
		final double[] extent = DrawEntity.getExtent(entity);
		this.drawModel(x, y, width, height, extent, (float) (2D * extent[2]), scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
			final RenderManager manager = Minecraft.getMinecraft().getRenderManager();
			if (!(entity instanceof EntityLivingBase)) {
				DrawEntity.render(manager, entity);
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
				manager.playerViewY = 180F;
				DrawEntity.render(manager, living);
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
		this.drawModel(x, y, width, height, new double[] {0.525D, 0.9D}, 1.8F, scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
			final ModelPlayer model = DrawEntity.PLAYER;
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			GlStateManager.rotate(180F - lookYaw, 0F, 1F, 0F);
			GlStateManager.scale(-0.9375F, -0.9375F, 0.9375F);
			GlStateManager.translate(0F, -1.5078125F, 0F);
			model.bipedHead.rotateAngleY = (float) Math.toRadians(lookYaw);
			model.bipedHead.rotateAngleX = (float) Math.toRadians(lookPitch);
			ModelBase.copyModelAngles(model.bipedHead, model.bipedHeadwear);
			for (final ModelRenderer part : new ModelRenderer[] {model.bipedHead, model.bipedBody, model.bipedRightArm, model.bipedLeftArm, model.bipedRightLeg, model.bipedLeftLeg, model.bipedHeadwear, model.bipedLeftLegwear, model.bipedRightLegwear, model.bipedLeftArmwear, model.bipedRightArmwear, model.bipedBodyWear}) {
				part.render(0.0625F);
			}
		});
	}

	private void drawModel(final double x, final double y, final double width, final double height, final double[] extent, final float modelHeight, final double scale, final double rotationYaw, final double rotationPitch, final double lookX, final double lookY, final ModelDraw draw) {
		if (extent[0] <= 0D || modelHeight <= 0F || scale <= 0D) {
			return;
		}

		final double unit = Math.min(width / (2D * extent[0]), height / (2D * extent[1])) * scale;
		final float lookYaw = (float) Math.atan((x + width / 2D - lookX) * 0.75D / unit) * 20F;
		final float lookPitch = -(float) Math.atan((y + height / 2D - lookY) * 0.75D / unit) * 20F;
		DrawUtils.RASTER.drawRaster(x - width / 4D, y - height / 4D, width * 1.5D, height * 1.5D, GuiDrawableRaster.of((rasterWidth, rasterHeight) -> {
			final float size = (float) (unit * rasterWidth / (width * 1.5D));
			GlStateManager.enableColorMaterial();
			GlStateManager.translate(rasterWidth / 2F, rasterHeight / 2F, 0F);
			GlStateManager.scale(-size, size, size);
			GlStateManager.rotate(180F, 0F, 0F, 1F);
			GlStateManager.rotate(135F, 0F, 1F, 0F);
			RenderHelper.enableStandardItemLighting();
			GlStateManager.rotate(-135F, 0F, 1F, 0F);
			GlStateManager.rotate(lookPitch, 1F, 0F, 0F);
			GlStateManager.rotate((float) rotationPitch, 1F, 0F, 0F);
			GlStateManager.rotate((float) rotationYaw, 0F, 1F, 0F);
			GlStateManager.translate(0F, -modelHeight / 2F, 0F);
			draw.draw(lookYaw, lookPitch);
		}));
	}

	private static double[] getExtent(final Entity entity) {
		final double[] extent = {1.75D * entity.width / 2D, entity.height / 2D, entity.height / 2D};
		final Render<Entity> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
		if (!(render instanceof RenderLivingBase) || entity.height <= 0F) {
			return extent;
		}

		final ModelBase model = ((RenderLivingBase<?>) render).getMainModel();
		model.setRotationAngles(0F, 0F, entity.ticksExisted, 0F, 0F, 0.0625F, entity);
		final Set<ModelRenderer> children = new HashSet<>();
		for (final ModelRenderer part : model.boxList) {
			if (part.childModels != null) {
				children.addAll(part.childModels);
			}
		}

		final double[] bounds = {0D, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
		for (final ModelRenderer part : model.boxList) {
			if (!children.contains(part)) {
				DrawEntity.visit(part, new Matrix4f(), bounds);
			}
		}

		if (bounds[2] <= bounds[1]) {
			return extent;
		}

		final double scale = DrawEntity.getRenderScale((RenderLivingBase<?>) render, (EntityLivingBase) entity);
		final double top = scale * (1.5078125D - bounds[1]);
		final double bottom = scale * (1.5078125D - bounds[2]);
		extent[0] = scale * bounds[0];
		extent[1] = (top - bottom) / 2D;
		extent[2] = (top + bottom) / 2D;
		return extent;
	}

	private static double getRenderScale(final RenderLivingBase<?> render, final EntityLivingBase entity) {
		final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
		GlStateManager.pushMatrix();
		try {
			GlStateManager.loadIdentity();
			DrawEntity.PRE_RENDER.invoke(render, entity, 1F);
			GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		} catch (final ReflectiveOperationException exception) {
			return 1D;
		} finally {
			GlStateManager.popMatrix();
		}

		return Math.sqrt(matrix.get(0) * matrix.get(0) + matrix.get(1) * matrix.get(1) + matrix.get(2) * matrix.get(2));
	}

	private static void visit(final ModelRenderer part, final Matrix4f parent, final double[] bounds) {
		if (part.isHidden || !part.showModel) {
			return;
		}

		final Matrix4f matrix = new Matrix4f(parent);
		matrix.translate(new Vector3f(part.offsetX + part.rotationPointX / 16F, part.offsetY + part.rotationPointY / 16F, part.offsetZ + part.rotationPointZ / 16F));
		matrix.rotate(part.rotateAngleZ, new Vector3f(0F, 0F, 1F));
		matrix.rotate(part.rotateAngleY, new Vector3f(0F, 1F, 0F));
		matrix.rotate(part.rotateAngleX, new Vector3f(1F, 0F, 0F));
		final Vector4f corner = new Vector4f();
		for (final ModelBox cube : part.cubeList) {
			for (int index = 0; index < 8; index++) {
				corner.set((index & 1) == 0 ? cube.posX1 / 16F : cube.posX2 / 16F, (index & 2) == 0 ? cube.posY1 / 16F : cube.posY2 / 16F, (index & 4) == 0 ? cube.posZ1 / 16F : cube.posZ2 / 16F, 1F);
				Matrix4f.transform(matrix, corner, corner);
				bounds[0] = Math.max(bounds[0], Math.sqrt(corner.x * corner.x + corner.z * corner.z));
				bounds[1] = Math.min(bounds[1], corner.y);
				bounds[2] = Math.max(bounds[2], corner.y);
			}
		}

		if (part.childModels != null) {
			for (final ModelRenderer child : part.childModels) {
				DrawEntity.visit(child, matrix, bounds);
			}
		}
	}

	private static void render(final RenderManager manager, final Entity entity) {
		manager.setRenderShadow(false);
		try {
			manager.renderEntity(entity, 0D, 0D, 0D, 0F, 1F, false);
		} finally {
			manager.setRenderShadow(true);
		}
	}

	@FunctionalInterface
	private interface ModelDraw {

		public void draw(final float lookYaw, final float lookPitch);

	}

}