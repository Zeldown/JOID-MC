package fr.augma.joidmc.lib.draw.entity;

import be.zeldown.joid.lib.bridge.BridgeHandler;
import fr.augma.joidmc.lib.bridge.render.RenderBridge;
import lombok.NonNull;

import net.minecraft.world.entity.Entity;

public final class MCDrawEntity {

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double size, final double scale) {
		this.drawEntity(entity, x, y, size, size, scale, 0F, 0F, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale) {
		this.drawEntity(entity, x, y, width, height, scale, 0F, 0F, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale, final float yaw, final float pitch) {
		this.drawEntity(entity, x, y, width, height, scale, yaw, pitch, 0F, 0F);
	}

	public void drawEntity(final @NonNull Entity entity, final double x, final double y, final double width, final double height, final double scale, final float yaw, final float pitch, final float rotationYaw, final float rotationPitch) {
		((RenderBridge) BridgeHandler.RENDER.get()).entity(entity, x, y, width, height, scale, yaw, pitch, rotationYaw, rotationPitch);
	}

}