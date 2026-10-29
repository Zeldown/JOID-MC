package dev.joid.backend.minecraft.bridge.input;

import org.lwjgl.input.Keyboard;

import dev.joid.base.lwjgl2.input.Lwjgl2Keys;
import dev.joid.lib.input.key.Key;
import dev.joid.lib.input.key.resolver.IKeyResolver;
import lombok.NonNull;
import net.minecraft.client.settings.KeyBinding;

public final class KeyBindKeyResolver implements IKeyResolver {

	@Override
	public boolean supports(final @NonNull Object binding) {
		return binding instanceof KeyBinding;
	}

	@Override
	public Key resolve(final @NonNull Object binding) {
		final int code = ((KeyBinding) binding).getKeyCode();
		return code > Keyboard.KEY_NONE && code < Keyboard.KEYBOARD_SIZE ? Lwjgl2Keys.getPhysicalKey(code) : null;
	}

}