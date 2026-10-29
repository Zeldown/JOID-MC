package dev.joid.backend.minecraft;

import java.io.File;

import dev.joid.backend.minecraft.bridge.render.MinecraftStencil;
import dev.joid.base.lwjgl2.opengl.Lwjgl2GlBinding;
import dev.joid.base.opengl.capability.GlCapabilities;
import dev.joid.internal.JOIDLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftJoid {

	public static final String MOD_ID = "joid";

	private static boolean unsupported;

	public static void register() {
		if (MinecraftJoid.unsupported || MinecraftBackend.inst().isRegistered()) {
			return;
		}

		try {
			GlCapabilities.read(Lwjgl2GlBinding.inst()).require();
		} catch (final IllegalStateException exception) {
			MinecraftJoid.unsupported = true;
			JOIDLog.warn("JOID-MC stays off and opens no UI: " + exception.getMessage());
			return;
		}

		MinecraftBackend.inst().dataDir(new File(Minecraft.getMinecraft().mcDataDir, "config/joid")).register();
		MinecraftStencil.enable();
	}

}