package fr.augma.joidmc.render.raster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;

public interface RasterDrawer {

	public void draw(PoseStack pose, SubmitNodeCollector collector);

}