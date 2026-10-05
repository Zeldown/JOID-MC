package dev.joid.impl.joidmc.lib.bridge.render.shader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.zeldown.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderSource;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderVariable;
import lombok.NonNull;

public final class ShaderTranslator {

	public static final String BLOCK   = "JoidUniforms";
	public static final String STENCIL = "joid_Stencil";

	private static final List<ShaderVariable> INTERNAL_LIST = Arrays.asList(
			ShaderVariable.create("int", "joid_AlphaTest", "", false),
			ShaderVariable.create("float", "joid_AlphaThreshold", "", false),
			ShaderVariable.create("int", "joid_StencilTest", "", false),
			ShaderVariable.create("int", "joid_StencilFunction", "", false),
			ShaderVariable.create("int", "joid_StencilReference", "", false),
			ShaderVariable.create("int", "joid_StencilMask", "", false),
			ShaderVariable.create("int", "joid_StencilFail", "", false),
			ShaderVariable.create("int", "joid_StencilPass", "", false)
	);

	private static final String STENCIL_FUNCTIONS = """

			int joid_stencilValue() {
				return int(texelFetch(joid_Stencil, ivec2(gl_FragCoord.xy), 0).r * 255.0 + 0.5);
			}

			bool joid_stencilCompare(int value) {
				int reference = joid_StencilReference & joid_StencilMask;
				int current = value & joid_StencilMask;
				switch (joid_StencilFunction) {
				case 0:
					return false;
				case 1:
					return reference < current;
				case 2:
					return reference <= current;
				case 3:
					return reference > current;
				case 4:
					return reference >= current;
				case 5:
					return reference == current;
				case 6:
					return reference != current;
				default:
					return true;
				}
			}

			int joid_stencilApply(int operation, int value) {
				switch (operation) {
				case 1:
					return 0;
				case 2:
					return joid_StencilReference & 255;
				case 3:
					return min(value + 1, 255);
				case 4:
					return max(value - 1, 0);
				case 5:
					return ~value & 255;
				default:
					return value;
				}
			}
			""";

	private static final String COLOR_MAIN = """

			void main() {
				fragColor = vec4(0.0);
				joid_main();
				if (joid_AlphaTest != 0 && fragColor.a <= joid_AlphaThreshold) {
					discard;
				}

				if (joid_StencilTest != 0 && !joid_stencilCompare(joid_stencilValue())) {
					discard;
				}
			}
			""";

	private static final String STENCIL_MAIN = """

			void main() {
				fragColor = vec4(0.0);
				joid_main();
				if (joid_AlphaTest != 0 && fragColor.a <= joid_AlphaThreshold) {
					discard;
				}

				int value = joid_stencilValue();
				value = joid_stencilApply(joid_stencilCompare(value) ? joid_StencilPass : joid_StencilFail, value);
				fragColor = vec4(float(value) / 255.0, 0.0, 0.0, 1.0);
			}
			""";

	public static @NonNull List<ShaderVariable> getUniforms(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment) {
		final List<ShaderVariable> uniformList = new ArrayList<>();
		for (final ShaderBuiltin builtin : ShaderBuiltin.values()) {
			if (builtin.getKind() == ShaderBuiltin.Kind.UNIFORM && (vertex.getBuiltins().contains(builtin) || fragment.getBuiltins().contains(builtin))) {
				uniformList.add(ShaderVariable.create(builtin.getType(), builtin.getIdentifier(), "", false));
			}
		}

		uniformList.addAll(ShaderTranslator.INTERNAL_LIST);
		for (final ShaderSource source : new ShaderSource[] {vertex, fragment}) {
			for (final ShaderVariable uniform : source.getUniforms()) {
				if (uniformList.stream().noneMatch(variable -> variable.getName().equals(uniform.getName()))) {
					uniformList.add(uniform);
				}
			}
		}
		return uniformList;
	}

	public static @NonNull List<String> getSamplers(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment) {
		final List<String> samplerList = new ArrayList<>();
		for (final ShaderSource source : new ShaderSource[] {vertex, fragment}) {
			for (final ShaderVariable sampler : source.getSamplers()) {
				if (!samplerList.contains(sampler.getName())) {
					samplerList.add(sampler.getName());
				}
			}
		}
		return samplerList;
	}

	public static @NonNull String translateVertex(final @NonNull ShaderSource vertex, final @NonNull List<ShaderVariable> uniforms) {
		final StringBuilder builder = ShaderTranslator.createHeader(uniforms);
		builder.append("in vec3 aPosition;\nin vec2 aTexCoord;\nin vec4 aColor;\nin vec3 aNormal;\n");
		ShaderTranslator.appendSamplers(builder, vertex);
		for (final ShaderVariable output : vertex.getOutputs()) {
			builder.append(output.isFlat() ? "flat " : "").append("out ").append(output.getDeclaration()).append(";\n");
		}
		return builder.append("#line 1\n").append(vertex.getBody()).toString();
	}

	public static @NonNull String translateFragment(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment, final @NonNull List<ShaderVariable> uniforms, final boolean stencil) {
		final StringBuilder builder = ShaderTranslator.createHeader(uniforms);
		ShaderTranslator.appendSamplers(builder, fragment);
		builder.append("uniform sampler2D ").append(ShaderTranslator.STENCIL).append(";\n");

		final List<String> inputNames = new ArrayList<>();
		for (final ShaderVariable input : vertex.getOutputs()) {
			inputNames.add(input.getName());
			builder.append(input.isFlat() ? "flat " : "").append("in ").append(input.getDeclaration()).append(";\n");
		}

		for (final ShaderVariable input : fragment.getInputs()) {
			if (!inputNames.contains(input.getName())) {
				builder.append(input.isFlat() ? "flat " : "").append("in ").append(input.getDeclaration()).append(";\n");
			}
		}

		builder.append("out vec4 fragColor;\n#line 1\n").append(fragment.getBody().replaceFirst("void\\s+main\\s*\\(\\s*\\)", "void joid_main()"));
		return builder.append(ShaderTranslator.STENCIL_FUNCTIONS).append(stencil ? ShaderTranslator.STENCIL_MAIN : ShaderTranslator.COLOR_MAIN).toString();
	}

	private static StringBuilder createHeader(final List<ShaderVariable> uniforms) {
		final StringBuilder builder = new StringBuilder("#version 330\n\nlayout(std140) uniform ").append(ShaderTranslator.BLOCK).append(" {\n");
		for (final ShaderVariable uniform : uniforms) {
			builder.append('\t').append(uniform.getDeclaration()).append(";\n");
		}
		return builder.append("};\n\n");
	}

	private static void appendSamplers(final StringBuilder builder, final ShaderSource source) {
		for (final ShaderVariable sampler : source.getSamplers()) {
			builder.append("uniform ").append(sampler.getDeclaration()).append(";\n");
		}
	}

}