package dev.joid.backend.minecraft.showcase;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.showcase.ui.ShowAnvil;
import dev.joid.backend.minecraft.showcase.ui.ShowChest;
import dev.joid.lib.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.math.BlockPos;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseLauncher {

	public static final InventoryBasic STORAGE = new InventoryBasic("container.chest", false, 27);

	public static final ContainerTypeRegistry<ContainerChest>  CHEST = ContainerTypeRegistry.create(MinecraftJoid.MOD_ID + ":showcase/chest", player -> new ContainerChest(player.inventory, player.world.isRemote ? new InventoryBasic("container.chest", false, 27) : ShowcaseLauncher.STORAGE, player));
	public static final ContainerTypeRegistry<ContainerRepair> ANVIL = ContainerTypeRegistry.create(MinecraftJoid.MOD_ID + ":showcase/anvil", AnvilContainer::new);

	public static void init() {
		ShowcaseLauncher.CHEST.register();
		ShowcaseLauncher.ANVIL.register();
		ContainerUIRegistry.create(ShowcaseLauncher.CHEST, ShowChest::new).register();
		ContainerUIRegistry.create(ShowcaseLauncher.ANVIL, ShowAnvil::new).register();
		JOID.onReady(ShowcaseDirector::start);
	}

	public static boolean isActive() {
		return System.getProperty("joid.showcase") != null;
	}

	private static final class AnvilContainer extends ContainerRepair {

		private AnvilContainer(final EntityPlayer player) {
			super(player.inventory, player.world, BlockPos.ORIGIN, player);
		}

		@Override
		public boolean canInteractWith(final EntityPlayer player) {
			return true;
		}

	}

}