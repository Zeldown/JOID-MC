package fr.augma.joidmc.render.shader.uniform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.zeldown.joid.lib.bridge.render.shader.source.ShaderVariable;
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

	public static @NonNull UniformBlock create(final @NonNull List<ShaderVariable> variables, final @NonNull String code) {
		final int[] offsets = new int[variables.size()];
		final int[] strides = new int[variables.size()];

		int offset = 0;
		for (int i = 0; i < variables.size(); i++) {
			final ShaderVariable variable = variables.get(i);
			final int length = UniformBlock.getLength(variable.getArray(), code);
			final int columns = UniformBlock.getColumns(variable.getType());
			final int size = columns > 1 ? columns * 16 : UniformBlock.getSize(variable.getType());
			final int alignment = length > 0 || columns > 1 ? 16 : UniformBlock.getAlignment(size);

			offset = UniformBlock.align(offset, alignment);
			offsets[i] = offset;
			strides[i] = length > 0 ? UniformBlock.align(size, 16) : 0;
			offset += length > 0 ? strides[i] * length : size;
		}

		final ByteBuffer data = ByteBuffer.allocateDirect(Math.max(UniformBlock.align(offset, 16), 16)).order(ByteOrder.nativeOrder());
		final Map<String, UniformMember> memberMap = new HashMap<>();
		for (int i = 0; i < variables.size(); i++) {
			memberMap.put(variables.get(i).getName(), new UniformMember(data, offsets[i], strides[i], 16));
		}
		return new UniformBlock(data, memberMap);
	}

	private static int getLength(final String array, final String code) {
		final Matcher matcher = UniformBlock.ARRAY_LENGTH.matcher(array);
		if (!matcher.matches()) {
			return 0;
		}

		final String length = matcher.group(1);
		if (length.chars().allMatch(Character::isDigit)) {
			return Integer.parseInt(length);
		}

		final Matcher define = Pattern.compile("#define\\s+" + length + "\\s+(\\d+)").matcher(code);
		if (!define.find()) {
			throw new IllegalArgumentException("Unable to resolve the uniform array length " + length);
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
			return 1;
		}
	}

	private static int getSize(final String type) {
		if (type.endsWith("2")) {
			return 8;
		}

		if (type.endsWith("3")) {
			return 12;
		}

		return type.endsWith("4") ? 16 : 4;
	}

	private static int getAlignment(final int size) {
		return size > 8 ? 16 : size;
	}

	private static int align(final int value, final int alignment) {
		return (value + alignment - 1) / alignment * alignment;
	}

}