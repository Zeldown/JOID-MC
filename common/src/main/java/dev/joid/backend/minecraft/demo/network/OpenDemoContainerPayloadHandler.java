package dev.joid.backend.minecraft.demo.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.joid.backend.minecraft.demo.container.DemoContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.server.MinecraftServer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenDemoContainerPayloadHandler {

	private static final Map<UUID, InventoryBasic> STORAGES = new HashMap<>();

	public static void handle(final OpenDemoContainerPayload payload, final EntityPlayerMP player) {
		if (OpenDemoContainerPayloadHandler.canOpen(player)) {
			DemoContainer.TYPE.open(player);
		}
	}

	public static boolean canOpen(final EntityPlayerMP player) {
		final MinecraftServer server = MinecraftServer.getServer();
		return server.getConfigurationManager().func_152596_g(player.getGameProfile()) || server.isSinglePlayer() && player.getCommandSenderName().equals(server.getServerOwner());
	}

	public static InventoryBasic getStorage(final EntityPlayer player) {
		return OpenDemoContainerPayloadHandler.STORAGES.computeIfAbsent(player.getUniqueID(), uuid -> new InventoryBasic("Demo storage", true, 27));
	}

}