package dev.joid.backend.minecraft.lib.draw.raster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;

import dev.joid.lib.draw.raster.DrawableRaster;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiDrawableRaster implements DrawableRaster {

	private static final List<PoseStack>                      POSE_LIST   = new ArrayList<>();
	private static final List<MultiBufferSource.BufferSource> BUFFER_LIST = new ArrayList<>();

	private static int rasterDepth;

	private final Runnable                                                lighting;
	private final BiConsumer<PoseStack, MultiBufferSource.BufferSource> draw;

	public static @NonNull GuiDrawableRaster of(final @NonNull Runnable lighting, final @NonNull BiConsumer<PoseStack, MultiBufferSource.BufferSource> draw) {
		return new GuiDrawableRaster(lighting, draw);
	}

	@Override
	public void draw(final int width, final int height) {
		final float size = Math.min(width, height);
		final Matrix4f projection = RenderSystem.getProjectionMatrix();
		final VertexSorting sorting = RenderSystem.getVertexSorting();
		final Matrix4fStack modelView = RenderSystem.getModelViewStack();
		final PoseStack pose = GuiDrawableRaster.getPose(GuiDrawableRaster.rasterDepth);
		final MultiBufferSource.BufferSource buffer = GuiDrawableRaster.getBuffer(GuiDrawableRaster.rasterDepth);
		RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0F, width, height, 0F, -10000F, 10000F), VertexSorting.ORTHOGRAPHIC_Z);
		modelView.pushMatrix();
		modelView.identity();
		RenderSystem.applyModelViewMatrix();
		this.lighting.run();
		pose.pushPose();
		GuiDrawableRaster.rasterDepth++;
		try {
			pose.translate(width / 2F, height / 2F, 0F);
			pose.mulPose(new Matrix4f().scaling(size, -size, size));
			this.draw.accept(pose, buffer);
			buffer.endBatch();
		} finally {
			GuiDrawableRaster.rasterDepth--;
			pose.popPose();
			Lighting.setupFor3DItems();
			modelView.popMatrix();
			RenderSystem.applyModelViewMatrix();
			RenderSystem.setProjectionMatrix(projection, sorting);
		}
	}

	private static PoseStack getPose(final int depth) {
		if (depth == GuiDrawableRaster.POSE_LIST.size()) {
			GuiDrawableRaster.POSE_LIST.add(new PoseStack());
		}

		return GuiDrawableRaster.POSE_LIST.get(depth);
	}

	private static MultiBufferSource.BufferSource getBuffer(final int depth) {
		if (depth == GuiDrawableRaster.BUFFER_LIST.size()) {
			GuiDrawableRaster.BUFFER_LIST.add(new RenderBuffers(1).bufferSource());
		}

		return GuiDrawableRaster.BUFFER_LIST.get(depth);
	}

}