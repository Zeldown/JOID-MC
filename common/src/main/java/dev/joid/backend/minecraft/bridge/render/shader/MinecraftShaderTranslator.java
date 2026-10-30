package dev.joid.backend.minecraft.bridge.render.shader;

import dev.joid.lib.bridge.render.shader.source.GlslDialect;
import dev.joid.lib.bridge.render.shader.source.GlslShaderTranslator;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import dev.joid.lib.bridge.render.shader.source.UniformLayout;
import lombok.NonNull;

public final class MinecraftShaderTranslator extends GlslShaderTranslator {

	public MinecraftShaderTranslator() {
		super(GlslDialect.GLSL_450, UniformLayout.BLOCK);
	}

	@Override
	protected @NonNull String declareVarying(final @NonNull ShaderVariable varying, final int location, final boolean output) {
		return "layout(location = " + location + ") " + super.declareVarying(varying, location, output);
	}

}