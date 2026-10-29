package dev.joid.backend.minecraft.bridge.render;

import java.lang.reflect.Field;
import java.util.BitSet;

import dev.joid.internal.JOIDLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftStencil {

	public static void enable() {
		if (MinecraftForgeClient.getStencilBits() > 0) {
			return;
		}

		if (!OpenGlHelper.isFramebufferEnabled()) {
			JOIDLog.warn("The framebuffers of Minecraft are off, so the screen has no stencil and the masks of JOID do not clip: turn the framebuffers on in the video settings");
			return;
		}

		try {
			final Field bits = ForgeHooksClient.class.getDeclaredField("stencilBits");
			bits.setAccessible(true);
			bits.setInt(null, 8);

			final Field reserved = MinecraftForgeClient.class.getDeclaredField("stencilBits");
			reserved.setAccessible(true);
			((BitSet) reserved.get(null)).set(0, 8);
		} catch (final ReflectiveOperationException exception) {
			JOIDLog.warn("Forge does not let JOID turn the stencil of the screen on, so the masks of JOID do not clip: " + exception);
			return;
		}

		final Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
	}

}