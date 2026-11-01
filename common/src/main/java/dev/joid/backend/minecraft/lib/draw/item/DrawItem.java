package dev.joid.backend.minecraft.lib.draw.item;

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
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.ForgeHooksClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawItem {

	private static final DrawItem INSTANCE = new DrawItem();

	private static final Color COOLDOWN = Color.argb(0x7FFFFFFF).lock();

	public static @NonNull DrawItem inst() {
		return DrawItem.INSTANCE;
	}

	public void drawItem(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		this.drawItem(x, y, size, stack, null);
	}

	public void drawItem(final double x, final double y, final double size, final @NonNull ItemStack stack, final Boolean glint) {
		if (stack.isEmpty()) {
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
		if (stack.isEmpty() || !stack.getItem().showDurabilityBar(stack)) {
			return;
		}

		final double unit = size / 16D;
		final double health = stack.getItem().getDurabilityForDisplay(stack);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 13D * unit, 2D * unit, Color.BLACK);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, Math.round(13D - health * 13D) * unit, unit, Color.rgb(stack.getItem().getRGBDurabilityForDisplay(stack)));
	}

	public void drawItemCooldown(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		if (stack.isEmpty() || minecraft.player == null) {
			return;
		}

		final float cooldown = minecraft.player.getCooldownTracker().getCooldown(stack.getItem(), minecraft.getRenderPartialTicks());
		if (cooldown <= 0F) {
			return;
		}

		final double unit = size / 16D;
		final double top = Math.floor(16F * (1F - cooldown));
		DrawUtils.SHAPE.drawRect(x, y + top * unit, size, Math.ceil(16F * cooldown) * unit, DrawItem.COOLDOWN);
	}

	public void drawItemCount(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.getCount() != 1) {
			this.drawItemCount(x, y, size, stack, String.valueOf(stack.getCount()));
		}
	}

	public void drawItemCount(final double x, final double y, final double size, final @NonNull ItemStack stack, final @NonNull String count) {
		if (stack.isEmpty()) {
			return;
		}

		final double unit = size / 16D;
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, (float) (MinecraftFont.SIZE * unit)).color(Color.WHITE).shadowTint(0.25F).shadow((float) unit, (float) unit);
		DrawUtils.TEXT.drawText(x + 17D * unit - info.getWidth(count), y + 9D * unit, count, info, Align.START, Align.START);
	}

	private static void drawWithoutEffect(final ItemStack stack) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final RenderItem render = minecraft.getRenderItem();
		final IBakedModel model = render.getItemModelWithOverrides(stack, null, minecraft.player);
		final ITextureObject atlas = minecraft.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		atlas.setBlurMipmap(false, false);
		GlStateManager.pushMatrix();
		try {
			GlStateManager.translate(8F, 8F, 150F);
			GlStateManager.scale(16F, -16F, 16F);
			if (model.isGui3d()) {
				GlStateManager.enableLighting();
			} else {
				GlStateManager.disableLighting();
			}

			final IBakedModel transformed = ForgeHooksClient.handleCameraTransforms(model, TransformType.GUI, false);
			if (transformed.isBuiltInRenderer()) {
				render.renderItem(stack, transformed);
			} else {
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
				ForgeHooksClient.renderLitItem(render, transformed, -1, stack);
			}
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