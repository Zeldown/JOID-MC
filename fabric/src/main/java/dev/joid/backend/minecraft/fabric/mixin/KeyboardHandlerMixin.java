package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	private void joid$charTyped(final long window, final int codepoint, final int modifiers, final CallbackInfo info) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (window == minecraft.getWindow().getWindow() && minecraft.screen != null && minecraft.getOverlay() == null && MinecraftScreenEvents.fireCharTyped(codepoint)) {
			info.cancel();
		}
	}

}