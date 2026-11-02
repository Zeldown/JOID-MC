package dev.joid.backend.minecraft.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.joid.backend.minecraft.forge.registry.ForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.scoreboard.ScoreObjective;

@Mixin(IngameGui.class)
public abstract class IngameGuiMixin {

	@Inject(method = { "renderSelectedItemName", "func_238453_b_" }, at = @At("HEAD"), cancellable = true, remap = false)
	private void joid$selectedItemNameStart(final MatrixStack pose, final CallbackInfo info) {
		if (!ForgeOverlayLayerRegistry.startLayer(pose, OverlayLayer.SELECTED_ITEM_NAME)) {
			info.cancel();
		}
	}

	@Inject(method = { "renderSelectedItemName", "func_238453_b_" }, at = @At("RETURN"), remap = false)
	private void joid$selectedItemNameEnd(final MatrixStack pose, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SELECTED_ITEM_NAME, true);
	}

	@Inject(method = { "displayScoreboardSidebar", "func_238447_a_" }, at = @At("HEAD"), cancellable = true, remap = false)
	private void joid$scoreboardStart(final MatrixStack pose, final ScoreObjective objective, final CallbackInfo info) {
		if (!ForgeOverlayLayerRegistry.startLayer(pose, OverlayLayer.SCOREBOARD)) {
			info.cancel();
		}
	}

	@Inject(method = { "displayScoreboardSidebar", "func_238447_a_" }, at = @At("RETURN"), remap = false)
	private void joid$scoreboardEnd(final MatrixStack pose, final ScoreObjective objective, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SCOREBOARD, true);
	}

}