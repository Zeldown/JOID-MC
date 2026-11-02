package dev.joid.backend.minecraft.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.joid.backend.minecraft.forge.registry.ForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraftforge.client.gui.ForgeIngameGui;

@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin {

	@Inject(method = "renderSleepFade", at = @At("HEAD"), cancellable = true, remap = false)
	private void joid$sleepStart(final int width, final int height, final MatrixStack pose, final CallbackInfo info) {
		if (!ForgeOverlayLayerRegistry.startLayer(pose, OverlayLayer.SLEEP)) {
			info.cancel();
		}
	}

	@Inject(method = "renderSleepFade", at = @At("RETURN"), remap = false)
	private void joid$sleepEnd(final int width, final int height, final MatrixStack pose, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SLEEP, true);
	}

	@Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true, remap = false)
	private void joid$actionBarStart(final int width, final int height, final float partialTick, final MatrixStack pose, final CallbackInfo info) {
		if (!ForgeOverlayLayerRegistry.startLayer(pose, OverlayLayer.ACTION_BAR)) {
			info.cancel();
		}
	}

	@Inject(method = "renderRecordOverlay", at = @At("RETURN"), remap = false)
	private void joid$actionBarEnd(final int width, final int height, final float partialTick, final MatrixStack pose, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.ACTION_BAR, true);
	}

	@Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true, remap = false)
	private void joid$titleStart(final int width, final int height, final float partialTick, final MatrixStack pose, final CallbackInfo info) {
		if (!ForgeOverlayLayerRegistry.startLayer(pose, OverlayLayer.TITLE)) {
			info.cancel();
		}
	}

	@Inject(method = "renderTitle", at = @At("RETURN"), remap = false)
	private void joid$titleEnd(final int width, final int height, final float partialTick, final MatrixStack pose, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.TITLE, true);
	}

}