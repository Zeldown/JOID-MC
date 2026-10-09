package dev.joid.backend.minecraft.neoforge.registry;

import java.util.List;

import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeOverlayLayers {

	public static void register(final RegisterGuiLayersEvent event) {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final List<Identifier> layers = NeoForgeOverlayLayers.getLayers(layer);
			event.registerBelow(layers.getFirst(), OverlayLayerRenderer.getIdentifier(layer, false), (graphics, _) -> OverlayLayerRenderer.extract(graphics, layer, false));
			event.registerAbove(layers.getLast(), OverlayLayerRenderer.getIdentifier(layer, true), (graphics, _) -> OverlayLayerRenderer.extract(graphics, layer, true));
			for (final Identifier vanillaLayer : layers) {
				event.wrapLayer(vanillaLayer, vanilla -> (graphics, deltaTracker) -> {
					if (!OverlayLayerRenderer.isCancelled(layer)) {
						vanilla.render(graphics, deltaTracker);
					}
				});
			}
		}

		event.registerAboveAll(OverlayLayerRenderer.getIdentifier(), (graphics, _) -> OverlayLayerRenderer.extract(graphics));
	}

	private static List<Identifier> getLayers(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> List.of(VanillaGuiLayers.CAMERA_OVERLAYS);
			case CROSSHAIR -> List.of(VanillaGuiLayers.CROSSHAIR);
			case HOTBAR -> List.of(VanillaGuiLayers.HOTBAR);
			case ARMOR -> List.of(VanillaGuiLayers.ARMOR_LEVEL);
			case HEALTH -> List.of(VanillaGuiLayers.PLAYER_HEALTH);
			case FOOD -> List.of(VanillaGuiLayers.FOOD_LEVEL);
			case AIR -> List.of(VanillaGuiLayers.AIR_LEVEL);
			case MOUNT_HEALTH -> List.of(VanillaGuiLayers.VEHICLE_HEALTH);
			case CONTEXTUAL_BAR -> List.of(VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND, VanillaGuiLayers.CONTEXTUAL_INFO_BAR);
			case EXPERIENCE_LEVEL -> List.of(VanillaGuiLayers.EXPERIENCE_LEVEL);
			case SELECTED_ITEM_NAME -> List.of(VanillaGuiLayers.SELECTED_ITEM_NAME);
			case EFFECTS -> List.of(VanillaGuiLayers.EFFECTS);
			case BOSS_BAR -> List.of(VanillaGuiLayers.BOSS_OVERLAY);
			case SLEEP -> List.of(VanillaGuiLayers.SLEEP_OVERLAY);
			case SCOREBOARD -> List.of(VanillaGuiLayers.SCOREBOARD_SIDEBAR);
			case ACTION_BAR -> List.of(VanillaGuiLayers.OVERLAY_MESSAGE);
			case TITLE -> List.of(VanillaGuiLayers.TITLE);
			case CHAT -> List.of(VanillaGuiLayers.CHAT);
			case PLAYER_LIST -> List.of(VanillaGuiLayers.TAB_LIST);
			case SUBTITLES -> List.of(VanillaGuiLayers.SUBTITLE_OVERLAY);
		};
	}

}