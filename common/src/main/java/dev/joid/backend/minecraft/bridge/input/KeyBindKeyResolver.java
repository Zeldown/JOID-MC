package dev.joid.backend.minecraft.bridge.input;

import com.mojang.blaze3d.platform.InputConstants;

import dev.joid.base.glfw.input.GlfwKeys;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.IKeyResolver;
import lombok.NonNull;
import net.minecraft.client.KeyMapping;

public final class KeyBindKeyResolver implements IKeyResolver {

	@Override
	public boolean supports(final @NonNull Object binding) {
		return binding instanceof KeyMapping;
	}

	@Override
	public Key resolve(final @NonNull Object binding) {
		final KeyMapping mapping = (KeyMapping) binding;
		if (mapping.isUnbound()) {
			return null;
		}

		final InputConstants.Key key = InputConstants.getKey(mapping.saveString());
		return key.getType() == InputConstants.Type.KEYSYM ? GlfwKeys.getKey(key.getValue()) : null;
	}

}