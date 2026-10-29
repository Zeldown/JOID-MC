package dev.joid.backend.minecraft.lib.draw.item;

import org.lwjgl.opengl.GL11;

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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawItem {

	private static final DrawItem INSTANCE = new DrawItem();

	private static final RenderItem RENDER = new RenderItem();

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

		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of((width, height) -> {
			final Minecraft minecraft = Minecraft.getMinecraft();
			final float scale = Math.min(width, height) / 16F;
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glTranslatef((width - 16F * scale) / 2F, (height - 16F * scale) / 2F, 0F);
			GL11.glScalef(scale, scale, scale);
			if (glint == null) {
				DrawItem.RENDER.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), stack, 0, 0);
				return;
			}

			DrawItem.RENDER.renderItemIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), stack, 0, 0, false);
			if (glint) {
				DrawItem.RENDER.renderEffect(minecraft.getTextureManager(), 0, 0);
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
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 12D * unit, unit, Color.rgb((255 - green) / 4 << 16 | 0x3F00));
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

}