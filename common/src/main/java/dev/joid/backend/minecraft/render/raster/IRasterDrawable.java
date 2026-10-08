package dev.joid.backend.minecraft.render.raster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;

@FunctionalInterface
public interface IRasterDrawable {

	public void submit(final PoseStack pose, final SubmitNodeCollector collector);

}