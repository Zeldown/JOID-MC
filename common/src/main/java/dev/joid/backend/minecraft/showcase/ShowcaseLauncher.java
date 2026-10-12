package dev.joid.backend.minecraft.showcase;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.showcase.ui.ShowChest;
import dev.joid.internal.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowcaseLauncher {

	public static final MenuType<ChestMenu> CHEST = new MenuType<>((containerId, inventory) -> new ChestMenu(ShowcaseLauncher.CHEST, containerId, inventory, new SimpleContainer(27), 3), FeatureFlags.VANILLA_SET);

	public static void init() {
		ContainerUIRegistry.create(ShowcaseLauncher.CHEST, ShowChest::new).register();
		ContainerTypeRegistry.create(MinecraftJoid.MOD_ID + ":showcase/chest", ShowcaseLauncher.CHEST).register();
		JOID.onReady(ShowcaseDirector::start);
	}

	public static boolean isActive() {
		return System.getProperty("joid.showcase") != null;
	}

}