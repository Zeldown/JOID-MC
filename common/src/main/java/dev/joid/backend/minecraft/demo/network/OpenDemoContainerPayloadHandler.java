package dev.joid.backend.minecraft.demo.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.joid.backend.minecraft.demo.container.DemoContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenDemoContainerPayloadHandler {

	private static final Map<UUID, SimpleContainer> STORAGES = new HashMap<>();

	public static void handle(final OpenDemoContainerPayload payload, final ServerPlayer player) {
		if (!OpenDemoContainerPayloadHandler.canOpen(player)) {
			return;
		}

		final SimpleContainer storage = OpenDemoContainerPayloadHandler.STORAGES.computeIfAbsent(player.getUUID(), uuid -> new SimpleContainer(27));
		player.openMenu(new SimpleMenuProvider((containerId, inventory, owner) -> new DemoContainer(containerId, inventory, storage), new TextComponent("Demo storage")));
	}

	public static boolean canOpen(final ServerPlayer player) {
		return player.hasPermissions(2) || player.server.isSingleplayerOwner(player.getGameProfile());
	}

}