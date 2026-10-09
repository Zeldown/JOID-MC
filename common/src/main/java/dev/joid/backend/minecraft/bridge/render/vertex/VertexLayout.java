package dev.joid.backend.minecraft.bridge.render.vertex;

import java.util.Collection;

import dev.joid.lib.bridge.render.shader.source.ShaderBuiltin;
import dev.joid.lib.bridge.render.vertex.VertexAttribute;
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
		for (final VertexAttribute attribute : VertexAttribute.values()) {
			if (attribute == VertexAttribute.POSITION || builtins.contains(attribute.getBuiltin())) {
				builder.addAttribute(attribute.getBuiltin().getIdentifier(), attribute.getOffset(), VertexBuffer.STRIDE - attribute.getOffset(), VertexLayout.getFormat(attribute), 1);
			}
		}
		return builder.build();
	}

	private static GpuFormat getFormat(final VertexAttribute attribute) {
		switch (attribute) {
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