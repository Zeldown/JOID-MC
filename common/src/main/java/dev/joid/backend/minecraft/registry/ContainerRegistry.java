package dev.joid.backend.minecraft.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContainerRegistry {

	private static final List<ContainerDeclaration<?>> DECLARATIONS = new ArrayList<>();

	public static <M extends AbstractContainerMenu> void register(final Identifier id, final MenuType<M> type, final Function<M, ? extends ContainerUI<M>> ui) {
		ContainerRegistry.DECLARATIONS.add(new ContainerDeclaration<>(id, type, ui));
	}

	public static List<ContainerDeclaration<?>> getDeclarations() {
		return Collections.unmodifiableList(ContainerRegistry.DECLARATIONS);
	}

}