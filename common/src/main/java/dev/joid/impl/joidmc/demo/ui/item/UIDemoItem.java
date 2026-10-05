package dev.joid.impl.joidmc.demo.ui.item;

import be.zeldown.joid.demo.ui.UIDemo;
import be.zeldown.joid.lib.ui.node.impl.structure.flex.FlexNode;
import dev.joid.impl.joidmc.lib.ui.node.impl.design.item.ItemNode;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class UIDemoItem extends UIDemo {

	@Override
	public void init() {
		FlexNode
		.horizontal(1920D / 2D - 270D / 2D, 1080D / 2D - 50D, 100D)
		.margin(10D)
		.body(flex -> {
			final ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
			sword.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			final ItemStack block = new ItemStack(Blocks.GRASS_BLOCK);
			block.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			ItemNode.create(0D, 0D, 100D).item(Items.DIAMOND_SWORD).attach(flex);
			ItemNode.create(0D, 0D, 100D).item(Blocks.GRASS_BLOCK).attach(flex);
			ItemNode.create(0D, 0D, 100D).stack(sword).attach(flex);
			ItemNode.create(0D, 0D, 100D).stack(block).attach(flex);
			ItemNode.create(0D, 0D, 100D).item(Items.SHIELD).attach(flex);
		})
		.attach(this);
	}

}