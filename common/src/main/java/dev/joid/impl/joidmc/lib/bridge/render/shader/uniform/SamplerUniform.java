package dev.joid.impl.joidmc.lib.bridge.render.shader.uniform;

import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.bridge.render.texture.TextureFilter;
import dev.joid.lib.bridge.render.texture.TextureWrap;
import dev.joid.impl.joidmc.lib.bridge.render.texture.Texture;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class SamplerUniform implements dev.joid.lib.bridge.render.shader.uniform.SamplerUniform {

	private Texture       texture;
	private TextureFilter filter;
	private TextureWrap   wrap;

	@Override
	public void setValue(final @NonNull ITexture texture, final @NonNull TextureFilter filter, final @NonNull TextureWrap wrap) {
		this.texture = (Texture) texture;
		this.filter  = filter;
		this.wrap    = wrap;
	}

}