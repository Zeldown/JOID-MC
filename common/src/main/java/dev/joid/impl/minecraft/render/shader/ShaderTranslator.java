package dev.joid.impl.minecraft.render.shader;

import java.util.List;

import dev.joid.lib.bridge.render.shader.source.BlockShaderTranslator;
import dev.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import dev.joid.lib.bridge.render.shader.source.ShaderSource;
import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShaderTranslator extends BlockShaderTranslator {

	public static final String STENCIL           = "joid_Stencil";
	public static final String STENCIL_TEST      = "joid_StencilTest";
	public static final String STENCIL_FUNCTION  = "joid_StencilFunction";
	public static final String STENCIL_REFERENCE = "joid_StencilReference";
	public static final String STENCIL_MASK      = "joid_StencilMask";
	public static final String STENCIL_FAIL      = "joid_StencilFail";
	public static final String STENCIL_PASS      = "joid_StencilPass";

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

	private final boolean stencil;

	public static @NonNull ShaderTranslator create() {
		return new ShaderTranslator(false);
	}

	public static @NonNull ShaderTranslator createStencil() {
		return new ShaderTranslator(true);
	}

	@Override
	protected @NonNull String getVersion() {
		return "#version 330";
	}

	@Override
	protected @NonNull String getMain() {
		return "\nuniform sampler2D " + ShaderTranslator.STENCIL + ";\n" + ShaderTranslator.STENCIL_FUNCTIONS + (this.stencil ? ShaderTranslator.STENCIL_MAIN : ShaderTranslator.COLOR_MAIN);
	}

	@Override
	protected @NonNull String declareAttribute(final @NonNull ShaderBuiltin builtin, final int location) {
		return "in " + builtin.getType() + " " + builtin.getIdentifier() + ";\n";
	}

	@Override
	protected @NonNull List<@NonNull ShaderVariable> getInternals(final @NonNull ShaderSource vertex, final @NonNull ShaderSource fragment) {
		final List<ShaderVariable> internalList = super.getInternals(vertex, fragment);
		for (final String name : new String[] {ShaderTranslator.STENCIL_TEST, ShaderTranslator.STENCIL_FUNCTION, ShaderTranslator.STENCIL_REFERENCE, ShaderTranslator.STENCIL_MASK, ShaderTranslator.STENCIL_FAIL, ShaderTranslator.STENCIL_PASS}) {
			internalList.add(ShaderVariable.create("int", name, "", false));
		}
		return internalList;
	}

}