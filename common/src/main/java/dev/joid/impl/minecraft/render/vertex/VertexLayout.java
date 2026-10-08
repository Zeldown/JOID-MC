package dev.joid.impl.minecraft.render.vertex;

import java.util.Collection;

import dev.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import dev.joid.lib.bridge.render.vertex.VertexBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VertexLayout {

	public static @NonNull VertexFormat create(final @NonNull Collection<@NonNull ShaderBuiltin> builtins) {
		final VertexFormat.Builder builder = VertexFormat.builder(0);
		for (final ShaderBuiltin builtin : ShaderBuiltin.values()) {
			if (builtin.getKind() == ShaderBuiltin.Kind.ATTRIBUTE && (builtin == ShaderBuiltin.POSITION || builtins.contains(builtin))) {
				final int offset = VertexLayout.getOffset(builtin);
				builder.addAttribute(builtin.getIdentifier(), offset, VertexBuffer.STRIDE - offset, VertexLayout.getFormat(builtin), 1);
			}
		}
		return builder.build();
	}

	private static int getOffset(final ShaderBuiltin builtin) {
		switch (builtin) {
		case TEXTURE_COORDINATE:
			return VertexBuffer.TEXTURE_OFFSET;
		case COLOR:
			return VertexBuffer.COLOR_OFFSET;
		case NORMAL:
			return VertexBuffer.NORMAL_OFFSET;
		default:
			return VertexBuffer.POSITION_OFFSET;
		}
	}

	private static GpuFormat getFormat(final ShaderBuiltin builtin) {
		switch (builtin) {
		case TEXTURE_COORDINATE:
			return GpuFormat.RG32_FLOAT;
		case COLOR:
			return GpuFormat.RGBA8_UNORM;
		case NORMAL:
			return GpuFormat.RGBA8_SNORM;
		default:
			return GpuFormat.RGB32_FLOAT;
		}
	}

}