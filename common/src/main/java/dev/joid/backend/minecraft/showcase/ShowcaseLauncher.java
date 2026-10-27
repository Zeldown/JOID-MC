package dev.joid.backend.minecraft.showcase;

import java.lang.reflect.Field;
import java.util.Map;

import dev.joid.backend.minecraft.MinecraftJoid;
import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import dev.joid.backend.minecraft.loader.registry.ContainerTypeRegistry;
import dev.joid.backend.minecraft.loader.registry.ContainerUIRegistry;
import dev.joid.backend.minecraft.showcase.ui.ShowAnvil;
import dev.joid.backend.minecraft.showcase.ui.ShowChest;
import dev.joid.lib.JOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
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
		JOID.onReady(() -> {
			ShowcaseLauncher.replaceAnvil();
			ShowcaseDirector.start();
		});
	}

	public static boolean isActive() {
		return System.getProperty("joid.showcase") != null;
	}

	private static void replaceAnvil() {
		try {
			final Field field = MenuScreens.class.getDeclaredField("SCREENS");
			field.setAccessible(true);
			((Map<MenuType<?>, ScreenConstructor<?, ?>>) field.get(null)).put(MenuType.ANVIL, ContainerUI.screen(ShowAnvil::new));
		} catch (final ReflectiveOperationException exception) {
			throw new AssertionError("Unable to replace the anvil screen", exception);
		}
	}

}