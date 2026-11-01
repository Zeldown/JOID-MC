package dev.joid.backend.minecraft.bridge.render;

import dev.joid.internal.JOIDLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftStencil {

	public static void enable() {
		final Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
		if (!framebuffer.isStencilEnabled() && !framebuffer.enableStencil()) {
			JOIDLog.warn("The framebuffers of Minecraft are off, so the screen has no stencil and the masks of JOID do not clip: turn the framebuffers on in the video settings");
		}
	}

}