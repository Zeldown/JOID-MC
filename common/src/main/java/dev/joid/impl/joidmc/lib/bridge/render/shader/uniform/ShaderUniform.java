package dev.joid.impl.joidmc.lib.bridge.render.shader.uniform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ShaderUniform implements dev.joid.lib.bridge.render.shader.uniform.ShaderUniform {

	private final UniformMember member;

}