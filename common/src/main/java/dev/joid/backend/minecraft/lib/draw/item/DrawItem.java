package dev.joid.backend.minecraft.lib.draw.item;

import java.lang.reflect.Method;

import dev.joid.backend.minecraft.lib.draw.raster.GuiDrawableRaster;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.utils.align.Align;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawItem {

	private static final DrawItem INSTANCE = new DrawItem();

	private static final Method RENDER_MODEL = ReflectionHelper.findMethod(RenderItem.class, null, new String[] {"renderModel", "func_175036_a"}, IBakedModel.class, ItemStack.class);

	public static @NonNull DrawItem inst() {
		return DrawItem.INSTANCE;
	}

	public void drawItem(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		this.drawItem(x, y, size, stack, null);
	}

	public void drawItem(final double x, final double y, final double size, final @NonNull ItemStack stack, final Boolean glint) {
		if (stack.getItem() == null) {
			return;
		}

		final ItemStack drawn = glint != null && glint && !stack.hasEffect() ? DrawItem.withEffect(stack) : stack;
		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of((width, height) -> {
			final float scale = Math.min(width, height) / 16F;
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.translate((width - 16F * scale) / 2F, (height - 16F * scale) / 2F, 0F);
			GlStateManager.scale(scale, scale, scale);
			if (glint != null && !glint && drawn.hasEffect()) {
				DrawItem.drawWithoutEffect(drawn);
			} else {
				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(drawn, 0, 0);
			}
		}));
	}

	public void drawItemBar(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.getItem() == null || !stack.getItem().showDurabilityBar(stack)) {
			return;
		}

		final double unit = size / 16D;
		final double health = stack.getItem().getDurabilityForDisplay(stack);
		final int green = (int) Math.round(255D - health * 255D);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 13D * unit, 2D * unit, Color.BLACK);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 12D * unit, unit, Color.rgb((255 - green) / 4 << 16 | 0x4000));
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, Math.round(13D - health * 13D) * unit, unit, Color.rgb(255 - green << 16 | green << 8));
	}

	public void drawItemCount(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.stackSize != 1) {
			this.drawItemCount(x, y, size, stack, String.valueOf(stack.stackSize));
		}
	}

	public void drawItemCount(final double x, final double y, final double size, final @NonNull ItemStack stack, final @NonNull String count) {
		if (stack.getItem() == null) {
			return;
		}

		final double unit = size / 16D;
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, (float) (MinecraftFont.SIZE * unit)).color(Color.WHITE).shadowTint(0.25F).shadow((float) unit, (float) unit);
		DrawUtils.TEXT.drawText(x + 17D * unit - info.getWidth(count), y + 9D * unit, count, info, Align.START, Align.START);
	}

	@SuppressWarnings("deprecation")
	private static void drawWithoutEffect(final ItemStack stack) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final RenderItem render = minecraft.getRenderItem();
		final IBakedModel model = render.getItemModelMesher().getItemModel(stack);
		final ITextureObject atlas = minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture);
		minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		atlas.setBlurMipmap(false, false);
		GlStateManager.pushMatrix();
		try {
			GlStateManager.translate(8F, 8F, 150F);
			GlStateManager.scale(0.5F, 0.5F, -0.5F);
			if (model.isGui3d()) {
				GlStateManager.scale(40F, 40F, 40F);
				GlStateManager.rotate(210F, 1F, 0F, 0F);
				GlStateManager.rotate(-135F, 0F, 1F, 0F);
				GlStateManager.enableLighting();
			} else {
				GlStateManager.scale(64F, 64F, 64F);
				GlStateManager.rotate(180F, 1F, 0F, 0F);
				GlStateManager.disableLighting();
			}

			final IBakedModel transformed = ForgeHooksClient.handleCameraTransforms(model, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GUI);
			if (transformed.isBuiltInRenderer()) {
				render.renderItem(stack, transformed);
			} else {
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				DrawItem.RENDER_MODEL.invoke(render, transformed, stack);
			}
		} catch (final ReflectiveOperationException exception) {
			throw new IllegalStateException("The model of " + stack + " cannot be drawn without its glint", exception);
		} finally {
			GlStateManager.popMatrix();
			atlas.restoreLastBlurMipmap();
		}
	}

	private static ItemStack withEffect(final ItemStack stack) {
		final ItemStack copy = stack.copy();
		final NBTTagList enchantments = new NBTTagList();
		enchantments.appendTag(new NBTTagCompound());
		if (!copy.hasTagCompound()) {
			copy.setTagCompound(new NBTTagCompound());
		}

		copy.getTagCompound().setTag("ench", enchantments);
		return copy;
	}

}