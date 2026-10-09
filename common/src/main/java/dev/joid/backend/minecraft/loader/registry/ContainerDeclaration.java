package dev.joid.backend.minecraft.loader.registry;

import java.util.function.Function;

import dev.joid.backend.minecraft.lib.ui.core.container.ContainerUI;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public record ContainerDeclaration<M extends AbstractContainerMenu>(Identifier id, MenuType<M> type, Function<M, ? extends ContainerUI<M>> ui) {

}