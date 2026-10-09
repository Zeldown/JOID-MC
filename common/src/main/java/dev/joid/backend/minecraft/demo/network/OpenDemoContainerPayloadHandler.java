package dev.joid.backend.minecraft.demo.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.joid.backend.minecraft.demo.container.DemoContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenDemoContainerPayloadHandler {

	private static final Map<UUID, SimpleContainer> STORAGES = new HashMap<>();

	public static void handle(final OpenDemoContainerPayload payload, final ServerPlayer player) {
		if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && !player.level().getServer().isSingleplayerOwner(player.nameAndId())) {
			return;
		}

		final SimpleContainer storage = OpenDemoContainerPayloadHandler.STORAGES.computeIfAbsent(player.getUUID(), _ -> new SimpleContainer(27));
		player.openMenu(new SimpleMenuProvider((containerId, inventory, _) -> new DemoContainer(containerId, inventory, storage), Component.literal("Demo storage")));
	}

}