package dev.joid.backend.minecraft.bridge.input;

import org.lwjgl.sdl.SDLMouse;

import dev.joid.lib.input.mouse.MouseButton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SdlMouseButtons {

	public static @NonNull MouseButton getButton(final int button) {
		return switch (button) {
			case SDLMouse.SDL_BUTTON_LEFT -> MouseButton.LEFT;
			case SDLMouse.SDL_BUTTON_RIGHT -> MouseButton.RIGHT;
			case SDLMouse.SDL_BUTTON_MIDDLE -> MouseButton.MIDDLE;
			case SDLMouse.SDL_BUTTON_X1 -> MouseButton.BACK;
			case SDLMouse.SDL_BUTTON_X2 -> MouseButton.FORWARD;
			default -> MouseButton.OTHER;
		};
	}

}