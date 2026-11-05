package dev.joid.backend.minecraft.forge.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayLayerRegistry {

	private static final Map<IIngameOverlay, OverlayLayer> LAYERS = new HashMap<>();

	public static void register(final IEventBus modBus) {
		modBus.addListener((final FMLClientSetupEvent event) -> ForgeOverlayLayerRegistry.registerOverlays());
		MinecraftForge.EVENT_BUS.addListener(ForgeOverlayLayerRegistry::renderOverlay);
	}

	private static void registerOverlays() {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final List<IIngameOverlay> overlays = ForgeOverlayLayerRegistry.getOverlays(layer);
			for (final IIngameOverlay overlay : overlays) {
				ForgeOverlayLayerRegistry.LAYERS.put(overlay, layer);
			}

			OverlayRegistry.registerOverlayBelow(overlays.get(0), MinecraftOverlayEvents.getId(layer, false).getPath(), (gui, pose, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(pose, layer, false));
			OverlayRegistry.registerOverlayAbove(overlays.get(overlays.size() - 1), MinecraftOverlayEvents.getId(layer, true).getPath(), (gui, pose, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(pose, layer, true));
		}

		OverlayRegistry.registerOverlayTop(MinecraftOverlayEvents.getId().getPath(), (gui, pose, partialTick, width, height) -> MinecraftOverlayEvents.fireHud(pose));
	}

	private static void renderOverlay(final RenderGameOverlayEvent.PreLayer event) {
		final OverlayLayer layer = ForgeOverlayLayerRegistry.LAYERS.get(event.getOverlay());
		if (layer != null && MinecraftOverlayEvents.isCancelled(layer)) {
			event.setCanceled(true);
		}
	}

	private static List<IIngameOverlay> getOverlays(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> List.of(ForgeIngameGui.VIGNETTE_ELEMENT, ForgeIngameGui.SPYGLASS_ELEMENT, ForgeIngameGui.HELMET_ELEMENT, ForgeIngameGui.FROSTBITE_ELEMENT, ForgeIngameGui.PORTAL_ELEMENT);
			case CROSSHAIR -> List.of(ForgeIngameGui.CROSSHAIR_ELEMENT);
			case HOTBAR -> List.of(ForgeIngameGui.HOTBAR_ELEMENT);
			case ARMOR -> List.of(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
			case HEALTH -> List.of(ForgeIngameGui.PLAYER_HEALTH_ELEMENT);
			case FOOD -> List.of(ForgeIngameGui.FOOD_LEVEL_ELEMENT);
			case AIR -> List.of(ForgeIngameGui.AIR_LEVEL_ELEMENT);
			case MOUNT_HEALTH -> List.of(ForgeIngameGui.MOUNT_HEALTH_ELEMENT);
			case CONTEXTUAL_BAR -> List.of(ForgeIngameGui.JUMP_BAR_ELEMENT, ForgeIngameGui.EXPERIENCE_BAR_ELEMENT);
			case SELECTED_ITEM_NAME -> List.of(ForgeIngameGui.ITEM_NAME_ELEMENT);
			case EFFECTS -> List.of(ForgeIngameGui.POTION_ICONS_ELEMENT);
			case BOSS_BAR -> List.of(ForgeIngameGui.BOSS_HEALTH_ELEMENT);
			case SLEEP -> List.of(ForgeIngameGui.SLEEP_FADE_ELEMENT);
			case SCOREBOARD -> List.of(ForgeIngameGui.SCOREBOARD_ELEMENT);
			case ACTION_BAR -> List.of(ForgeIngameGui.RECORD_OVERLAY_ELEMENT);
			case TITLE -> List.of(ForgeIngameGui.TITLE_TEXT_ELEMENT);
			case CHAT -> List.of(ForgeIngameGui.CHAT_PANEL_ELEMENT);
			case PLAYER_LIST -> List.of(ForgeIngameGui.PLAYER_LIST_ELEMENT);
			case SUBTITLES -> List.of(ForgeIngameGui.SUBTITLES_ELEMENT);
		};
	}

}