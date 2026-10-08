package dev.joid.impl.minecraft.render.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.joid.impl.minecraft.JoidMinecraft;
import dev.joid.impl.minecraft.render.shader.Shader;
import dev.joid.impl.minecraft.render.shader.ShaderSourceProvider;
import dev.joid.impl.minecraft.render.state.PipelineKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendEquation;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.BlendOp;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.Identifier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipelineCache {

	private static final VertexFormat VERTEX_FORMAT = VertexFormat.builder(0)
			.addAttribute("aPosition", GpuFormat.RGB32_FLOAT)
			.addAttribute("aTexCoord", GpuFormat.RG32_FLOAT)
			.addAttribute("aColor", GpuFormat.RGBA8_UNORM)
			.addAttribute("aNormal", 8, GpuFormat.RGBA8_SNORM)
			.build();

	private final GpuDevice                        device;
	private final ShaderSourceProvider             sourceProvider;
	private final Map<PipelineKey, RenderPipeline> pipelineMap;

	public static @NonNull PipelineCache create(final @NonNull GpuDevice device, final @NonNull ShaderSourceProvider sourceProvider) {
		return new PipelineCache(device, sourceProvider, new HashMap<>());
	}

	public RenderPipeline get(final @NonNull PipelineKey key) {
		RenderPipeline pipeline = this.pipelineMap.get(key);
		if (pipeline == null) {
			pipeline = PipelineCache.build(key, this.pipelineMap.size());
			this.pipelineMap.put(key, pipeline);
		}

		return this.device.precompilePipeline(pipeline, this.sourceProvider).isValid() ? pipeline : null;
	}

	private static RenderPipeline build(final PipelineKey key, final int index) {
		final Shader shader = (Shader) key.getShader();
		return RenderPipeline.builder()
				.withLocation(Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, "pipeline/" + index))
				.withVertexShader(shader.getIdentifier())
				.withFragmentShader(key.isStencil() ? shader.getStencilIdentifier() : shader.getIdentifier())
				.withBindGroupLayout(shader.getLayout())
				.withVertexBinding(0, PipelineCache.VERTEX_FORMAT)
				.withPrimitiveTopology(key.isLines() ? PrimitiveTopology.DEBUG_LINES : PrimitiveTopology.TRIANGLES)
				.withColorTargetState(new ColorTargetState(PipelineCache.getBlendFunction(key), key.isStencil() ? GpuFormat.R8_UNORM : GpuFormat.RGBA8_UNORM, key.isColorMask() ? ColorTargetState.WRITE_ALL : ColorTargetState.WRITE_NONE))
				.withDepthStencilState(PipelineCache.getDepthStencilState(key))
				.withCull(key.isCull())
				.build();
	}

	private static Optional<DepthStencilState> getDepthStencilState(final PipelineKey key) {
		if (!key.isDepthTest()) {
			return Optional.empty();
		}

		return Optional.of(new DepthStencilState(CompareOp.LESS_THAN, key.isDepthWrite()));
	}

	private static Optional<BlendFunction> getBlendFunction(final PipelineKey key) {
		if (!key.isBlend()) {
			return Optional.empty();
		}

		final BlendOp operation = BlendOp.valueOf(key.getEquation().name());
		return Optional.of(new BlendFunction(new BlendEquation(BlendFactor.valueOf(key.getSourceColor().name()), BlendFactor.valueOf(key.getDestinationColor().name()), operation), new BlendEquation(BlendFactor.valueOf(key.getSourceAlpha().name()), BlendFactor.valueOf(key.getDestinationAlpha().name()), operation)));
	}

}