package dev.joid.impl.joidmc.lib.draw.item;

import org.joml.Matrix4f;

import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.color.Color;
import dev.joid.impl.joidmc.lib.bridge.render.RenderBridge;
import dev.joid.impl.joidmc.lib.render.raster.RasterLayer;
import lombok.NonNull;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class MCDrawItem {

	private static final int FULL_BRIGHT = 15728880;

	private final TrackingItemStackRenderState state;

	public MCDrawItem() {
		this.state = new TrackingItemStackRenderState();
	}

	public void drawItem(final @NonNull ItemLike item, final double x, final double y, final double size) {
		this.drawItem(new ItemStack(item), x, y, size, size);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double size) {
		this.drawItem(stack, x, y, size, size);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height) {
		this.drawItem(stack, x, y, width, height, Color.WHITE);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final @NonNull Color color) {
		this.drawItem(stack, x, y, width, height, color, true, true, true, null);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final boolean durability, final boolean stackCount) {
		this.drawItem(stack, x, y, width, height, Color.WHITE, durability, stackCount, true, null);
	}

	public void drawItem(final @NonNull ItemStack stack, final double x, final double y, final double width, final double height, final @NonNull Color color, final boolean durability, final boolean stackCount, final boolean cooldown, final String text) {
		final Minecraft minecraft = Minecraft.getInstance();
		this.state.clear();
		minecraft.getItemModelResolver().updateForTopItem(this.state, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
		if (this.state.isEmpty()) {
			return;
		}

		final RenderBridge render = (RenderBridge) BridgeHandler.RENDER.get();
		final RasterLayer raster = render.getRaster();
		final Matrix4f matrix = raster.getMatrix(x, y);
		final ScreenRectangle region = raster.getRegion(matrix, width, height);
		if (region != null) {
			final GpuTextureView view = raster.render(region, this.state.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT, (pose, collector) -> {
				pose.mulPose(matrix);
				pose.scale((float) (width / 16D), (float) (height / 16D), (float) (width / 16D));
				pose.translate(8F, 8F, 0F);
				pose.scale(16F, -16F, 16F);
				this.state.submit(pose, collector, MCDrawItem.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			});
			raster.composite(view, region, color);
		}

		MCDrawItem.decorations(render, stack, x, y, width, height, durability, stackCount, cooldown, text);
	}

	private static void decorations(final RenderBridge render, final ItemStack stack, final double x, final double y, final double width, final double height, final boolean durability, final boolean stackCount, final boolean cooldown, final String text) {
		final double scaleX = width / 16D;
		final double scaleY = height / 16D;
		if (durability && stack.isBarVisible()) {
			render.getRaster().rect(x + 2D * scaleX, y + 13D * scaleY, 13D * scaleX, 2D * scaleY, 0xFF000000);
			render.getRaster().rect(x + 2D * scaleX, y + 13D * scaleY, stack.getBarWidth() * scaleX, scaleY, ARGB.opaque(stack.getBarColor()));
		}

		if (cooldown) {
			final Minecraft minecraft = Minecraft.getInstance();
			final LocalPlayer player = minecraft.player;
			final float percent = player == null ? 0F : player.getCooldowns().getCooldownPercent(stack, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
			if (percent > 0F) {
				final int top = Mth.floor(16F * (1F - percent));
				final int bottom = top + Mth.ceil(16F * percent);
				render.getRaster().rect(x, y + top * scaleY, 16D * scaleX, (bottom - top) * scaleY, Integer.MAX_VALUE);
			}
		}

		if (stackCount && (stack.getCount() != 1 || text != null)) {
			final Font font = Minecraft.getInstance().font;
			final String count = text == null ? String.valueOf(stack.getCount()) : text;
			render.getGlyph().draw(font.prepareText(count, 0F, 0F, -1, true, 0), x + (17D - font.width(count)) * scaleX, y + 9D * scaleY, scaleX, scaleY);
		}
	}

}