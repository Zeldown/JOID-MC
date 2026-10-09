package dev.joid.backend.minecraft.bridge.render.shader;

import dev.joid.lib.bridge.render.shader.source.GlslDialect;
import dev.joid.lib.bridge.render.shader.source.GlslShaderTranslator;
import dev.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import dev.joid.lib.bridge.render.shader.source.UniformLayout;
import lombok.NonNull;

public final class MinecraftShaderTranslator extends GlslShaderTranslator {

	private MinecraftShaderTranslator() {
		super(GlslDialect.GLSL_330, UniformLayout.BLOCK);
	}

	public static @NonNull MinecraftShaderTranslator create() {
		return new MinecraftShaderTranslator();
	}

	@Override
	protected @NonNull String declareAttribute(final @NonNull ShaderBuiltin builtin, final int location) {
		return "in " + builtin.getType() + " " + builtin.getIdentifier() + ";\n";
	}

}