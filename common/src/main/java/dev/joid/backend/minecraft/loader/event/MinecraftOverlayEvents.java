package dev.joid.backend.minecraft.loader.event;

import java.util.Locale;

import dev.joid.backend.minecraft.Backend;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftOverlayEvents {

	public static void fireHud(final @NonNull GuiGraphicsExtractor graphics) {
		OverlayLayerRenderer.extract(graphics);
	}

	public static void fireHiddenHud(final @NonNull GuiGraphicsExtractor graphics) {
		OverlayLayerRenderer.extractHiddenHud(graphics);
	}

	public static void fireLayer(final @NonNull GuiGraphicsExtractor graphics, final @NonNull OverlayLayer layer, final boolean post) {
		OverlayLayerRenderer.extract(graphics, layer, post);
	}

	public static boolean isCancelled(final @NonNull OverlayLayer layer) {
		return OverlayLayerRenderer.isCancelled(layer);
	}

	public static @NonNull Identifier getId() {
		return Identifier.fromNamespaceAndPath(Backend.MOD_ID, "overlay");
	}

	public static @NonNull Identifier getId(final @NonNull OverlayLayer layer, final boolean post) {
		return Identifier.fromNamespaceAndPath(Backend.MOD_ID, "overlay/" + layer.name().toLowerCase(Locale.ROOT) + (post ? "_post" : "_pre"));
	}

	public static @NonNull Identifier getHiddenHudId() {
		return Identifier.fromNamespaceAndPath(Backend.MOD_ID, "overlay/hidden_hud");
	}

}