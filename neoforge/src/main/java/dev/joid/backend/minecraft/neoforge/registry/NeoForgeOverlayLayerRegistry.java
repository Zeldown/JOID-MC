package dev.joid.backend.minecraft.neoforge.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeOverlayLayerRegistry {

	private static final Map<ResourceLocation, OverlayLayer> LAYERS = new HashMap<>();

	static {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			for (final VanillaGuiOverlay overlay : NeoForgeOverlayLayerRegistry.getOverlays(layer)) {
				NeoForgeOverlayLayerRegistry.LAYERS.put(overlay.id(), layer);
			}
		}
	}

	public static void register(final RegisterGuiOverlaysEvent event) {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final List<VanillaGuiOverlay> overlays = NeoForgeOverlayLayerRegistry.getOverlays(layer);
			event.registerBelow(overlays.get(0).id(), MinecraftOverlayEvents.getId(layer, false).getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(graphics, layer, false));
			event.registerAbove(overlays.get(overlays.size() - 1).id(), MinecraftOverlayEvents.getId(layer, true).getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(graphics, layer, true));
		}

		event.registerAboveAll(MinecraftOverlayEvents.getId().getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireHud(graphics));
	}

	public static void cancel(final RenderGuiOverlayEvent.Pre event) {
		final OverlayLayer layer = NeoForgeOverlayLayerRegistry.LAYERS.get(event.getOverlay().id());
		if (layer != null && MinecraftOverlayEvents.isCancelled(layer)) {
			event.setCanceled(true);
		}
	}

	private static List<VanillaGuiOverlay> getOverlays(final OverlayLayer layer) {
		return switch (layer) {
			case CAMERA_OVERLAYS -> List.of(VanillaGuiOverlay.VIGNETTE, VanillaGuiOverlay.SPYGLASS, VanillaGuiOverlay.HELMET, VanillaGuiOverlay.FROSTBITE, VanillaGuiOverlay.PORTAL);
			case CROSSHAIR -> List.of(VanillaGuiOverlay.CROSSHAIR);
			case HOTBAR -> List.of(VanillaGuiOverlay.HOTBAR);
			case ARMOR -> List.of(VanillaGuiOverlay.ARMOR_LEVEL);
			case HEALTH -> List.of(VanillaGuiOverlay.PLAYER_HEALTH);
			case FOOD -> List.of(VanillaGuiOverlay.FOOD_LEVEL);
			case AIR -> List.of(VanillaGuiOverlay.AIR_LEVEL);
			case MOUNT_HEALTH -> List.of(VanillaGuiOverlay.MOUNT_HEALTH);
			case CONTEXTUAL_BAR -> List.of(VanillaGuiOverlay.JUMP_BAR, VanillaGuiOverlay.EXPERIENCE_BAR);
			case SELECTED_ITEM_NAME -> List.of(VanillaGuiOverlay.ITEM_NAME);
			case EFFECTS -> List.of(VanillaGuiOverlay.POTION_ICONS);
			case BOSS_BAR -> List.of(VanillaGuiOverlay.BOSS_EVENT_PROGRESS);
			case SLEEP -> List.of(VanillaGuiOverlay.SLEEP_FADE);
			case SCOREBOARD -> List.of(VanillaGuiOverlay.SCOREBOARD);
			case ACTION_BAR -> List.of(VanillaGuiOverlay.RECORD_OVERLAY);
			case TITLE -> List.of(VanillaGuiOverlay.TITLE_TEXT);
			case CHAT -> List.of(VanillaGuiOverlay.CHAT_PANEL);
			case PLAYER_LIST -> List.of(VanillaGuiOverlay.PLAYER_LIST);
			case SUBTITLES -> List.of(VanillaGuiOverlay.SUBTITLES);
		};
	}

}