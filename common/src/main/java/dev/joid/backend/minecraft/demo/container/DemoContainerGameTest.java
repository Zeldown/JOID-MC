package dev.joid.backend.minecraft.demo.container;

import java.util.Optional;

import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayload;
import dev.joid.backend.minecraft.demo.network.OpenDemoContainerPayloadHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoContainerGameTest {

	@SuppressWarnings("removal")
	public static void run(final GameTestHelper helper) {
		final ServerPlayer player = helper.makeMockServerPlayerInLevel();
		OpenDemoContainerPayloadHandler.handle(OpenDemoContainerPayload.INSTANCE, player);
		helper.assertTrue(!(player.containerMenu instanceof DemoContainer), "The demo container opens for a player who is not an operator");

		player.level().getServer().getPlayerList().op(player.nameAndId(), Optional.of(LevelBasedPermissionSet.MODERATOR), Optional.empty());
		OpenDemoContainerPayloadHandler.handle(OpenDemoContainerPayload.INSTANCE, player);
		helper.assertTrue(!(player.containerMenu instanceof DemoContainer), "The demo container opens for an operator of level 1");

		player.level().getServer().getPlayerList().op(player.nameAndId(), Optional.of(LevelBasedPermissionSet.GAMEMASTER), Optional.empty());
		OpenDemoContainerPayloadHandler.handle(OpenDemoContainerPayload.INSTANCE, player);
		helper.assertTrue(player.containerMenu instanceof DemoContainer, "The demo container does not open for an operator of level 2");

		final DemoContainer container = (DemoContainer) player.containerMenu;
		helper.assertValueEqual(container.slots.size(), 63, "slot count");
		container.getSlot(0).set(new ItemStack(Items.DIAMOND, 5));
		container.quickMoveStack(player, 0);
		helper.assertTrue(container.getSlot(0).getItem().isEmpty(), "Shift-clicking a storage slot leaves its stack in the storage");
		helper.assertValueEqual(container.getSlot(62).getItem().getCount(), 5, "diamonds moved to the last hotbar slot");

		container.quickMoveStack(player, 62);
		helper.assertValueEqual(container.getSlot(0).getItem().getCount(), 5, "diamonds moved back to the first storage slot");

		container.clicked(0, 0, ContainerInput.PICKUP, player);
		helper.assertValueEqual(container.getCarried().getCount(), 5, "carried diamonds after a left click");
		container.clicked(1, 1, ContainerInput.PICKUP, player);
		helper.assertValueEqual(container.getSlot(1).getItem().getCount(), 1, "diamonds placed by a right click");
		helper.assertValueEqual(container.getCarried().getCount(), 4, "carried diamonds after a right click");

		player.closeContainer();
		helper.assertValueEqual(player.getInventory().countItem(Items.DIAMOND), 4, "carried diamonds given back when the container closes");
		helper.assertValueEqual(container.getStorage().getItem(1).getCount(), 1, "diamonds kept in the demo storage");
		helper.succeed();
	}

}