package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeScreenEvents {

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::render);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::charTyped);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::keyPressed);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::mouseMoved);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::keyReleased);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::mousePressed);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::mouseReleased);
		MinecraftForge.EVENT_BUS.addListener(ForgeScreenEvents::mouseScrolled);
	}

	private static void render(final ScreenEvent.DrawScreenEvent.Post event) {
		MinecraftScreenEvents.fireRender(event.getPoseStack());
	}

	private static void mouseMoved(final ScreenEvent.MouseDragEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseMoved()) {
			event.setCanceled(true);
		}
	}

	private static void mousePressed(final ScreenEvent.MouseClickedEvent.Pre event) {
		if (MinecraftScreenEvents.fireMousePressed(event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void mouseReleased(final ScreenEvent.MouseReleasedEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseReleased(event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void mouseScrolled(final ScreenEvent.MouseScrollEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseScrolled(0D, event.getScrollDelta())) {
			event.setCanceled(true);
		}
	}

	private static void charTyped(final ScreenEvent.KeyboardCharTypedEvent.Pre event) {
		if (MinecraftScreenEvents.fireCharTyped(event.getCodePoint())) {
			event.setCanceled(true);
		}
	}

	private static void keyPressed(final ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		if (MinecraftScreenEvents.fireKeyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

	private static void keyReleased(final ScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
		if (MinecraftScreenEvents.fireKeyReleased(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

}