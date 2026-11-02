package dev.joid.backend.minecraft.forge.event;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.GuiScreenEvent;
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

	private static void render(final GuiScreenEvent.DrawScreenEvent.Post event) {
		MinecraftScreenEvents.fireRender(event.getMatrixStack());
	}

	private static void mouseMoved(final GuiScreenEvent.MouseDragEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseMoved()) {
			event.setCanceled(true);
		}
	}

	private static void mousePressed(final GuiScreenEvent.MouseClickedEvent.Pre event) {
		if (MinecraftScreenEvents.fireMousePressed(event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void mouseReleased(final GuiScreenEvent.MouseReleasedEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseReleased(event.getButton())) {
			event.setCanceled(true);
		}
	}

	private static void mouseScrolled(final GuiScreenEvent.MouseScrollEvent.Pre event) {
		if (MinecraftScreenEvents.fireMouseScrolled(0D, event.getScrollDelta())) {
			event.setCanceled(true);
		}
	}

	private static void charTyped(final GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
		if (MinecraftScreenEvents.fireCharTyped(event.getCodePoint())) {
			event.setCanceled(true);
		}
	}

	private static void keyPressed(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		if (MinecraftScreenEvents.fireKeyPressed(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

	private static void keyReleased(final GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
		if (MinecraftScreenEvents.fireKeyReleased(event.getKeyCode())) {
			event.setCanceled(true);
		}
	}

}