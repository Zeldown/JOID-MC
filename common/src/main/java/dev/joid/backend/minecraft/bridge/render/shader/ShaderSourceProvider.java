package dev.joid.backend.minecraft.bridge.render.shader;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.shaders.ShaderType;

import lombok.NonNull;
import net.minecraft.resources.Identifier;

public final class ShaderSourceProvider implements ShaderSource {

	private final Map<ShaderType, Map<Identifier, String>> sourceMap;

	public ShaderSourceProvider() {
		this.sourceMap = new EnumMap<>(ShaderType.class);
		for (final ShaderType type : ShaderType.values()) {
			this.sourceMap.put(type, new HashMap<>());
		}
	}

	public void register(final @NonNull Identifier identifier, final @NonNull ShaderType type, final @NonNull String source) {
		this.sourceMap.get(type).put(identifier, source);
	}

	public void unregister(final @NonNull Identifier identifier) {
		for (final Map<Identifier, String> sources : this.sourceMap.values()) {
			sources.remove(identifier);
		}
	}

	@Override
	public String get(final Identifier identifier, final ShaderType type) {
		return this.sourceMap.get(type).get(identifier);
	}

}