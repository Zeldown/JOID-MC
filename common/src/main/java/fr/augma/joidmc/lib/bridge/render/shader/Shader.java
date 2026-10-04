package fr.augma.joidmc.lib.bridge.render.shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.zeldown.joid.lib.bridge.render.shader.IShader;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderSource;
import be.zeldown.joid.lib.bridge.render.shader.source.ShaderVariable;
import be.zeldown.joid.lib.bridge.render.state.BlendState;
import fr.augma.joidmc.Constants;
import fr.augma.joidmc.lib.bridge.render.RenderBridge;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.BooleanUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.Float2Uniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.Float3Uniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.Float4ArrayUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.Float4Uniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.FloatArrayUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.FloatMatrixUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.FloatUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.IntUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.SamplerUniform;
import fr.augma.joidmc.lib.bridge.render.shader.uniform.UniformBlock;
import fr.augma.joidmc.lib.render.pipeline.PipelineKey;
import lombok.Getter;
import lombok.NonNull;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.shaders.UniformType;

import net.minecraft.resources.Identifier;

@Getter
public final class Shader implements IShader {

	private static int counter;

	private final RenderBridge                bridge;
	private final BlendState                  blend;
	private final Identifier                  identifier;
	private final Identifier                  stencilIdentifier;
	private final UniformBlock                block;
	private final List<String>                samplerList;
	private final BindGroupLayout             layout;
	private final Map<String, SamplerUniform> samplerMap;

	private boolean    active;
	private boolean    bound;
	private BlendState previousBlend;

	private Shader(final RenderBridge bridge, final BlendState blend, final String path, final UniformBlock block, final List<String> samplerList) {
		BindGroupLayout.Builder layout = BindGroupLayout.builder().withUniform(ShaderTranslator.BLOCK, UniformType.UNIFORM_BUFFER).withSampler(ShaderTranslator.STENCIL);
		for (final String sampler : samplerList) {
			layout = layout.withSampler(sampler);
		}

		this.bridge            = bridge;
		this.blend             = blend;
		this.identifier        = Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
		this.stencilIdentifier = Identifier.fromNamespaceAndPath(Constants.MOD_ID, path + "_stencil");
		this.block             = block;
		this.samplerList       = samplerList;
		this.layout            = layout.build();
		this.samplerMap        = new HashMap<>();
	}

	public static @NonNull Shader create(final RenderBridge bridge, final ShaderSource vertex, final ShaderSource fragment, final BlendState blend) {
		final List<ShaderVariable> uniformList = ShaderTranslator.getUniforms(vertex, fragment);
		final Shader shader = new Shader(bridge, blend, "joid/shader_" + Shader.counter++, UniformBlock.create(uniformList, vertex.getBody() + fragment.getBody()), ShaderTranslator.getSamplers(vertex, fragment));
		bridge.registerSource(shader.identifier, ShaderType.VERTEX, ShaderTranslator.translateVertex(vertex, uniformList));
		bridge.registerSource(shader.identifier, ShaderType.FRAGMENT, ShaderTranslator.translateFragment(vertex, fragment, uniformList, false));
		bridge.registerSource(shader.stencilIdentifier, ShaderType.FRAGMENT, ShaderTranslator.translateFragment(vertex, fragment, uniformList, true));
		shader.active = bridge.isValid(PipelineKey.create(shader, blend, false, PrimitiveTopology.TRIANGLES, false));
		return shader;
	}

	@Override
	public void bind() {
		this.previousBlend = this.bridge.getState().getBlend();
		this.bridge.shader(this);
		this.bridge.blend(this.blend);
		this.bound = true;
	}

	@Override
	public void unbind() {
		this.bridge.shader(null);
		if (this.previousBlend != null) {
			this.bridge.blend(this.previousBlend);
			this.previousBlend = null;
		}

		this.bound = false;
	}

	@Override
	public @NonNull SamplerUniform getSamplerUniform(final @NonNull String name) {
		return this.samplerMap.computeIfAbsent(name, key -> new SamplerUniform());
	}

	@Override
	public @NonNull BooleanUniform getBooleanUniform(final @NonNull String name) {
		return new BooleanUniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull IntUniform getIntUniform(final @NonNull String name) {
		return new IntUniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull FloatArrayUniform getFloatArrayUniform(final @NonNull String name) {
		return new FloatArrayUniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull FloatUniform getFloatUniform(final @NonNull String name) {
		return new FloatUniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull Float2Uniform getFloat2Uniform(final @NonNull String name) {
		return new Float2Uniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull Float3Uniform getFloat3Uniform(final @NonNull String name) {
		return new Float3Uniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull Float4Uniform getFloat4Uniform(final @NonNull String name) {
		return new Float4Uniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull FloatMatrixUniform getFloatMatrixUniform(final @NonNull String name) {
		return new FloatMatrixUniform(this.block.getMemberMap().get(name));
	}

	@Override
	public @NonNull Float4ArrayUniform getFloat4ArrayUniform(final @NonNull String name) {
		return new Float4ArrayUniform(this.block.getMemberMap().get(name));
	}

}