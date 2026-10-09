package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraftforge.client.event.ScreenEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeScreenEvents {

	public static void register() {
		ScreenEvent.KeyPressed.Pre.BUS.addListener(ForgeScreenEvents::keyPressed);
		ScreenEvent.MouseDragged.Pre.BUS.addListener(ForgeScreenEvents::mouseMoved);
		ScreenEvent.CharacterTyped.Pre.BUS.addListener(ForgeScreenEvents::charTyped);
		ScreenEvent.MouseScrolled.Pre.BUS.addListener(ForgeScreenEvents::mouseScrolled);
		ScreenEvent.MouseButtonPressed.Pre.BUS.addListener(ForgeScreenEvents::mousePressed);
		ScreenEvent.MouseButtonReleased.Pre.BUS.addListener(ForgeScreenEvents::mouseReleased);
		ScreenEvent.Render.Post.BUS.addListener(event -> MinecraftScreenEvents.fireRender(event.getGuiGraphics()));
	}

	private static boolean keyPressed(final ScreenEvent.KeyPressed.Pre event) {
		return MinecraftScreenEvents.fireKeyPressed(event.getInfo().key());
	}

	private static boolean charTyped(final ScreenEvent.CharacterTyped.Pre event) {
		return MinecraftScreenEvents.fireCharTyped(event.getInfo().codepoint());
	}

	private static boolean mouseMoved(final ScreenEvent.MouseDragged.Pre event) {
		return MinecraftScreenEvents.fireMouseMoved();
	}

	private static boolean mousePressed(final ScreenEvent.MouseButtonPressed.Pre event) {
		return MinecraftScreenEvents.fireMousePressed(event.getInfo().button());
	}

	private static boolean mouseReleased(final ScreenEvent.MouseButtonReleased.Pre event) {
		return MinecraftScreenEvents.fireMouseReleased(event.getButton());
	}

	private static boolean mouseScrolled(final ScreenEvent.MouseScrolled.Pre event) {
		return MinecraftScreenEvents.fireMouseScrolled(event.getDeltaX(), event.getDeltaY());
	}

}