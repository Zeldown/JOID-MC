package dev.joid.impl.minecraft.render.state;

import dev.joid.lib.bridge.render.shader.IShader;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.RenderState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipelineKey {

	private final IShader             shader;
	private final boolean             blend;
	private final BlendState.Equation equation;
	private final BlendState.Factor   sourceColor;
	private final BlendState.Factor   destinationColor;
	private final BlendState.Factor   sourceAlpha;
	private final BlendState.Factor   destinationAlpha;
	private final boolean             colorMask;
	private final boolean             depthTest;
	private final boolean             depthWrite;
	private final boolean             cull;
	private final boolean             lines;
	private final boolean             stencil;

	public static @NonNull PipelineKey create(final @NonNull IShader shader, final @NonNull RenderState state, final boolean lines) {
		return PipelineKey.create(shader, state.getBlend(), state.isColorMask(), state.isDepthTest(), state.isDepthWrite(), state.isCull(), lines, false);
	}

	public static @NonNull PipelineKey stencil(final @NonNull IShader shader, final @NonNull RenderState state, final boolean lines) {
		return PipelineKey.create(shader, BlendState.DISABLED, true, false, false, state.isCull(), lines, true);
	}

	private static PipelineKey create(final IShader shader, final BlendState source, final boolean colorMask, final boolean depthTest, final boolean depthWrite, final boolean cull, final boolean lines, final boolean stencil) {
		final BlendState blend = source.isEnabled() ? source : BlendState.DISABLED;
		return new PipelineKey(shader, blend.isEnabled(), blend.getEquation(), blend.getSourceColor(), blend.getDestinationColor(), blend.getSourceAlpha(), blend.getDestinationAlpha(), colorMask, depthTest, depthTest && depthWrite, cull, lines, stencil);
	}

}