package dev.joid.backend.minecraft.loader.event;

import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
import dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftOverlayEvents {

	public static void fireHud(final PoseStack pose) {
		OverlayLayerRenderer.extract(pose);
	}

	public static void fireLayer(final PoseStack pose, final OverlayLayer layer, final boolean post) {
		OverlayLayerRenderer.extract(pose, layer, post);
	}

	public static void fireHiddenHud() {
		OverlayLayerRenderer.extractHiddenHud(new PoseStack());
	}

	public static boolean isCancelled(final OverlayLayer layer) {
		return OverlayLayerRenderer.isCancelled(layer);
	}

	public static @NonNull ResourceLocation getId() {
		return ResourceLocation.tryBuild(MinecraftJoid.MOD_ID, "overlay");
	}

	public static @NonNull ResourceLocation getId(final OverlayLayer layer, final boolean post) {
		return ResourceLocation.tryBuild(MinecraftJoid.MOD_ID, "overlay/" + layer.name().toLowerCase(Locale.ROOT) + (post ? "_post" : "_pre"));
	}

}