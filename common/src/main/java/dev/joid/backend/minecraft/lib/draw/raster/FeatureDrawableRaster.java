package dev.joid.backend.minecraft.lib.draw.raster;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.joid.lib.draw.raster.DrawableRaster;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureDrawableRaster implements DrawableRaster {

	private static final PoseStack         POSE    = new PoseStack();
	private static final SubmitNodeStorage STORAGE = new SubmitNodeStorage();

	private final Lighting.Entry                             lighting;
	private final BiConsumer<PoseStack, SubmitNodeCollector> submit;

	public static @NonNull FeatureDrawableRaster create(final @NonNull Lighting.Entry lighting, final @NonNull BiConsumer<PoseStack, SubmitNodeCollector> submit) {
		return new FeatureDrawableRaster(lighting, submit);
	}

	@Override
	public void draw(final int width, final int height) {
		final Minecraft minecraft = Minecraft.getInstance();
		final float size = Math.min(width, height);
		minecraft.gameRenderer.lighting().setupFor(this.lighting);
		FeatureDrawableRaster.POSE.pushPose();
		try {
			FeatureDrawableRaster.POSE.translate(width / 2F, height / 2F, 0F);
			FeatureDrawableRaster.POSE.scale(size, -size, size);
			this.submit.accept(FeatureDrawableRaster.POSE, FeatureDrawableRaster.STORAGE);
			minecraft.gameRenderer.featureRenderDispatcher().renderAllFeatures(FeatureDrawableRaster.STORAGE);
		} finally {
			FeatureDrawableRaster.POSE.popPose();
		}
	}

}