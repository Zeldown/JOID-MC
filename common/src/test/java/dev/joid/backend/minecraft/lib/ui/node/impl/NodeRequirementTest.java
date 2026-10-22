package dev.joid.backend.minecraft.lib.ui.node.impl;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode;
import dev.joid.lib.ui.node.Node;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class NodeRequirementTest {

	@BeforeClass
	public static void bootstrap() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	public void refusesABlockNodeWithoutBlock() {
		NodeRequirementTest.assertRefused(BlockNode.create(0, 0, 80, 80), "The BlockNode has no block, call block(...) before attaching it");
	}

	@Test
	public void acceptsABlockNodeWithABlock() throws ReflectiveOperationException {
		NodeRequirementTest.checkRequirements(BlockNode.create(0, 0, 80, 80).block(Blocks.STONE.defaultBlockState()));
	}

	@Test
	public void refusesAnItemNodeWithoutStack() {
		NodeRequirementTest.assertRefused(ItemNode.create(0, 0, 80, 80), "The ItemNode has no stack, call stack(...) before attaching it");
	}

	@Test
	public void acceptsAnItemNodeWithAnEmptyStack() throws ReflectiveOperationException {
		NodeRequirementTest.checkRequirements(ItemNode.create(0, 0, 80, 80).stack(ItemStack.EMPTY));
		NodeRequirementTest.checkRequirements(ItemNode.create(0, 0, 80, 80).stack(() -> ItemStack.EMPTY));
	}

	@Test
	public void refusesAnEntityNodeWithoutSource() {
		NodeRequirementTest.assertRefused(EntityNode.create(0, 0, 80, 160), "The EntityNode has no entity, call entity(...), skin(...), type(...) or profile(...) before attaching it");
	}

	@Test
	public void acceptsAnEntityNodeWithAType() throws ReflectiveOperationException {
		NodeRequirementTest.checkRequirements(EntityNode.create(0, 0, 80, 160).type(EntityTypes.PIG));
	}

	@Test
	public void acceptsAnEntityNodeFollowingAMissingEntity() throws ReflectiveOperationException {
		NodeRequirementTest.checkRequirements(EntityNode.create(0, 0, 80, 160).entity(() -> null));
	}

	@Test
	public void refusesASlotNodeWithoutSlot() {
		NodeRequirementTest.assertRefused(SlotNode.create(0, 0, 72, 72), "The SlotNode has no slot, call slot(...) before attaching it");
	}

	@Test
	public void acceptsASlotNodeWithASlot() throws ReflectiveOperationException {
		NodeRequirementTest.checkRequirements(SlotNode.create(0, 0, 72, 72).slot(new Slot(new SimpleContainer(1), 0, 0, 0)));
	}

	private static void assertRefused(final Node node, final String message) {
		try {
			NodeRequirementTest.checkRequirements(node);
			Assert.fail("The " + node.getClass().getSimpleName() + " was accepted without its setting");
		} catch (final IllegalStateException exception) {
			Assert.assertEquals(message, exception.getMessage());
		} catch (final ReflectiveOperationException exception) {
			throw new AssertionError(exception);
		}
	}

	private static void checkRequirements(final Node node) throws ReflectiveOperationException {
		final Field field = Node.class.getDeclaredField("requirementList");
		field.setAccessible(true);
		for (final Runnable requirement : (List<Runnable>) field.get(node)) {
			requirement.run();
		}
	}

}