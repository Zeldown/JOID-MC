package fr.augma.joidblaze3d.test;

import fr.augma.joidblaze3d.screen.JOIDMenuScreen;
import lombok.NonNull;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;

public final class TestMenuScreen extends JOIDMenuScreen<InventoryMenu> {

	protected TestMenuScreen(final LocalPlayer player) {
		super(player.inventoryMenu, player.getInventory(), Component.literal("JOID"), 176, 166, new TestMenuUI());
	}

	public static @NonNull TestMenuScreen create(final @NonNull LocalPlayer player) {
		return new TestMenuScreen(player);
	}

}