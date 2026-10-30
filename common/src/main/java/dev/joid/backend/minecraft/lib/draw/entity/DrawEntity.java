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

import cpw.mods.fml.relauncher.ReflectionHelper;
import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.lib.draw.DrawUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawEntity {

	private static final DrawEntity INSTANCE = new DrawEntity();

	private static final ModelBiped PLAYER = new ModelBiped(0F);
	private static final Method PRE_RENDER = ReflectionHelper.findMethod(RendererLivingEntity.class, null, new String[] {"preRenderCallback", "func_77041_b"}, EntityLivingBase.class, float.class);

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
		this.drawModel(x, y, width, height, DrawEntity.getExtent(entity), entity.height, scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
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
		this.drawModel(x, y, width, height, new double[] {0.525D, 0.9D}, 1.8F, scale, rotationYaw, rotationPitch, lookX, lookY, (lookYaw, lookPitch) -> {
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			GL11.glRotatef(180F - lookYaw, 0F, 1F, 0F);
			GL11.glScalef(-0.9375F, -0.9375F, 0.9375F);
			GL11.glTranslatef(0F, -1.5078125F, 0F);
			DrawEntity.PLAYER.render(null, 0F, 0F, 0F, lookYaw, lookPitch, 0.0625F);
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

	private static double[] getExtent(final Entity entity) {
		final double[] extent = {1.75D * entity.width / 2D, entity.height / 2D};
		final Render render = RenderManager.instance.getEntityRenderObject(entity);
		if (!(render instanceof RendererLivingEntity) || entity.height <= 0F) {
			return extent;
		}

		final ModelBase model = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, (RendererLivingEntity) render, "mainModel", "field_77045_g");
		model.setRotationAngles(0F, 0F, entity.ticksExisted, 0F, 0F, 0.0625F, entity);
		final Set<Object> children = new HashSet<>();
		for (final Object part : model.boxList) {
			if (((ModelRenderer) part).childModels != null) {
				children.addAll(((ModelRenderer) part).childModels);
			}
		}

		final double[] bounds = {0D, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
		for (final Object part : model.boxList) {
			if (!children.contains(part)) {
				DrawEntity.visit((ModelRenderer) part, new Matrix4f(), bounds);
			}
		}

		if (bounds[2] <= bounds[1]) {
			return extent;
		}

		final double scale = DrawEntity.getRenderScale((RendererLivingEntity) render, (EntityLivingBase) entity);
		final double factor = Math.max(1D, entity.height / (scale * (bounds[2] - bounds[1])));
		final double center = entity.height / 2D;
		extent[0] = Math.max(entity.width / 2D, scale * bounds[0] * factor);
		extent[1] = Math.max(center, Math.max(scale * (1.5078125D - bounds[1]) * factor - center, center - scale * (1.5078125D - bounds[2]) * factor));
		return extent;
	}

	private static double getRenderScale(final RendererLivingEntity render, final EntityLivingBase entity) {
		final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
		GL11.glPushMatrix();
		try {
			GL11.glLoadIdentity();
			DrawEntity.PRE_RENDER.invoke(render, entity, 1F);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		} catch (final ReflectiveOperationException exception) {
			return 1D;
		} finally {
			GL11.glPopMatrix();
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
		for (final Object box : part.cubeList) {
			final ModelBox cube = (ModelBox) box;
			for (int index = 0; index < 8; index++) {
				corner.set((index & 1) == 0 ? cube.posX1 / 16F : cube.posX2 / 16F, (index & 2) == 0 ? cube.posY1 / 16F : cube.posY2 / 16F, (index & 4) == 0 ? cube.posZ1 / 16F : cube.posZ2 / 16F, 1F);
				Matrix4f.transform(matrix, corner, corner);
				bounds[0] = Math.max(bounds[0], Math.sqrt(corner.x * corner.x + corner.z * corner.z));
				bounds[1] = Math.min(bounds[1], corner.y);
				bounds[2] = Math.max(bounds[2], corner.y);
			}
		}

		if (part.childModels != null) {
			for (final Object child : part.childModels) {
				DrawEntity.visit((ModelRenderer) child, matrix, bounds);
			}
		}
	}

	@FunctionalInterface
	private interface ModelDraw {

		public void draw(final float lookYaw, final float lookPitch);

	}

}