package dev.joid.backend.minecraft.lib.draw.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawItem {

	private static final DrawItem INSTANCE = new DrawItem();

	private static final Color COOLDOWN = Color.argb(0x7FFFFFFF).lock();

	private static final VertexConsumer NO_VERTEX = new NoVertexConsumer();

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

		final Minecraft minecraft = Minecraft.getInstance();
		final ItemStack drawn = glint != null && glint && !stack.hasFoil() ? DrawItem.withFoil(stack) : stack;
		final BakedModel model = minecraft.getItemRenderer().getModel(drawn, minecraft.level, minecraft.player, 0);
		DrawUtils.RASTER.drawRaster(x, y, size, size, GuiDrawableRaster.of(model.usesBlockLight() ? Lighting::setupFor3DItems : Lighting::setupForFlatItems, (pose, buffer) -> {
			final MultiBufferSource source = glint != null && !glint ? type -> DrawItem.isGlint(type) ? DrawItem.NO_VERTEX : buffer.getBuffer(type) : buffer;
			minecraft.getItemRenderer().render(drawn, ItemDisplayContext.GUI, false, pose, source, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
		}));
	}

	public void drawItemBar(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.isEmpty() || !stack.isBarVisible()) {
			return;
		}

		final double unit = size / 16D;
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 13D * unit, 2D * unit, Color.BLACK);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, stack.getBarWidth() * unit, unit, Color.rgb(stack.getBarColor()));
	}

	public void drawItemCooldown(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (stack.isEmpty() || minecraft.player == null) {
			return;
		}

		final float cooldown = minecraft.player.getCooldowns().getCooldownPercent(stack.getItem(), minecraft.getTimer().getGameTimeDeltaPartialTick(true));
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

	private static boolean isGlint(final RenderType type) {
		return type == RenderType.glint() || type == RenderType.glintTranslucent() || type == RenderType.entityGlint() || type == RenderType.entityGlintDirect() || type == RenderType.armorEntityGlint();
	}

	private static ItemStack withFoil(final ItemStack stack) {
		final ItemStack copy = stack.copy();
		copy.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
		return copy;
	}

	private static final class NoVertexConsumer implements VertexConsumer {

		@Override
		public VertexConsumer addVertex(final float x, final float y, final float z) {
			return this;
		}

		@Override
		public VertexConsumer setColor(final int red, final int green, final int blue, final int alpha) {
			return this;
		}

		@Override
		public VertexConsumer setUv(final float u, final float v) {
			return this;
		}

		@Override
		public VertexConsumer setUv1(final int u, final int v) {
			return this;
		}

		@Override
		public VertexConsumer setUv2(final int u, final int v) {
			return this;
		}

		@Override
		public VertexConsumer setNormal(final float x, final float y, final float z) {
			return this;
		}

	}

}