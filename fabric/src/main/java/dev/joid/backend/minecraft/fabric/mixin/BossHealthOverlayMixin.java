package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void joid$renderStart(final GuiGraphics graphics, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.BOSS_BAR, false);
		if (MinecraftOverlayEvents.isCancelled(OverlayLayer.BOSS_BAR)) {
			MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.BOSS_BAR, true);
			info.cancel();
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void joid$renderEnd(final GuiGraphics graphics, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.BOSS_BAR, true);
	}

}