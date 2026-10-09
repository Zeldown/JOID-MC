package dev.joid.backend.minecraft.forge.registry;

import java.util.List;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.OverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayer;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayLayers {

	public static void register() {
		AddGuiOverlayLayersEvent.BUS.addListener(event -> ForgeOverlayLayers.add(event.getLayeredDraw()));
	}

	private static void add(final ForgeLayeredDraw draw) {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final Identifier stack = ForgeOverlayLayers.getStack(layer);
			final List<Identifier> layers = ForgeOverlayLayers.getLayers(layer);
			draw.addBelow(stack, OverlayEvents.getId(layer, false), layers.getFirst(), (graphics, _) -> OverlayEvents.fireLayer(graphics, layer, false));
			draw.addAbove(stack, OverlayEvents.getId(layer, true), layers.getLast(), (graphics, _) -> OverlayEvents.fireLayer(graphics, layer, true));
			for (final Identifier vanillaLayer : layers) {
				final ForgeLayer vanilla = draw.locateStack(stack).orElseThrow().getLayer(vanillaLayer);
				draw.replace(stack, vanillaLayer, (graphics, deltaTracker) -> {
					if (!OverlayEvents.isCancelled(layer)) {
						vanilla.extract(graphics, deltaTracker);
					}
				});
			}
		}

		draw.add(OverlayEvents.getId(), (graphics, _) -> OverlayEvents.fireHud(graphics));
	}

	private static Identifier getStack(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS, CROSSHAIR, EFFECTS, BOSS_BAR -> ForgeLayeredDraw.PRE_SLEEP_STACK;
			case HOTBAR, ARMOR, HEALTH, FOOD, AIR, MOUNT_HEALTH, CONTEXTUAL_BAR, EXPERIENCE_LEVEL, SELECTED_ITEM_NAME -> ForgeLayeredDraw.HOTBAR_AND_DECOS;
			case SLEEP -> ForgeLayeredDraw.VANILLA_ROOT;
			case SCOREBOARD, ACTION_BAR, TITLE, CHAT, PLAYER_LIST, SUBTITLES -> ForgeLayeredDraw.POST_SLEEP_STACK;
		};
	}

	private static List<Identifier> getLayers(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> List.of(ForgeLayeredDraw.CAMERA_OVERLAY);
			case CROSSHAIR -> List.of(ForgeLayeredDraw.CROSSHAIR);
			case HOTBAR -> List.of(ForgeLayeredDraw.ITEM_HOTBAR);
			case ARMOR, HEALTH, FOOD, AIR -> List.of(ForgeLayeredDraw.HEALTH_BAR);
			case MOUNT_HEALTH -> List.of(ForgeLayeredDraw.VEHICLE_HEALTH);
			case CONTEXTUAL_BAR -> List.of(ForgeLayeredDraw.BACKGROUND, ForgeLayeredDraw.CONTEXTUAL_INFO);
			case EXPERIENCE_LEVEL -> List.of(ForgeLayeredDraw.EXPERIENCE_LEVEL);
			case SELECTED_ITEM_NAME -> List.of(ForgeLayeredDraw.SELECTED_ITEM_NAME);
			case EFFECTS -> List.of(ForgeLayeredDraw.POTION_EFFECTS);
			case BOSS_BAR -> List.of(ForgeLayeredDraw.BOSS_OVERLAY);
			case SLEEP -> List.of(ForgeLayeredDraw.SLEEP_OVERLAY);
			case SCOREBOARD -> List.of(ForgeLayeredDraw.SCOREBOARD);
			case ACTION_BAR -> List.of(ForgeLayeredDraw.HOTBAR_MESSAGE);
			case TITLE -> List.of(ForgeLayeredDraw.TITLE_OVERLAY);
			case CHAT -> List.of(ForgeLayeredDraw.CHAT_OVERLAY);
			case PLAYER_LIST -> List.of(ForgeLayeredDraw.TAB_LIST);
			case SUBTITLES -> List.of(ForgeLayeredDraw.SUBTITLE_OVERLAY);
		};
	}

}