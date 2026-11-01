package dev.joid.backend.minecraft.forge.registry;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
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
			final ResourceLocation stack = ForgeOverlayLayerRegistry.getStack(layer);
			final ResourceLocation vanillaLayer = ForgeOverlayLayerRegistry.getLayer(layer);
			draw.addBelow(stack, MinecraftOverlayEvents.getId(layer, false), vanillaLayer, (graphics, deltaTracker) -> MinecraftOverlayEvents.fireLayer(graphics, layer, false));
			draw.addAbove(stack, MinecraftOverlayEvents.getId(layer, true), vanillaLayer, (graphics, deltaTracker) -> MinecraftOverlayEvents.fireLayer(graphics, layer, true));
			draw.addConditionTo(stack, vanillaLayer, () -> !MinecraftOverlayEvents.isCancelled(layer));
		}

		draw.add(MinecraftOverlayEvents.getId(), (graphics, deltaTracker) -> MinecraftOverlayEvents.fireHud(graphics));
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
			case HOTBAR, ARMOR, HEALTH, FOOD, AIR, MOUNT_HEALTH, CONTEXTUAL_BAR, SELECTED_ITEM_NAME -> ForgeLayeredDraw.HOTBAR;
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