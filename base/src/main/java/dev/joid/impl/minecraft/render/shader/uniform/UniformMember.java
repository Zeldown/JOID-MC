package dev.joid.impl.minecraft.render.shader.uniform;

import java.nio.ByteBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public final class UniformMember {

	private final ByteBuffer data;
	private final String     name;
	private final String     type;
	private final int        offset;
	private final int        size;
	private final int        arrayStride;
	private final int        matrixStride;

	public void putInt(final int value) {
		this.data.putInt(this.offset, value);
	}

	public void putFloats(final @NonNull float... values) {
		if (values.length * 4 > this.size) {
			throw new IllegalArgumentException("The uniform " + this.name + " of type " + this.type + " cannot hold " + values.length + " floats");
		}

		for (int i = 0; i < values.length; i++) {
			this.data.putFloat(this.offset + i * 4, values[i]);
		}
	}

	public void putArray(final @NonNull float[] values, final int components) {
		if (this.arrayStride == 0 || (values.length + components - 1) / components > this.size / this.arrayStride) {
			throw new IllegalArgumentException("The uniform " + this.name + " of type " + this.type + " cannot hold " + values.length + " floats");
		}

		for (int i = 0; i < values.length; i++) {
			this.data.putFloat(this.offset + i / components * this.arrayStride + i % components * 4, values[i]);
		}
	}

	public void putMatrix(final @NonNull float[] values) {
		final int size = (int) Math.round(Math.sqrt(values.length));
		if (size * size != values.length || this.matrixStride == 0 || size * this.matrixStride != this.size) {
			throw new IllegalArgumentException("The uniform " + this.name + " of type " + this.type + " cannot hold a matrix of " + values.length + " floats");
		}

		for (int column = 0; column < size; column++) {
			for (int row = 0; row < size; row++) {
				this.data.putFloat(this.offset + column * this.matrixStride + row * 4, values[column * size + row]);
			}
		}
	}

}