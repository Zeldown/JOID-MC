package dev.joid.backend.minecraft.bridge.render.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.BlendEquation;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.BlendOp;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.bridge.render.shader.MinecraftShader;
import dev.joid.backend.minecraft.bridge.render.shader.ShaderSourceProvider;
import dev.joid.lib.bridge.render.state.BlendState;
import dev.joid.lib.bridge.render.state.PipelineKey;
import dev.joid.lib.bridge.render.vertex.Primitive;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class MinecraftPipelineCache {

	private final GpuDevice                                device;
	private final ShaderSourceProvider                     sourceProvider;
	private final Map<PipelineKey, CompiledRenderPipeline> pipelineMap;

	public MinecraftPipelineCache(final GpuDevice device, final ShaderSourceProvider sourceProvider) {
		this.device         = device;
		this.sourceProvider = sourceProvider;
		this.pipelineMap    = new HashMap<>();
	}

	public CompiledRenderPipeline get(final PipelineKey key) {
		if (!this.pipelineMap.containsKey(key)) {
			this.pipelineMap.put(key, this.compile(MinecraftPipelineCache.build(key, this.pipelineMap.size())));
		}

		return this.pipelineMap.get(key);
	}

	public void remove(final MinecraftShader shader) {
		this.pipelineMap.entrySet().removeIf(entry -> {
			if (entry.getKey().getShader() != shader) {
				return false;
			}

			if (entry.getValue() != null) {
				entry.getValue().close();
			}
			return true;
		});
	}

	public boolean isValid(final Identifier identifier, final BindGroupLayout layout, final VertexFormat vertexFormat, final BlendState blend) {
		final CompiledRenderPipeline pipeline = this.compile(RenderPipeline.builder()
				.withLocation(identifier)
				.withVertexShader(identifier)
				.withFragmentShader(identifier)
				.withBindGroupLayout(layout)
				.withVertexBinding(0, vertexFormat)
				.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
				.withColorTargetState(new ColorTargetState(MinecraftPipelineCache.getBlendFunction(blend), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_ALL))
				.build());
		if (pipeline == null) {
			return false;
		}

		pipeline.close();
		return true;
	}

	private CompiledRenderPipeline compile(final RenderPipeline pipeline) {
		return this.device.compilePipeline(pipeline, this.sourceProvider, Util.backgroundExecutor()).join().finishCompile();
	}

	private static RenderPipeline build(final PipelineKey key, final int index) {
		final MinecraftShader shader = (MinecraftShader) key.getShader();
		return RenderPipeline.builder()
				.withLocation(Identifier.fromNamespaceAndPath(MinecraftJoid.MOD_ID, "pipeline/" + index))
				.withVertexShader(shader.getIdentifier())
				.withFragmentShader(key.isStencil() ? shader.getStencilIdentifier() : shader.getIdentifier())
				.withBindGroupLayout(shader.getLayout())
				.withVertexBinding(0, shader.getVertexFormat())
				.withPrimitiveTopology(key.getPrimitive() == Primitive.LINES ? PrimitiveTopology.DEBUG_LINES : PrimitiveTopology.TRIANGLES)
				.withColorTargetState(new ColorTargetState(MinecraftPipelineCache.getBlendFunction(key.getBlend()), key.isStencil() ? GpuFormat.R8_UNORM : GpuFormat.RGBA8_UNORM, key.isColorWrite() ? ColorTargetState.WRITE_ALL : ColorTargetState.WRITE_NONE))
				.withDepthStencilState(MinecraftPipelineCache.getDepthStencilState(key))
				.withCull(key.isCull())
				.build();
	}

	private static Optional<BlendFunction> getBlendFunction(final BlendState blend) {
		if (!blend.isEnabled()) {
			return Optional.empty();
		}

		final BlendOp operation = BlendOp.valueOf(blend.getEquation().name());
		return Optional.of(new BlendFunction(new BlendEquation(BlendFactor.valueOf(blend.getSourceColor().name()), BlendFactor.valueOf(blend.getDestinationColor().name()), operation), new BlendEquation(BlendFactor.valueOf(blend.getSourceAlpha().name()), BlendFactor.valueOf(blend.getDestinationAlpha().name()), operation)));
	}

	private static Optional<DepthStencilState> getDepthStencilState(final PipelineKey key) {
		if (!key.isDepthTest()) {
			return Optional.empty();
		}

		return Optional.of(new DepthStencilState(CompareOp.LESS_THAN, key.isDepthWrite()));
	}

}