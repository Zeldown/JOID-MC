package dev.joid.impl.minecraft.lib.draw.item;

import dev.joid.impl.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.impl.minecraft.render.RenderBridge;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.DrawUtils;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.utils.align.Align;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Lighting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DrawItem {

	private static final DrawItem INSTANCE = new DrawItem();

	private static final Color SHADOW   = new Color(0xFF3F3F3F);
	private static final Color COOLDOWN = new Color(0x7FFFFFFF);

	public static @NonNull DrawItem inst() {
		return DrawItem.INSTANCE;
	}

	public void drawItem(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final ItemStackRenderState state = new ItemStackRenderState();
		minecraft.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
		((RenderBridge) BridgeHandler.RENDER.get()).getRasterizer().draw(x, y, size, size, state.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT, (pose, collector) -> state.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0));
	}

	public void drawItemBar(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.isEmpty() || !stack.isBarVisible()) {
			return;
		}

		final double unit = size / 16D;
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, 13D * unit, 2D * unit, Color.BLACK);
		DrawUtils.SHAPE.drawRect(x + 2D * unit, y + 13D * unit, stack.getBarWidth() * unit, unit, new Color(0xFF000000 | stack.getBarColor()));
	}

	public void drawItemCooldown(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (stack.isEmpty() || minecraft.player == null) {
			return;
		}

		final float cooldown = minecraft.player.getCooldowns().getCooldownPercent(stack, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
		if (cooldown <= 0F) {
			return;
		}

		final double unit = size / 16D;
		final double top = Math.floor(16F * (1F - cooldown));
		DrawUtils.SHAPE.drawRect(x, y + top * unit, size, Math.ceil(16F * cooldown) * unit, DrawItem.COOLDOWN);
	}

	public void drawItemCount(final double x, final double y, final double size, final @NonNull ItemStack stack) {
		if (stack.isEmpty() || stack.getCount() == 1) {
			return;
		}

		final double unit = size / 16D;
		final String count = String.valueOf(stack.getCount());
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, (float) (MinecraftFont.SIZE * unit), Color.WHITE).shadow(DrawItem.SHADOW).shadow((float) unit, (float) unit);
		DrawUtils.TEXT.drawText(x + 17D * unit - info.getWidth(count), y + 9D * unit, count, info, Align.START, Align.START);
	}

}