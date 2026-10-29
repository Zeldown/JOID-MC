package dev.joid.backend.minecraft.forge.registry;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeContainerTypeRegistry implements IGuiHandler {

	public static void register(final Object mod) {
		if (ContainerTypeRegistry.getRegistered().isEmpty()) {
			return;
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(mod, new ForgeContainerTypeRegistry());
		ContainerTypeRegistry.opener((type, player) -> player.openGui(mod, ContainerTypeRegistry.getRegistered().indexOf(type), player.worldObj, 0, 0, 0));
	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		return ContainerTypeRegistry.getRegistered().get(id).getFactory().apply(player);
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final ContainerTypeRegistry<?> type = ContainerTypeRegistry.getRegistered().get(id);
		for (final ContainerUIRegistry<?> binding : ContainerUIRegistry.getRegistered()) {
			if (binding.getType() == type) {
				return ForgeContainerTypeRegistry.createScreen(binding, player);
			}
		}
		return null;
	}

	private static <C extends Container> Object createScreen(final ContainerUIRegistry<C> binding, final EntityPlayer player) {
		return ContainerUI.screen(binding.getFactory()).apply(binding.getType().getFactory().apply(player));
	}

}