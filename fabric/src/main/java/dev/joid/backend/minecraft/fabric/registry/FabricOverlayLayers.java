package dev.joid.backend.minecraft.fabric.registry;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FabricOverlayLayers {

	public static void register() {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final Identifier element = FabricOverlayLayers.getElement(layer);
			HudElementRegistry.attachElementBefore(element, MinecraftOverlayEvents.getId(layer, false), (graphics, _) -> MinecraftOverlayEvents.fireLayer(graphics, layer, false));
			HudElementRegistry.attachElementAfter(element, MinecraftOverlayEvents.getId(layer, true), (graphics, _) -> MinecraftOverlayEvents.fireLayer(graphics, layer, true));
			HudElementRegistry.replaceElement(element, vanilla -> (graphics, deltaTracker) -> {
				if (!MinecraftOverlayEvents.isCancelled(layer)) {
					vanilla.extractRenderState(graphics, deltaTracker);
				}
			});
		}

		HudElementRegistry.addLast(MinecraftOverlayEvents.getId(), (graphics, _) -> MinecraftOverlayEvents.fireHud(graphics));
	}

	private static Identifier getElement(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> VanillaHudElements.MISC_OVERLAYS;
			case CROSSHAIR -> VanillaHudElements.CROSSHAIR;
			case HOTBAR -> VanillaHudElements.HOTBAR;
			case ARMOR -> VanillaHudElements.ARMOR_BAR;
			case HEALTH -> VanillaHudElements.HEALTH_BAR;
			case FOOD -> VanillaHudElements.FOOD_BAR;
			case AIR -> VanillaHudElements.AIR_BAR;
			case MOUNT_HEALTH -> VanillaHudElements.MOUNT_HEALTH;
			case CONTEXTUAL_BAR -> VanillaHudElements.INFO_BAR;
			case EXPERIENCE_LEVEL -> VanillaHudElements.EXPERIENCE_LEVEL;
			case SELECTED_ITEM_NAME -> VanillaHudElements.HELD_ITEM_TOOLTIP;
			case EFFECTS -> VanillaHudElements.MOB_EFFECTS;
			case BOSS_BAR -> VanillaHudElements.BOSS_BAR;
			case SLEEP -> VanillaHudElements.SLEEP;
			case SCOREBOARD -> VanillaHudElements.SCOREBOARD;
			case ACTION_BAR -> VanillaHudElements.OVERLAY_MESSAGE;
			case TITLE -> VanillaHudElements.TITLE_AND_SUBTITLE;
			case CHAT -> VanillaHudElements.CHAT;
			case PLAYER_LIST -> VanillaHudElements.PLAYER_LIST;
			case SUBTITLES -> VanillaHudElements.SUBTITLES;
		};
	}

}