package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayLayerRegistry {

	public static void register(final IEventBus modBus) {
		modBus.addListener((final AddGuiOverlayLayersEvent event) -> ForgeOverlayLayerRegistry.add(event.getLayeredDraw()));
	}

	private static void add(final ForgeLayeredDraw draw) {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final ResourceLocation vanillaLayer = ForgeOverlayLayerRegistry.getLayer(layer);
			if (vanillaLayer == null) {
				continue;
			}

			final ResourceLocation stack = ForgeOverlayLayerRegistry.getStack(layer);
			draw.addBelow(stack, MinecraftOverlayEvents.getId(layer, false), vanillaLayer, (graphics, deltaTracker) -> MinecraftOverlayEvents.fireLayer(graphics, layer, false));
			draw.addAbove(stack, MinecraftOverlayEvents.getId(layer, true), vanillaLayer, (graphics, deltaTracker) -> MinecraftOverlayEvents.fireLayer(graphics, layer, true));
			draw.addConditionTo(stack, vanillaLayer, () -> !MinecraftOverlayEvents.isCancelled(layer));
		}

		draw.addConditionTo(ForgeLayeredDraw.PRE_SLEEP_STACK, ForgeLayeredDraw.HOTBAR, () -> false);
		draw.addAbove(ForgeLayeredDraw.PRE_SLEEP_STACK, ResourceLocation.fromNamespaceAndPath(MinecraftJoid.MOD_ID, "overlay/hotbar"), ForgeLayeredDraw.HOTBAR, ForgeOverlayLayerRegistry::drawHotbar);
		draw.add(MinecraftOverlayEvents.getId(), (graphics, deltaTracker) -> MinecraftOverlayEvents.fireHud(graphics));
	}

	private static void drawHotbar(final GuiGraphics graphics, final DeltaTracker deltaTracker) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Gui gui = minecraft.gui;
		final boolean spectator = minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR;
		ForgeOverlayLayerRegistry.draw(graphics, () -> {
			if (spectator) {
				gui.getSpectatorGui().renderHotbar(graphics);
			} else {
				gui.renderItemHotbar(graphics, deltaTracker);
			}
		}, OverlayLayer.HOTBAR);

		ForgeOverlayLayerRegistry.draw(graphics, () -> {
			final int x = graphics.guiWidth() / 2 - 91;
			final PlayerRideableJumping jumping = minecraft.player.jumpableVehicle();
			if (jumping != null) {
				gui.renderJumpMeter(jumping, graphics, x);
			} else if (gui.isExperienceBarVisible()) {
				gui.renderExperienceBar(graphics, x);
			}
		}, OverlayLayer.CONTEXTUAL_BAR);

		if (minecraft.gameMode.canHurtPlayer()) {
			ForgeOverlayLayerRegistry.draw(graphics, () -> gui.renderPlayerHealth(graphics), OverlayLayer.ARMOR, OverlayLayer.HEALTH, OverlayLayer.FOOD, OverlayLayer.AIR);
		}

		ForgeOverlayLayerRegistry.draw(graphics, () -> gui.renderVehicleHealth(graphics), OverlayLayer.MOUNT_HEALTH);
		ForgeOverlayLayerRegistry.draw(graphics, () -> {
			if (!spectator) {
				gui.renderSelectedItemName(graphics);
			}
		}, OverlayLayer.SELECTED_ITEM_NAME);

		if (spectator && minecraft.player.isSpectator()) {
			gui.getSpectatorGui().renderTooltip(graphics);
		}
	}

	private static void draw(final GuiGraphics graphics, final Runnable vanilla, final OverlayLayer... layers) {
		boolean cancelled = false;
		for (final OverlayLayer layer : layers) {
			MinecraftOverlayEvents.fireLayer(graphics, layer, false);
			cancelled |= MinecraftOverlayEvents.isCancelled(layer);
		}

		if (!cancelled) {
			vanilla.run();
		}

		for (final OverlayLayer layer : layers) {
			MinecraftOverlayEvents.fireLayer(graphics, layer, true);
		}
	}

	private static ResourceLocation getStack(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS, CROSSHAIR, HOTBAR, ARMOR, HEALTH, FOOD, AIR, MOUNT_HEALTH, CONTEXTUAL_BAR, EXPERIENCE_LEVEL, SELECTED_ITEM_NAME, EFFECTS, BOSS_BAR -> ForgeLayeredDraw.PRE_SLEEP_STACK;
			case SLEEP -> ForgeLayeredDraw.VANILLA_ROOT;
			case SCOREBOARD, ACTION_BAR, TITLE, CHAT, PLAYER_LIST, SUBTITLES -> ForgeLayeredDraw.POST_SLEEP_STACK;
		};
	}

	private static ResourceLocation getLayer(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> ForgeLayeredDraw.CAMERA_OVERLAY;
			case CROSSHAIR -> ForgeLayeredDraw.CROSSHAIR;
			case HOTBAR, ARMOR, HEALTH, FOOD, AIR, MOUNT_HEALTH, CONTEXTUAL_BAR, SELECTED_ITEM_NAME -> null;
			case EXPERIENCE_LEVEL -> ForgeLayeredDraw.EXPERIENCE;
			case EFFECTS -> ForgeLayeredDraw.POTION_EFFECTS;
			case BOSS_BAR -> ForgeLayeredDraw.BOSS_OVERLAY;
			case SLEEP -> ForgeLayeredDraw.SLEEP_OVERLAY;
			case SCOREBOARD -> ForgeLayeredDraw.SCOREBOARD;
			case ACTION_BAR -> ForgeLayeredDraw.HOTBAR_MESSAGE;
			case TITLE -> ForgeLayeredDraw.TITLE_OVERLAY;
			case CHAT -> ForgeLayeredDraw.CHAT_OVERLAY;
			case PLAYER_LIST -> ForgeLayeredDraw.TAB_LIST;
			case SUBTITLES -> ForgeLayeredDraw.SUBTITLE_OVERLAY;
		};
	}

}