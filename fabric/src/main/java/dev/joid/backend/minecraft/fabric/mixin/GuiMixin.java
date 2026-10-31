package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Unique
	private OverlayLayer joid$section;

	@Inject(method = "render", at = @At("HEAD"))
	private void joid$renderStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CAMERA_OVERLAYS, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 0))
	private void joid$hotbarStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CAMERA_OVERLAYS, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HOTBAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$crosshairStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.HOTBAR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CROSSHAIR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$bossBarStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CROSSHAIR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.BOSS_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void joid$bossBarEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.BOSS_BAR, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$mountHealthStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.MOUNT_HEALTH, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void joid$mountHealthEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.MOUNT_HEALTH, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CONTEXTUAL_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 1))
	private void joid$selectedItemNameStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CONTEXTUAL_BAR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SELECTED_ITEM_NAME, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getSleepTimer()I", ordinal = 0))
	private void joid$sleepStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SELECTED_ITEM_NAME, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SLEEP, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$effectsStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SLEEP, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.EFFECTS, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void joid$effectsEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.EFFECTS, true);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;overlayMessageString:Lnet/minecraft/network/chat/Component;", ordinal = 0))
	private void joid$actionBarStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		this.joid$section = OverlayLayer.ACTION_BAR;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.ACTION_BAR, false);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;title:Lnet/minecraft/network/chat/Component;", ordinal = 0))
	private void joid$titleStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		this.joid$section = OverlayLayer.TITLE;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.ACTION_BAR, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.TITLE, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void joid$subtitlesStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		this.joid$section = null;
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.TITLE, true);
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SUBTITLES, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
	private void joid$subtitlesEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SUBTITLES, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"))
	private void joid$scoreboardStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SCOREBOARD, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V", shift = At.Shift.AFTER))
	private void joid$scoreboardEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.SCOREBOARD, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
	private void joid$chatStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CHAT, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V", shift = At.Shift.AFTER))
	private void joid$chatEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.CHAT, true);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
	private void joid$playerListStart(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.PLAYER_LIST, false);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V", shift = At.Shift.AFTER))
	private void joid$playerListEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
		MinecraftOverlayEvents.fireLayer(graphics, OverlayLayer.PLAYER_LIST, true);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void joid$renderEnd(final GuiGraphics graphics, final float partialTick, final CallbackInfo info) {
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

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private boolean joid$drawBossBar(final BossHealthOverlay overlay, final GuiGraphics graphics) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.BOSS_BAR);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"))
	private boolean joid$drawSleep(final GuiGraphics graphics, final RenderType type, final int minX, final int minY, final int maxX, final int maxY, final int color) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.SLEEP);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private boolean joid$drawSubtitles(final SubtitleOverlay overlay, final GuiGraphics graphics) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.SUBTITLES);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
	private boolean joid$drawChat(final ChatComponent chat, final GuiGraphics graphics, final int tickCount, final int mouseX, final int mouseY) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.CHAT);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
	private boolean joid$drawPlayerList(final PlayerTabOverlay list, final GuiGraphics graphics, final int width, final Scoreboard scoreboard, final Objective objective) {
		return !MinecraftOverlayEvents.isCancelled(OverlayLayer.PLAYER_LIST);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawBackdrop(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;III)V"))
	private boolean joid$drawBackdrop(final Gui gui, final GuiGraphics graphics, final Font font, final int y, final int width, final int color) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
	private boolean joid$drawText(final GuiGraphics graphics, final Font font, final Component text, final int x, final int y, final int color) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private boolean joid$drawStatus(final GuiGraphics graphics, final ResourceLocation texture, final int x, final int y, final int u, final int v, final int width, final int height) {
		return this.joid$section == null || !MinecraftOverlayEvents.isCancelled(this.joid$section);
	}

	@Unique
	private void joid$cancel(final OverlayLayer layer, final CallbackInfo info) {
		if (MinecraftOverlayEvents.isCancelled(layer)) {
			info.cancel();
		}
	}

}