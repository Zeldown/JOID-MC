package dev.joid.backend.minecraft.lib.draw.raster;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;

import dev.joid.backend.minecraft.bridge.render.MinecraftRenderBridge;
import dev.joid.backend.minecraft.bridge.render.MinecraftRenderTarget;
import dev.joid.lib.bridge.BridgeHandler;
import dev.joid.lib.draw.raster.DrawableRaster;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureDrawableRaster implements DrawableRaster {

	private static final List<PoseStack>         POSE_LIST    = new ArrayList<>();
	private static final List<SubmitNodeStorage> STORAGE_LIST = new ArrayList<>();

	private static int rasterDepth;

	private final Lighting.Entry                             lighting;
	private final BiConsumer<PoseStack, SubmitNodeCollector> submit;

	public static @NonNull FeatureDrawableRaster of(final @NonNull Lighting.Entry lighting, final @NonNull BiConsumer<PoseStack, SubmitNodeCollector> submit) {
		return new FeatureDrawableRaster(lighting, submit);
	}

	@Override
	public void draw(final int width, final int height) {
		final Minecraft minecraft = Minecraft.getInstance();
		final float size = Math.min(width, height);
		final GpuBufferSlice lights = RenderSystem.getShaderLights();
		final PoseStack pose = FeatureDrawableRaster.getPose(FeatureDrawableRaster.rasterDepth);
		final SubmitNodeStorage storage = FeatureDrawableRaster.getStorage(FeatureDrawableRaster.rasterDepth);
		minecraft.gameRenderer.lighting().setupFor(this.lighting);
		pose.pushPose();
		FeatureDrawableRaster.rasterDepth++;
		try {
			pose.translate(width / 2F, height / 2F, 0F);
			pose.scale(size, -size, size);
			this.submit.accept(pose, storage);
			final MinecraftRenderTarget target = ((MinecraftRenderBridge) BridgeHandler.RENDER.get()).getRasterTarget();
			try (final FeatureRenderDispatcher.PreparedFrame frame = minecraft.gameRenderer.featureRenderDispatcher().prepareFrame(storage); final RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "JOID Raster", target.getView(), Optional.empty(), target.getDepthView(), OptionalDouble.empty())) {
				RenderSystem.bindDefaultUniforms(pass);
				FeatureRenderDispatcher.renderAllFeatures(pass, frame);
			}
		} finally {
			FeatureDrawableRaster.rasterDepth--;
			pose.popPose();
			RenderSystem.setShaderLights(lights);
		}
	}

	private static PoseStack getPose(final int depth) {
		if (depth == FeatureDrawableRaster.POSE_LIST.size()) {
			FeatureDrawableRaster.POSE_LIST.add(new PoseStack());
		}

		return FeatureDrawableRaster.POSE_LIST.get(depth);
	}

	private static SubmitNodeStorage getStorage(final int depth) {
		if (depth == FeatureDrawableRaster.STORAGE_LIST.size()) {
			FeatureDrawableRaster.STORAGE_LIST.add(new SubmitNodeStorage());
		}

		return FeatureDrawableRaster.STORAGE_LIST.get(depth);
	}

}