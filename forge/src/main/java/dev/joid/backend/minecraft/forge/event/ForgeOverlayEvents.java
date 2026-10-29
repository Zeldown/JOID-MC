package dev.joid.backend.minecraft.forge.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayEvents {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ForgeOverlayEvents());
	}

	@SubscribeEvent
	public void pre(final RenderGameOverlayEvent.Pre event) {
		final OverlayLayer layer = ForgeOverlayEvents.getLayer(event.type);
		if (layer == null) {
			return;
		}

		if (event.type != ElementType.PORTAL) {
			MinecraftOverlayEvents.fireLayer(layer, false);
		}

		if (MinecraftOverlayEvents.isCancelled(layer)) {
			event.setCanceled(true);
			if (event.type != ElementType.HELMET) {
				MinecraftOverlayEvents.fireLayer(layer, true);
			}
		}
	}

	@SubscribeEvent
	public void post(final RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.ALL) {
			MinecraftOverlayEvents.fireHud();
			return;
		}

		final OverlayLayer layer = ForgeOverlayEvents.getLayer(event.type);
		if (layer != null && event.type != ElementType.HELMET) {
			MinecraftOverlayEvents.fireLayer(layer, true);
		}
	}

	private static OverlayLayer getLayer(final ElementType type) {
		switch (type) {
			case HELMET:
			case PORTAL:
				return OverlayLayer.CAMERA_OVERLAYS;
			case CROSSHAIRS:
				return OverlayLayer.CROSSHAIR;
			case BOSSHEALTH:
				return OverlayLayer.BOSS_BAR;
			case HEALTH:
				return OverlayLayer.HEALTH;
			case ARMOR:
				return OverlayLayer.ARMOR;
			case FOOD:
				return OverlayLayer.FOOD;
			case HEALTHMOUNT:
				return OverlayLayer.MOUNT_HEALTH;
			case AIR:
				return OverlayLayer.AIR;
			case HOTBAR:
				return OverlayLayer.HOTBAR;
			case EXPERIENCE:
			case JUMPBAR:
				return OverlayLayer.CONTEXTUAL_BAR;
			case CHAT:
				return OverlayLayer.CHAT;
			case PLAYER_LIST:
				return OverlayLayer.PLAYER_LIST;
			default:
				return null;
		}
	}

}