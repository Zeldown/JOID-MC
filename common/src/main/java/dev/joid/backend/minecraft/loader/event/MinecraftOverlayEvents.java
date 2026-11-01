package dev.joid.backend.minecraft.loader.event;

import java.util.Locale;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftOverlayEvents {

	public static void fireHud(final GuiGraphics graphics) {
		OverlayLayerRenderer.extract(graphics);
	}

	public static void fireLayer(final GuiGraphics graphics, final OverlayLayer layer, final boolean post) {
		OverlayLayerRenderer.extract(graphics, layer, post);
	}

	public static void fireHiddenHud() {
		final Minecraft minecraft = Minecraft.getInstance();
		OverlayLayerRenderer.extractHiddenHud(new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource()));
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		return OverlayLayerRenderer.isCancelled(layer);
	}

	public static @NonNull ResourceLocation getId() {
		return ResourceLocation.fromNamespaceAndPath(MinecraftJoid.MOD_ID, "overlay");
	}

	public static @NonNull ResourceLocation getId(final OverlayLayer layer, final boolean post) {
		return ResourceLocation.fromNamespaceAndPath(MinecraftJoid.MOD_ID, "overlay/" + layer.name().toLowerCase(Locale.ROOT) + (post ? "_post" : "_pre"));
	}

}