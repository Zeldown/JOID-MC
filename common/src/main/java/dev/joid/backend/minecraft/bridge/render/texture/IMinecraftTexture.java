package dev.joid.backend.minecraft.bridge.render.texture;

import com.mojang.renderpearl.api.textures.GpuTextureView;

import dev.joid.lib.bridge.render.texture.ITexture;

public interface IMinecraftTexture extends ITexture {

	public GpuTextureView getView();

}