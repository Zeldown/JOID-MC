package fr.augma.joidmc.overlay;

import fr.augma.joidmc.lib.bridge.ui.OverlayBridge;
import fr.augma.joidmc.lib.bridge.ui.OverlayLayerMap;
import fr.augma.joidmc.lib.ui.core.data.overlay.render.ElementType;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public final class OverlayHandler {

	private static final OverlayLayerMap LAYER_MAP = OverlayLayerMap.create()
			.map(ElementType.HELMET, VanillaGuiLayers.CAMERA_OVERLAYS)
			.map(ElementType.PORTAL, VanillaGuiLayers.CAMERA_OVERLAYS)
			.map(ElementType.CROSSHAIRS, VanillaGuiLayers.CROSSHAIR)
			.map(ElementType.BOSSHEALTH, VanillaGuiLayers.BOSS_OVERLAY)
			.map(ElementType.ARMOR, VanillaGuiLayers.ARMOR_LEVEL)
			.map(ElementType.HEALTH, VanillaGuiLayers.PLAYER_HEALTH)
			.map(ElementType.FOOD, VanillaGuiLayers.FOOD_LEVEL)
			.map(ElementType.AIR, VanillaGuiLayers.AIR_LEVEL)
			.map(ElementType.HOTBAR, VanillaGuiLayers.HOTBAR)
			.map(ElementType.EXPERIENCE, VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND, VanillaGuiLayers.EXPERIENCE_LEVEL, VanillaGuiLayers.CONTEXTUAL_INFO_BAR)
			.map(ElementType.TEXT, VanillaGuiLayers.OVERLAY_MESSAGE)
			.map(ElementType.HEALTHMOUNT, VanillaGuiLayers.VEHICLE_HEALTH)
			.map(ElementType.JUMPBAR, VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND, VanillaGuiLayers.CONTEXTUAL_INFO_BAR)
			.map(ElementType.CHAT, VanillaGuiLayers.CHAT)
			.map(ElementType.PLAYER_LIST, VanillaGuiLayers.TAB_LIST)
			.map(ElementType.EFFECTS, VanillaGuiLayers.EFFECTS);

	public static void register() {
		final IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener(EventPriority.NORMAL, true, RenderGuiEvent.Pre.class, event -> {
			OverlayBridge.inst().extract(event.getGuiGraphics(), ElementType.ALL, false, event.isCanceled());
			if (OverlayBridge.inst().isCancelled(ElementType.ALL)) {
				event.setCanceled(true);
			}
		});
		bus.addListener(RenderGuiEvent.Post.class, event -> OverlayBridge.inst().extract(event.getGuiGraphics(), ElementType.ALL, true, false));
		bus.addListener(EventPriority.NORMAL, true, RenderGuiLayerEvent.Pre.class, event -> {
			OverlayHandler.LAYER_MAP.extractPre(event.getGuiGraphics(), event.getName(), event.isCanceled());
			if (OverlayHandler.LAYER_MAP.isCancelled(event.getName())) {
				event.setCanceled(true);
			}
		});
		bus.addListener(RenderGuiLayerEvent.Post.class, event -> OverlayHandler.LAYER_MAP.extractPost(event.getGuiGraphics(), event.getName()));
		bus.addListener(ScreenEvent.Render.Pre.class, event -> OverlayBridge.inst().extractScreen(event.getGuiGraphics(), false));
		bus.addListener(ScreenEvent.Render.Post.class, event -> OverlayBridge.inst().extractScreen(event.getGuiGraphics(), true));
		bus.addListener(ScreenEvent.MouseButtonPressed.Pre.class, event -> {
			if (OverlayBridge.inst().mousePressed(event.getButton())) {
				event.setCanceled(true);
			}
		});
		bus.addListener(ScreenEvent.MouseButtonReleased.Pre.class, event -> {
			if (OverlayBridge.inst().mouseReleased(event.getButton())) {
				event.setCanceled(true);
			}
		});
		bus.addListener(ScreenEvent.MouseDragged.Pre.class, event -> {
			if (OverlayBridge.inst().mouseDragged()) {
				event.setCanceled(true);
			}
		});
		bus.addListener(ScreenEvent.MouseScrolled.Pre.class, event -> {
			if (OverlayBridge.inst().mouseScrolled(event.getScrollDeltaY())) {
				event.setCanceled(true);
			}
		});
		bus.addListener(ScreenEvent.KeyPressed.Pre.class, event -> {
			if (OverlayBridge.inst().keyPressed(event.getKeyCode())) {
				event.setCanceled(true);
			}
		});
		bus.addListener(ScreenEvent.CharacterTyped.Pre.class, event -> {
			if (OverlayBridge.inst().charTyped(event.getCodePoint())) {
				event.setCanceled(true);
			}
		});
	}

}