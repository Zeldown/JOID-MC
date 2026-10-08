package dev.joid.impl.minecraft.render.texture;

import dev.joid.lib.bridge.render.texture.ITexture;

import com.mojang.blaze3d.textures.GpuTextureView;

public interface IGpuTexture extends ITexture {

	public GpuTextureView getView();

}