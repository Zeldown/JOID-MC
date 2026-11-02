package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Unique
	private OverlayLayer joid$section;

	@Inject(method = "render", at = @At("HEAD"))
	private void joid$renderStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CAMERA_OVERLAYS, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 0))
	private void joid$hotbarStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CAMERA_OVERLAYS, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.HOTBAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private void joid$crosshairStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.HOTBAR, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CROSSHAIR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private void joid$bossBarStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CROSSHAIR, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.BOSS_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
	private void joid$bossBarEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.BOSS_BAR, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private void joid$mountHealthStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.MOUNT_HEALTH, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
	private void joid$mountHealthEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.MOUNT_HEALTH, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CONTEXTUAL_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 1))
	private void joid$selectedItemNameStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CONTEXTUAL_BAR, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SELECTED_ITEM_NAME, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getSleepTimer()I", ordinal = 0))
	private void joid$sleepStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SELECTED_ITEM_NAME, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SLEEP, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private void joid$effectsStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SLEEP, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.EFFECTS, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
	private void joid$effectsEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.EFFECTS, true);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;overlayMessageString:Lnet/minecraft/network/chat/Component;", ordinal = 0))
	private void joid$actionBarStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		this.joid$section = OverlayLayer.ACTION_BAR;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.ACTION_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;title:Lnet/minecraft/network/chat/Component;", ordinal = 0))
	private void joid$titleStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		this.joid$section = OverlayLayer.TITLE;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.ACTION_BAR, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.TITLE, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private void joid$subtitlesStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		this.joid$section = null;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.TITLE, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SUBTITLES, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
	private void joid$subtitlesEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SUBTITLES, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V"))
	private void joid$scoreboardStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SCOREBOARD, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V", shift = At.Shift.AFTER))
	private void joid$scoreboardEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.SCOREBOARD, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
	private void joid$chatStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CHAT, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", shift = At.Shift.AFTER))
	private void joid$chatEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.CHAT, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
	private void joid$playerListStart(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.PLAYER_LIST, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V", shift = At.Shift.AFTER))
	private void joid$playerListEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.PLAYER_LIST, true);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void joid$renderEnd(final PoseStack pose, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireHud(pose);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", args = "ldc=armor"))
	private void joid$armorStart(final PoseStack pose, final CallbackInfo info) {
		this.joid$section = OverlayLayer.ARMOR;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.ARMOR, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=health"))
	private void joid$healthStart(final PoseStack pose, final CallbackInfo info) {
		this.joid$section = OverlayLayer.HEALTH;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.ARMOR, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.HEALTH, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=food"))
	private void joid$foodStart(final PoseStack pose, final CallbackInfo info) {
		this.joid$section = OverlayLayer.FOOD;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.HEALTH, true);
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.FOOD, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=air"))
	private void joid$airStart(final PoseStack pose, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(pose, this.joid$section, true);
		this.joid$section = OverlayLayer.AIR;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.AIR, false);
	}

	@Inject(method = "renderPlayerHealth", at = @At("TAIL"))
	private void joid$airEnd(final PoseStack pose, final CallbackInfo info) {
		this.joid$section = null;
		MinecraftOverlayEvents.fireLayer(pose, OverlayLayer.AIR, true);
	}

	@Inject(method = {"renderVignette", "renderSpyglassOverlay", "renderTextureOverlay", "renderPortalOverlay"}, at = @At("HEAD"), cancellable = true)
	private void joid$cancelCameraOverlays(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.CAMERA_OVERLAYS, info);
	}

	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	private void joid$cancelHotbar(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.HOTBAR, info);
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void joid$cancelCrosshair(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.CROSSHAIR, info);
	}

	@Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
	private void joid$cancelHealth(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.HEALTH, info);
	}

	@Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void joid$cancelMountHealth(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.MOUNT_HEALTH, info);
	}

	@Inject(method = {"renderJumpMeter", "renderExperienceBar"}, at = @At("HEAD"), cancellable = true)
	private void joid$cancelContextualBar(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.CONTEXTUAL_BAR, info);
	}

	@Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
	private void joid$cancelSelectedItemName(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.SELECTED_ITEM_NAME, info);
	}

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void joid$cancelEffects(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.EFFECTS, info);
	}

	@Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void joid$cancelScoreboard(final CallbackInfo info) {
		this.joid$cancel(OverlayLayer.SCOREBOARD, info);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private boolean joid$drawBossBar(final BossHealthOverlay overlay, final PoseStack pose) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.BOSS_BAR);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
	private boolean joid$drawSleep(final PoseStack pose, final int minX, final int minY, final int maxX, final int maxY, final int color) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.SLEEP);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private boolean joid$drawSubtitles(final SubtitleOverlay overlay, final PoseStack pose) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.SUBTITLES);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
	private boolean joid$drawChat(final ChatComponent chat, final PoseStack pose, final int tickCount) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.CHAT);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
	private boolean joid$drawPlayerList(final PlayerTabOverlay list, final PoseStack pose, final int width, final Scoreboard scoreboard, final Objective objective) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.PLAYER_LIST);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawBackdrop(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;III)V"))
	private boolean joid$drawBackdrop(final Gui gui, final PoseStack pose, final Font font, final int y, final int width, final int color) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"))
	private boolean joid$drawText(final Font font, final PoseStack pose, final Component text, final float x, final float y, final int color) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
	private boolean joid$drawStatus(final Gui gui, final PoseStack pose, final int x, final int y, final int u, final int v, final int width, final int height) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@Unique
	private void joid$cancel(final OverlayLayer layer, final CallbackInfo info) {
		if (MinecraftOverlayEvents.isCancelled(layer)) {
			info.cancel();
		}
	}

}