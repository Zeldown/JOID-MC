package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayEvents {

	private boolean cameraStarted;

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ForgeOverlayEvents());
	}

	@SubscribeEvent
	public void pre(final RenderGameOverlayEvent.Pre event) {
		final ElementType type = event.type;
		if (type == ElementType.ALL) {
			this.cameraStarted = false;
			return;
		}

		final OverlayLayer layer = ForgeOverlayEvents.getLayer(type);
		if (layer == null) {
			return;
		}

		if (layer != OverlayLayer.CAMERA_OVERLAYS || !this.cameraStarted && type != ElementType.PORTAL) {
			this.cameraStarted = layer == OverlayLayer.CAMERA_OVERLAYS;
			MinecraftOverlayEvents.fireLayer(layer, false);
		}

		if (MinecraftOverlayEvents.isCancelled(layer)) {
			event.setCanceled(true);
			if (ForgeOverlayEvents.isLast(type)) {
				MinecraftOverlayEvents.fireLayer(layer, true);
			}
		}
	}

	@SubscribeEvent
	public void post(final RenderGameOverlayEvent.Post event) {
		final ElementType type = event.type;
		if (type == ElementType.ALL) {
			MinecraftOverlayEvents.fireHud();
			return;
		}

		final OverlayLayer layer = ForgeOverlayEvents.getLayer(type);
		if (layer != null && ForgeOverlayEvents.isLast(type)) {
			MinecraftOverlayEvents.fireLayer(layer, true);
		}
	}

	private static boolean isLast(final ElementType type) {
		return type != ElementType.HELMET;
	}

	private static OverlayLayer getLayer(final ElementType type) {
		switch (type) {
			case HELMET:
			case PORTAL:
				return OverlayLayer.CAMERA_OVERLAYS;
			case HOTBAR:
				return OverlayLayer.HOTBAR;
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