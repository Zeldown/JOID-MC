package dev.joid.impl.joidmc.lib.render.pipeline;

import be.zeldown.joid.lib.bridge.render.state.BlendState;
import dev.joid.impl.joidmc.lib.bridge.render.shader.Shader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.PrimitiveTopology;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipelineKey {

	private final Shader              shader;
	private final boolean             blend;
	private final BlendState.Equation equation;
	private final BlendState.Factor   sourceColor;
	private final BlendState.Factor   destinationColor;
	private final BlendState.Factor   sourceAlpha;
	private final BlendState.Factor   destinationAlpha;
	private final boolean             cull;
	private final PrimitiveTopology   topology;
	private final boolean             stencil;

	public static @NonNull PipelineKey create(final @NonNull Shader shader, final @NonNull BlendState blend, final boolean cull, final @NonNull PrimitiveTopology topology, final boolean stencil) {
		return new PipelineKey(shader, blend.isEnabled(), blend.getEquation(), blend.getSourceColor(), blend.getDestinationColor(), blend.getSourceAlpha(), blend.getDestinationAlpha(), cull, topology, stencil);
	}

}