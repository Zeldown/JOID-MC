package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Unique
	private OverlayLayer joid$section;

	@Inject(method = "renderCameraOverlays", at = @At("HEAD"), cancellable = true)
	private void joid$cameraOverlaysStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.CAMERA_OVERLAYS, info);
	}

	@Inject(method = "renderCameraOverlays", at = @At("RETURN"))
	private void joid$cameraOverlaysEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CAMERA_OVERLAYS, true);
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void joid$crosshairStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.CROSSHAIR, info);
	}

	@Inject(method = "renderCrosshair", at = @At("RETURN"))
	private void joid$crosshairEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CROSSHAIR, true);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
	private void joid$hotbarStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HOTBAR, false);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
	private void joid$contextualBarStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HOTBAR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CONTEXTUAL_BAR, false);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"))
	private void joid$contextualBarEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CONTEXTUAL_BAR, true);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$mountHealthStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.MOUNT_HEALTH, false);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void joid$selectedItemNameStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.MOUNT_HEALTH, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SELECTED_ITEM_NAME, false);
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At("TAIL"))
	private void joid$selectedItemNameEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SELECTED_ITEM_NAME, true);
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
	private void joid$experienceLevelStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.EXPERIENCE_LEVEL, info);
	}

	@Inject(method = "renderExperienceLevel", at = @At("RETURN"))
	private void joid$experienceLevelEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.EXPERIENCE_LEVEL, true);
	}

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void joid$effectsStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.EFFECTS, info);
	}

	@Inject(method = "renderEffects", at = @At("RETURN"))
	private void joid$effectsEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.EFFECTS, true);
	}

	@Inject(method = "renderSleepOverlay", at = @At("HEAD"), cancellable = true)
	private void joid$sleepStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.SLEEP, info);
	}

	@Inject(method = "renderSleepOverlay", at = @At("RETURN"))
	private void joid$sleepEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SLEEP, true);
	}

	@Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void joid$scoreboardStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.SCOREBOARD, info);
	}

	@Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
	private void joid$scoreboardEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SCOREBOARD, true);
	}

	@Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
	private void joid$actionBarStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.ACTION_BAR, info);
	}

	@Inject(method = "renderOverlayMessage", at = @At("RETURN"))
	private void joid$actionBarEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.ACTION_BAR, true);
	}

	@Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
	private void joid$titleStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.TITLE, info);
	}

	@Inject(method = "renderTitle", at = @At("RETURN"))
	private void joid$titleEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.TITLE, true);
	}

	@Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
	private void joid$chatStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.CHAT, info);
	}

	@Inject(method = "renderChat", at = @At("RETURN"))
	private void joid$chatEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CHAT, true);
	}

	@Inject(method = "renderTabList", at = @At("HEAD"), cancellable = true)
	private void joid$playerListStart(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		GuiMixin.joid$start(graphics, OverlayLayer.PLAYER_LIST, info);
	}

	@Inject(method = "renderTabList", at = @At("RETURN"))
	private void joid$playerListEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.PLAYER_LIST, true);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void joid$renderEnd(final GuiGraphics graphics, final DeltaTracker deltaTracker, final CallbackInfo info) {
		MinecraftOverlayEvents.fireHud(graphics);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", args = "ldc=armor"))
	private void joid$armorStart(final GuiGraphics graphics, final CallbackInfo info) {
		this.joid$section = OverlayLayer.ARMOR;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.ARMOR, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=health"))
	private void joid$healthStart(final GuiGraphics graphics, final CallbackInfo info) {
		this.joid$section = OverlayLayer.HEALTH;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.ARMOR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HEALTH, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=food"))
	private void joid$foodStart(final GuiGraphics graphics, final CallbackInfo info) {
		this.joid$section = OverlayLayer.FOOD;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HEALTH, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.FOOD, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=air"))
	private void joid$airStart(final GuiGraphics graphics, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, this.joid$section, true);
		this.joid$section = OverlayLayer.AIR;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.AIR, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At("TAIL"))
	private void joid$airEnd(final GuiGraphics graphics, final CallbackInfo info) {
		this.joid$section = null;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.AIR, true);
	}

	@Inject(method = "renderItemHotbar", at = @At("HEAD"), cancellable = true)
	private void joid$cancelHotbar(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.HOTBAR, info);
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void joid$cancelArmor(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.ARMOR, info);
	}

	@Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
	private void joid$cancelHealth(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.HEALTH, info);
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void joid$cancelFood(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.FOOD, info);
	}

	@Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void joid$cancelMountHealth(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.MOUNT_HEALTH, info);
	}

	@Inject(method = {"renderJumpMeter", "renderExperienceBar"}, at = @At("HEAD"), cancellable = true)
	private void joid$cancelContextualBar(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.CONTEXTUAL_BAR, info);
	}

	@Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void joid$cancelSelectedItemName(final CallbackInfo info) {
		GuiMixin.joid$cancel(OverlayLayer.SELECTED_ITEM_NAME, info);
	}

	@WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
	private boolean joid$drawAir(final GuiGraphics graphics, final ResourceLocation sprite, final int x, final int y, final int width, final int height) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.AIR);
	}

	@Unique
	private static void joid$start(final GuiGraphics graphics, final OverlayLayer layer, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, layer, false);
		if (MinecraftOverlayEvents.isCancelled(layer)) {
			MinecraftOverlayEvents.fireLayer(graphics, layer, true);
			info.cancel();
		}
	}

	@Unique
	private static void joid$cancel(final OverlayLayer layer, final CallbackInfo info) {
		if (MinecraftOverlayEvents.isCancelled(layer)) {
			info.cancel();
		}
	}

}