package dev.joid.impl.minecraft.render.shader;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.shaders.ShaderType;

import net.minecraft.resources.Identifier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShaderSourceProvider implements ShaderSource {

	private final Map<ShaderType, Map<Identifier, String>> sourceMap;

	public static @NonNull ShaderSourceProvider create() {
		final Map<ShaderType, Map<Identifier, String>> sourceMap = new EnumMap<>(ShaderType.class);
		for (final ShaderType type : ShaderType.values()) {
			sourceMap.put(type, new HashMap<>());
		}
		return new ShaderSourceProvider(sourceMap);
	}

	public void register(final @NonNull Identifier identifier, final @NonNull ShaderType type, final @NonNull String source) {
		this.sourceMap.get(type).put(identifier, source);
	}

	@Override
	public String get(final Identifier identifier, final ShaderType type) {
		return this.sourceMap.get(type).get(identifier);
	}

}