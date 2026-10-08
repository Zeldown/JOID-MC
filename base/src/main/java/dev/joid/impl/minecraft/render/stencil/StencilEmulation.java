package dev.joid.impl.minecraft.render.stencil;

import dev.joid.impl.minecraft.render.shader.ShaderTranslator;
import dev.joid.lib.bridge.render.shader.uniform.UniformBlock;
import dev.joid.lib.bridge.render.state.RenderState;
import dev.joid.lib.bridge.render.state.StencilFunction;
import dev.joid.lib.bridge.render.state.StencilOperation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StencilEmulation {

	private final boolean test;
	private final boolean write;

	private final int             mask;
	private final int             reference;
	private final StencilFunction function;

	private final StencilOperation fail;
	private final StencilOperation pass;

	public static @NonNull StencilEmulation create(final @NonNull RenderState state, final boolean screen) {
		final boolean test = screen && state.isStencilTest();
		final boolean write = test && (state.getStencilFail() != StencilOperation.KEEP || state.getStencilPass() != StencilOperation.KEEP);
		return new StencilEmulation(test, write, state.getStencilMask() & 0xFF, state.getStencilReference() & 0xFF, state.getStencilFunction(), state.getStencilFail(), state.getStencilPass());
	}

	public void write(final @NonNull UniformBlock block) {
		block
		.value(ShaderTranslator.STENCIL_TEST, this.test)
		.value(ShaderTranslator.STENCIL_FUNCTION, this.function.ordinal())
		.value(ShaderTranslator.STENCIL_REFERENCE, this.reference)
		.value(ShaderTranslator.STENCIL_MASK, this.mask)
		.value(ShaderTranslator.STENCIL_FAIL, this.fail.ordinal())
		.value(ShaderTranslator.STENCIL_PASS, this.pass.ordinal());
	}

}