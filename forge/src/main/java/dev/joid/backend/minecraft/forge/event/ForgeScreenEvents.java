package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.MinecraftBackend;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayInputForwarder;
import dev.joid.backend.minecraft.bridge.ui.overlay.OverlayLayerRenderer;
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
		ScreenEvent.Render.Post.BUS.addListener(event -> OverlayLayerRenderer.extractScreen(event.getGuiGraphics()));
		ScreenEvent.Init.Post.BUS.addListener(event -> MinecraftBackend.initScreen(event.getScreen(), event::addListener));
	}

	private static boolean keyPressed(final ScreenEvent.KeyPressed.Pre event) {
		return OverlayInputForwarder.keyPressed(event.getInfo().key());
	}

	private static boolean charTyped(final ScreenEvent.CharacterTyped.Pre event) {
		return OverlayInputForwarder.charTyped(event.getInfo().codepoint());
	}

	private static boolean mouseMoved(final ScreenEvent.MouseDragged.Pre event) {
		return OverlayInputForwarder.mouseMoved();
	}

	private static boolean mousePressed(final ScreenEvent.MouseButtonPressed.Pre event) {
		return OverlayInputForwarder.mousePressed(event.getInfo().button());
	}

	private static boolean mouseReleased(final ScreenEvent.MouseButtonReleased.Pre event) {
		return OverlayInputForwarder.mouseReleased(event.getButton());
	}

	private static boolean mouseScrolled(final ScreenEvent.MouseScrolled.Pre event) {
		return OverlayInputForwarder.mouseScrolled(event.getDeltaY());
	}

}