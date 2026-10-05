package dev.joid.impl.joidmc.lib.bridge.render.shader.uniform;

public final class FloatUniform extends ShaderUniform implements be.zeldown.joid.lib.bridge.render.shader.uniform.FloatUniform {

	public FloatUniform(final UniformMember member) {
		super(member);
	}

	@Override
	public void setValue(final float value) {
		if (super.getMember() != null) {
			super.getMember().putFloats(value);
		}
	}

}