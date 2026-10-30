package dev.joid.backend.minecraft.bridge.render;

import dev.joid.base.lwjgl2.opengl.Lwjgl2GlBinding;
import dev.joid.base.opengl.render.GlRenderBridge;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import lombok.NonNull;

public final class MinecraftRenderBridge extends GlRenderBridge {

	public MinecraftRenderBridge() {
		super(Lwjgl2GlBinding.inst());
	}

	@Override
	public boolean canWrap(final @NonNull TextureWrap wrap) {
		return wrap != TextureWrap.CLAMP_TO_BORDER;
	}

}