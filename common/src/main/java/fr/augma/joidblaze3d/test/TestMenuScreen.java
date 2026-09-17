package fr.augma.joidblaze3d.test;

import fr.augma.joidblaze3d.screen.JOIDMenuScreen;
import lombok.NonNull;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;

public final class TestMenuScreen extends JOIDMenuScreen<InventoryMenu> {

	private final TestMenuUI menu;

	protected TestMenuScreen(final LocalPlayer player, final TestMenuUI menu) {
		super(player.inventoryMenu, player.getInventory(), Component.literal("JOID"), 176, 166, menu);
		this.menu = menu;
	}

	@Override
	protected void init() {
		super.init();
		super.bounds(this.menu.getPanel());
	}

	public static @NonNull TestMenuScreen create(final @NonNull LocalPlayer player) {
		return new TestMenuScreen(player, new TestMenuUI(player.inventoryMenu));
	}

}