package dev.joid.backend.minecraft.bridge.render.texture;

import com.mojang.blaze3d.textures.GpuTextureView;

import dev.joid.lib.bridge.render.texture.ITexture;

public interface IGpuTexture extends ITexture {

	public GpuTextureView getView();

}