package fr.augma.joidmc.overlay;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.augma.joidmc.Constants;
import fr.augma.joidmc.lib.bridge.ui.OverlayBridge;
import fr.augma.joidmc.lib.bridge.ui.OverlayLayerMap;
import fr.augma.joidmc.lib.ui.core.data.overlay.render.ElementType;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.resources.Identifier;

public final class OverlayHandler {

	private static final OverlayLayerMap  LAYER_MAP    = OverlayLayerMap.create()
			.map(ElementType.HELMET, VanillaHudElements.MISC_OVERLAYS)
			.map(ElementType.PORTAL, VanillaHudElements.MISC_OVERLAYS)
			.map(ElementType.CROSSHAIRS, VanillaHudElements.CROSSHAIR)
			.map(ElementType.BOSSHEALTH, VanillaHudElements.BOSS_BAR)
			.map(ElementType.ARMOR, VanillaHudElements.ARMOR_BAR)
			.map(ElementType.HEALTH, VanillaHudElements.HEALTH_BAR)
			.map(ElementType.FOOD, VanillaHudElements.FOOD_BAR)
			.map(ElementType.AIR, VanillaHudElements.AIR_BAR)
			.map(ElementType.HOTBAR, VanillaHudElements.HOTBAR)
			.map(ElementType.EXPERIENCE, VanillaHudElements.INFO_BAR, VanillaHudElements.EXPERIENCE_LEVEL)
			.map(ElementType.TEXT, VanillaHudElements.OVERLAY_MESSAGE)
			.map(ElementType.HEALTHMOUNT, VanillaHudElements.MOUNT_HEALTH)
			.map(ElementType.JUMPBAR, VanillaHudElements.INFO_BAR)
			.map(ElementType.CHAT, VanillaHudElements.CHAT)
			.map(ElementType.PLAYER_LIST, VanillaHudElements.PLAYER_LIST)
			.map(ElementType.EFFECTS, VanillaHudElements.MOB_EFFECTS);
	private static final List<Identifier> ELEMENT_LIST = List.of(
			VanillaHudElements.MISC_OVERLAYS,
			VanillaHudElements.CROSSHAIR,
			VanillaHudElements.SPECTATOR_MENU,
			VanillaHudElements.HOTBAR,
			VanillaHudElements.ARMOR_BAR,
			VanillaHudElements.HEALTH_BAR,
			VanillaHudElements.FOOD_BAR,
			VanillaHudElements.AIR_BAR,
			VanillaHudElements.MOUNT_HEALTH,
			VanillaHudElements.INFO_BAR,
			VanillaHudElements.EXPERIENCE_LEVEL,
			VanillaHudElements.HELD_ITEM_TOOLTIP,
			VanillaHudElements.SPECTATOR_TOOLTIP,
			VanillaHudElements.MOB_EFFECTS,
			VanillaHudElements.BOSS_BAR,
			VanillaHudElements.SLEEP,
			VanillaHudElements.DEMO_TIMER,
			VanillaHudElements.SCOREBOARD,
			VanillaHudElements.OVERLAY_MESSAGE,
			VanillaHudElements.TITLE_AND_SUBTITLE,
			VanillaHudElements.CHAT,
			VanillaHudElements.PLAYER_LIST,
			VanillaHudElements.SUBTITLES);

	public static void register() {
		HudElementRegistry.addFirst(OverlayHandler.getIdentifier(ElementType.ALL, false), (graphics, delta) -> OverlayBridge.inst().extract(graphics, ElementType.ALL, false, false));
		HudElementRegistry.addLast(OverlayHandler.getIdentifier(ElementType.ALL, true), (graphics, delta) -> OverlayBridge.inst().extract(graphics, ElementType.ALL, true, false));
		for (final Map.Entry<ElementType, List<Identifier>> entry : OverlayHandler.LAYER_MAP.getLayerMap().entrySet()) {
			final ElementType type = entry.getKey();
			HudElementRegistry.attachElementBefore(entry.getValue().getFirst(), OverlayHandler.getIdentifier(type, false), (graphics, delta) -> OverlayBridge.inst().extract(graphics, type, false, false));
			HudElementRegistry.attachElementAfter(entry.getValue().getLast(), OverlayHandler.getIdentifier(type, true), (graphics, delta) -> OverlayBridge.inst().extract(graphics, type, true, false));
		}

		for (final Identifier element : OverlayHandler.ELEMENT_LIST) {
			HudElementRegistry.replaceElement(element, original -> (graphics, delta) -> {
				if (!OverlayHandler.LAYER_MAP.isCancelled(element)) {
					original.extractRenderState(graphics, delta);
				}
			});
		}

		ScreenEvents.AFTER_INIT.register((minecraft, screen, width, height) -> {
			ScreenEvents.beforeExtract(screen).register((current, graphics, mouseX, mouseY, delta) -> OverlayBridge.inst().extractScreen(graphics, false));
			ScreenEvents.afterExtract(screen).register((current, graphics, mouseX, mouseY, delta) -> OverlayBridge.inst().extractScreen(graphics, true));
			ScreenMouseEvents.allowMouseClick(screen).register((current, event) -> !OverlayBridge.inst().mousePressed(event.button()));
			ScreenMouseEvents.allowMouseRelease(screen).register((current, event) -> !OverlayBridge.inst().mouseReleased(event.button()));
			ScreenMouseEvents.allowMouseDrag(screen).register((current, event, horizontal, vertical) -> !OverlayBridge.inst().mouseDragged());
			ScreenMouseEvents.allowMouseScroll(screen).register((current, mouseX, mouseY, horizontal, vertical) -> !OverlayBridge.inst().mouseScrolled(vertical));
			ScreenKeyboardEvents.allowKeyPress(screen).register((current, event) -> !OverlayBridge.inst().keyPressed(event.key()));
			ScreenKeyboardEvents.allowCharType(screen).register((current, event) -> !OverlayBridge.inst().charTyped(event.codepoint()));
		});
	}

	private static Identifier getIdentifier(final ElementType type, final boolean post) {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "overlay/" + type.name().toLowerCase(Locale.ROOT) + (post ? "_post" : "_pre"));
	}

}