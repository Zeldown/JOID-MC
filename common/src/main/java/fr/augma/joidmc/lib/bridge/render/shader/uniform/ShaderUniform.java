package fr.augma.joidmc.lib.bridge.render.shader.uniform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ShaderUniform implements be.zeldown.joid.lib.bridge.render.shader.uniform.ShaderUniform {

	private final UniformMember member;

}