package fr.augma.joidblaze3d.screen.data.overlay.render;

import net.minecraft.client.Minecraft;

public enum ElementType {

	ALL,
	HELMET,
	PORTAL,
	CROSSHAIRS,
	BOSSHEALTH,
	ARMOR,
	HEALTH,
	FOOD,
	AIR,
	HOTBAR,
	EXPERIENCE,
	TEXT,
	HEALTHMOUNT,
	JUMPBAR,
	CHAT,
	PLAYER_LIST,
	EFFECTS;

	public boolean isActive() {
		if (this != ElementType.EXPERIENCE && this != ElementType.JUMPBAR) {
			return true;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final boolean jumping = minecraft.player != null && minecraft.player.jumpableVehicle() != null;
		return this == ElementType.JUMPBAR == jumping;
	}

}