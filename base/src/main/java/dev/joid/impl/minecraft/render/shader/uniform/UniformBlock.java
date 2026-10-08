package dev.joid.impl.minecraft.render.shader.uniform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.joid.lib.bridge.render.shader.source.ShaderVariable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniformBlock {

	private static final Pattern ARRAY_LENGTH = Pattern.compile("\\[\\s*(\\w+)\\s*\\]");

	private final ByteBuffer                 data;
	private final Map<String, UniformMember> memberMap;

	public static @NonNull UniformBlock create(final @NonNull List<@NonNull ShaderVariable> variables, final @NonNull String code) {
		int offset = 0;
		final int[][] layouts = new int[variables.size()][];
		for (int i = 0; i < variables.size(); i++) {
			final ShaderVariable variable = variables.get(i);
			final int length = UniformBlock.getLength(variable, code);
			final int columns = UniformBlock.getColumns(variable.getType());
			final int size = UniformBlock.getSize(variable.getType());
			final int alignment = length > 0 || columns > 0 ? 16 : UniformBlock.getAlignment(size);
			final int stride = length > 0 ? UniformBlock.align(size, 16) : 0;

			offset = UniformBlock.align(offset, alignment);
			layouts[i] = new int[] {offset, length > 0 ? stride * length : size, stride, columns > 0 ? 16 : 0};
			offset += layouts[i][1];
		}

		final ByteBuffer data = ByteBuffer.allocateDirect(Math.max(UniformBlock.align(offset, 16), 16)).order(ByteOrder.nativeOrder());
		final Map<String, UniformMember> memberMap = new LinkedHashMap<>();
		for (int i = 0; i < variables.size(); i++) {
			final ShaderVariable variable = variables.get(i);
			memberMap.put(variable.getName(), new UniformMember(data, variable.getName(), variable.getType(), layouts[i][0], layouts[i][1], layouts[i][2], layouts[i][3]));
		}
		return new UniformBlock(data, memberMap);
	}

	public UniformMember getMember(final @NonNull String name) {
		return this.memberMap.get(name);
	}

	public int getSize() {
		return this.data.capacity();
	}

	private static int getLength(final ShaderVariable variable, final String code) {
		final Matcher matcher = UniformBlock.ARRAY_LENGTH.matcher(variable.getArray());
		if (!matcher.matches()) {
			return 0;
		}

		final String length = matcher.group(1);
		if (length.chars().allMatch(Character::isDigit)) {
			return Integer.parseInt(length);
		}

		final Matcher define = Pattern.compile("#define\\s+" + length + "\\s+(\\d+)").matcher(code);
		if (!define.find()) {
			throw new IllegalArgumentException("Unable to resolve the length " + length + " of the uniform array " + variable.getName());
		}
		return Integer.parseInt(define.group(1));
	}

	private static int getColumns(final String type) {
		switch (type) {
		case "mat2":
			return 2;
		case "mat3":
			return 3;
		case "mat4":
			return 4;
		default:
			return 0;
		}
	}

	private static int getSize(final String type) {
		switch (type) {
		case "int":
		case "uint":
		case "bool":
		case "float":
			return 4;
		case "vec2":
		case "ivec2":
		case "uvec2":
		case "bvec2":
			return 8;
		case "vec3":
		case "ivec3":
		case "uvec3":
		case "bvec3":
			return 12;
		case "vec4":
		case "ivec4":
		case "uvec4":
		case "bvec4":
			return 16;
		case "mat2":
			return 32;
		case "mat3":
			return 48;
		case "mat4":
			return 64;
		default:
			throw new IllegalArgumentException("Unsupported uniform type " + type);
		}
	}

	private static int getAlignment(final int size) {
		return size > 8 ? 16 : size;
	}

	private static int align(final int value, final int alignment) {
		return (value + alignment - 1) / alignment * alignment;
	}

}