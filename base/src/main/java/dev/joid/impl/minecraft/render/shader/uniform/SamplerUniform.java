package dev.joid.impl.minecraft.render.shader.uniform;

import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class SamplerUniform implements dev.joid.lib.bridge.render.shader.uniform.SamplerUniform {

	private ITexture      texture;
	private TextureWrap   wrap;
	private TextureFilter filter;

	@Override
	public void setValue(final @NonNull ITexture texture, final @NonNull TextureFilter filter, final @NonNull TextureWrap wrap) {
		this.texture = texture;
		this.filter  = filter;
		this.wrap    = wrap;
	}

}