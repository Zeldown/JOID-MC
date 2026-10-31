package dev.joid.backend.minecraft.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import dev.joid.backend.minecraft.loader.event.MinecraftScreenEvents;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@WrapWithCondition(method = "onMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V", ordinal = 1))
	private boolean joid$mouseDragged(final Runnable action, final String description, final String screen) {
		return !MinecraftScreenEvents.fireMouseMoved();
	}

}