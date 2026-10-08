package dev.joid.backend.minecraft.render.shader;

import dev.joid.lib.bridge.render.shader.source.GlslDialect;
import dev.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import dev.joid.lib.bridge.render.shader.source.UniformLayout;
import lombok.NonNull;

public final class GlslShaderTranslator extends dev.joid.lib.bridge.render.shader.source.GlslShaderTranslator {

	private GlslShaderTranslator() {
		super(GlslDialect.GLSL_330, UniformLayout.BLOCK);
	}

	public static @NonNull GlslShaderTranslator create() {
		return new GlslShaderTranslator();
	}

	@Override
	protected @NonNull String declareAttribute(final @NonNull ShaderBuiltin builtin, final int location) {
		return "in " + builtin.getType() + " " + builtin.getIdentifier() + ";\n";
	}

}