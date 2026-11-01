package dev.joid.backend.minecraft.forge.event;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.gui.ForgeIngameGui;

public final class ForgeOverlayGui extends ForgeIngameGui {

	public ForgeOverlayGui(final Minecraft minecraft) {
		super(minecraft);
	}

	@Override
	public void renderSelectedItemName(final MatrixStack pose) {
		ForgeOverlayGui.draw(pose, OverlayLayer.SELECTED_ITEM_NAME, () -> super.renderSelectedItemName(pose));
	}

	@Override
	protected void renderSleepFade(final int width, final int height, final MatrixStack pose) {
		ForgeOverlayGui.draw(pose, OverlayLayer.SLEEP, () -> super.renderSleepFade(width, height, pose));
	}

	@Override
	protected void renderRecordOverlay(final int width, final int height, final float partialTick, final MatrixStack pose) {
		ForgeOverlayGui.draw(pose, OverlayLayer.ACTION_BAR, () -> super.renderRecordOverlay(width, height, partialTick, pose));
	}

	@Override
	protected void renderTitle(final int width, final int height, final float partialTick, final MatrixStack pose) {
		ForgeOverlayGui.draw(pose, OverlayLayer.TITLE, () -> super.renderTitle(width, height, partialTick, pose));
	}

	@Override
	protected void displayScoreboardSidebar(final MatrixStack pose, final ScoreObjective objective) {
		ForgeOverlayGui.draw(pose, OverlayLayer.SCOREBOARD, () -> super.displayScoreboardSidebar(pose, objective));
	}

	private static void draw(final MatrixStack pose, final OverlayLayer layer, final Runnable vanilla) {
		MinecraftOverlayEvents.fireLayer(pose, layer, false);
		if (!MinecraftOverlayEvents.isCancelled(layer)) {
			vanilla.run();
		}
		MinecraftOverlayEvents.fireLayer(pose, layer, true);
	}

}