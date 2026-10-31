package dev.joid.backend.minecraft.forge.registry;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayLayerRegistry {

	private static final Map<ResourceLocation, OverlayLayer> LAYERS = new HashMap<>();

	static {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			for (final VanillaGuiOverlay overlay : ForgeOverlayLayerRegistry.getOverlays(layer)) {
				ForgeOverlayLayerRegistry.LAYERS.put(overlay.id(), layer);
			}
		}
	}

	public static void register(final IEventBus modBus) {
		modBus.addListener(ForgeOverlayLayerRegistry::registerOverlays);
		MinecraftForge.EVENT_BUS.addListener(ForgeOverlayLayerRegistry::renderOverlay);
	}

	private static void registerOverlays(final RegisterGuiOverlaysEvent event) {
		for (final OverlayLayer layer : OverlayLayer.values()) {
			final List<VanillaGuiOverlay> overlays = ForgeOverlayLayerRegistry.getOverlays(layer);
			event.registerBelow(overlays.get(0).id(), MinecraftOverlayEvents.getId(layer, false).getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(graphics, layer, false));
			event.registerAbove(overlays.get(overlays.size() - 1).id(), MinecraftOverlayEvents.getId(layer, true).getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireLayer(graphics, layer, true));
		}

		event.registerAboveAll(MinecraftOverlayEvents.getId().getPath(), (gui, graphics, partialTick, width, height) -> MinecraftOverlayEvents.fireHud(graphics));
	}

	private static void renderOverlay(final RenderGuiOverlayEvent.Pre event) {
		final OverlayLayer layer = ForgeOverlayLayerRegistry.LAYERS.get(event.getOverlay().id());
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