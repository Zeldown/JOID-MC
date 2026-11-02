package dev.joid.backend.minecraft.forge.registry;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import dev.joid.backend.minecraft.loader.event.MinecraftOverlayEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeOverlayLayerRegistry {

	private static boolean     cameraStarted;
	private static MatrixStack pose;

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(ForgeOverlayLayerRegistry::pre);
		MinecraftForge.EVENT_BUS.addListener(ForgeOverlayLayerRegistry::post);
	}

	public static boolean startLayer(final MatrixStack pose, final OverlayLayer layer) {
		MinecraftOverlayEvents.fireLayer(pose, layer, false);
		if (!MinecraftOverlayEvents.isCancelled(layer)) {
			return true;
		}

		MinecraftOverlayEvents.fireLayer(pose, layer, true);
		return false;
	}

	private static void pre(final RenderGameOverlayEvent.Pre event) {
		final ElementType type = event.getType();
		if (type == ElementType.ALL) {
			ForgeOverlayLayerRegistry.cameraStarted = false;
			ForgeOverlayLayerRegistry.pose          = event.getMatrixStack();
			return;
		}

		final OverlayLayer layer = ForgeOverlayLayerRegistry.getLayer(type);
		if (layer == null) {
			return;
		}

		final MatrixStack pose = ForgeOverlayLayerRegistry.pose;
		if (layer != OverlayLayer.CAMERA_OVERLAYS || !ForgeOverlayLayerRegistry.cameraStarted) {
			ForgeOverlayLayerRegistry.cameraStarted |= layer == OverlayLayer.CAMERA_OVERLAYS;
			MinecraftOverlayEvents.fireLayer(pose, layer, false);
		}

		if (MinecraftOverlayEvents.isCancelled(layer)) {
			event.setCanceled(true);
			if (ForgeOverlayLayerRegistry.isLast(type)) {
				MinecraftOverlayEvents.fireLayer(pose, layer, true);
			}
		}
	}

	private static void post(final RenderGameOverlayEvent.Post event) {
		final ElementType type = event.getType();
		if (type == ElementType.ALL) {
			MinecraftOverlayEvents.fireHud(event.getMatrixStack());
			return;
		}

		final OverlayLayer layer = ForgeOverlayLayerRegistry.getLayer(type);
		if (layer != null && ForgeOverlayLayerRegistry.isLast(type)) {
			MinecraftOverlayEvents.fireLayer(ForgeOverlayLayerRegistry.pose, layer, true);
		}
	}

	private static boolean isLast(final ElementType type) {
		return type != ElementType.VIGNETTE && type != ElementType.HELMET;
	}

	private static OverlayLayer getLayer(final ElementType type) {
		switch (type) {
			case VIGNETTE:
			case HELMET:
			case PORTAL:
				return OverlayLayer.CAMERA_OVERLAYS;
			case CROSSHAIRS:
				return OverlayLayer.CROSSHAIR;
			case HOTBAR:
				return OverlayLayer.HOTBAR;
			case ARMOR:
				return OverlayLayer.ARMOR;
			case HEALTH:
				return OverlayLayer.HEALTH;
			case FOOD:
				return OverlayLayer.FOOD;
			case AIR:
				return OverlayLayer.AIR;
			case HEALTHMOUNT:
				return OverlayLayer.MOUNT_HEALTH;
			case EXPERIENCE:
			case JUMPBAR:
				return OverlayLayer.CONTEXTUAL_BAR;
			case POTION_ICONS:
				return OverlayLayer.EFFECTS;
			case BOSSHEALTH:
				return OverlayLayer.BOSS_BAR;
			case SUBTITLES:
				return OverlayLayer.SUBTITLES;
			case CHAT:
				return OverlayLayer.CHAT;
			case PLAYER_LIST:
				return OverlayLayer.PLAYER_LIST;
			default:
				return null;
		}
	}

}